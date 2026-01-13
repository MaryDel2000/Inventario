package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.*;
import com.mariastaff.Inventario.backend.data.repository.*;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductoService {

    private final InvProductoRepository repository;
    private final InvProductoVarianteRepository varianteRepository;
    private final InvLoteRepository loteRepository;
    private final InvPrecioVentaRepository precioVentaRepository;
    private final InvExistenciaRepository existenciaRepository;
    private final InvListaPrecioRepository listaPrecioRepository;

    public ProductoService(InvProductoRepository repository, 
                           InvProductoVarianteRepository varianteRepository,
                           InvLoteRepository loteRepository,
                           InvExistenciaRepository existenciaRepository,
                           InvPrecioVentaRepository precioVentaRepository,
                           InvListaPrecioRepository listaPrecioRepository) {
        this.repository = repository;
        this.varianteRepository = varianteRepository;
        this.loteRepository = loteRepository;
        this.existenciaRepository = existenciaRepository;
        this.precioVentaRepository = precioVentaRepository;
        this.listaPrecioRepository = listaPrecioRepository;
    }

    public java.util.Optional<InvProducto> findById(Long id) {
        return repository.findById(id);
    }

    public List<InvProducto> findAll() {
        return repository.findAll();
    }
    
    public List<InvProducto> search(InvCategoria categoria, InvUnidadMedida uom, Boolean activo) {
        return search(categoria, uom, activo, null);
    }

    public List<InvProducto> search(InvCategoria categoria, InvUnidadMedida uom, Boolean activo, InvAlmacen almacen) {
        return repository.search(categoria, uom, activo, almacen);
    }
    
    public InvProducto save(InvProducto producto) {
        return repository.save(producto);
    }

    @Transactional
    public InvProducto createProductWithInitialBatch(InvProducto product, InvUbicacion location, BigDecimal initialStock, String batchCode, java.time.LocalDateTime expiryDate, String batchObservations, BigDecimal initialPrice) {
        // 1. Save Product
        InvProducto savedProduct = repository.save(product);

        // 2. Create Default Variant
        InvProductoVariante defaultVariant = new InvProductoVariante();
        defaultVariant.setProducto(savedProduct);
        defaultVariant.setNombreVariante(savedProduct.getNombre());
        defaultVariant.setCodigoInternoVariante(savedProduct.getCodigoInterno());
        defaultVariant.setActivo(true);
        InvProductoVariante savedVariant = varianteRepository.save(defaultVariant);
        
        // 2.1 Create Initial Price if provided
        if (initialPrice != null && initialPrice.compareTo(BigDecimal.ZERO) > 0) {
            updatePrecioVenta(savedVariant, initialPrice);
        }

        InvLote newLote = null;
        // 3. Create Initial Batch (if code provided)
        if (batchCode != null && !batchCode.trim().isEmpty()) {
            newLote = new InvLote();
            newLote.setProductoVariante(savedVariant);
            newLote.setCodigoLote(batchCode);
            newLote.setFechaCaducidad(expiryDate);
            newLote.setObservaciones(batchObservations);
            newLote = loteRepository.save(newLote);
        }

        // 4. Create Initial Stock (if location provided)
        if (location != null) {
            InvExistencia existencia = new InvExistencia();
            existencia.setAlmacen(location.getAlmacen());
            existencia.setUbicacion(location);
            existencia.setProductoVariante(savedVariant);
            existencia.setLote(newLote); 
            existencia.setCantidadDisponible(initialStock != null ? initialStock : BigDecimal.ZERO); 
            existencia.setFechaUltimaActualizacion(java.time.LocalDateTime.now());
            existenciaRepository.save(existencia);
        }

        return savedProduct;
    }
    
    // Kept for backward compatibility if needed, but updated to call the main method with null price
    public InvProducto createProductWithInitialBatch(InvProducto product, InvUbicacion location, BigDecimal initialStock, String batchCode, java.time.LocalDateTime expiryDate, String batchObservations) {
        return createProductWithInitialBatch(product, location, initialStock, batchCode, expiryDate, batchObservations, null);
    }
    
    public void deleteProducto(InvProducto entity) {
        // Find all variants
        List<InvProductoVariante> variantes = varianteRepository.findByProducto(entity);
        for (InvProductoVariante variante : variantes) {
            deleteVariante(variante);
        }
        repository.delete(entity);
    }
    public long countProductos() { return repository.count(); }

    // Variantes
    public List<InvProductoVariante> findAllVariantes() {
        return varianteRepository.findAll();
    }
    
    public List<InvProductoVariante> findAllVariantesWithProducto() {
        return varianteRepository.findAllWithProducto();
    }
    
    public List<InvProductoVariante> findVariantesByProducto(InvProducto producto) {
        return varianteRepository.findByProducto(producto);
    }

    public InvProductoVariante saveVariante(InvProductoVariante variante) {
        return varianteRepository.save(variante);
    }

    public void deleteVariante(InvProductoVariante variante) {
        // Find all batches (lotes) for this variant
        List<InvLote> lotes = loteRepository.findByProductoVariante(variante);
        loteRepository.deleteAll(lotes);
        varianteRepository.delete(variante);
    }

    public BigDecimal getStockTotal(InvProductoVariante variante) {
        List<InvExistencia> existencias = existenciaRepository.findByProductoVariante(variante);
        return existencias.stream()
                .map(InvExistencia::getCantidadDisponible)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPrecioVentaActual(InvProductoVariante variante) {
        List<InvPrecioVenta> precios = precioVentaRepository.findByProductoVariante(variante);
        // Logic to pick the best price (e.g., active, recent). For now, return the first active one or ZERO.
        return precios.stream()
                .filter(p -> p.getListaPrecio() != null && p.getListaPrecio().getActivo()) 
                .map(InvPrecioVenta::getPrecioVenta)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
    
    @Transactional
    public void updatePrecioVenta(InvProductoVariante variante, BigDecimal precio) {
        InvListaPrecio listaGeneral = listaPrecioRepository.findByCodigo("GENERAL");
        if (listaGeneral == null) {
            listaGeneral = new InvListaPrecio();
            listaGeneral.setCodigo("GENERAL");
            listaGeneral.setNombre("Lista General de Precios");
            listaGeneral.setActivo(true);
            listaGeneral = listaPrecioRepository.save(listaGeneral);
        }
        
        final InvListaPrecio listaFinal = listaGeneral;
        
        // 2. Check if price exists for this variant and list
        List<InvPrecioVenta> precios = precioVentaRepository.findByProductoVariante(variante);
        InvPrecioVenta precioVenta = precios.stream()
            .filter(p -> p.getListaPrecio().getId().equals(listaFinal.getId()))
            .findFirst()
            .orElse(new InvPrecioVenta());
            
        precioVenta.setProductoVariante(variante);
        precioVenta.setListaPrecio(listaFinal);
        precioVenta.setPrecioVenta(precio);
        precioVenta.setFechaInicioVigencia(java.time.LocalDateTime.now());
        
        precioVentaRepository.save(precioVenta);
    }
}
