package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.repository.InvProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;


@Service
public class ProductoService {

    private final InvProductoRepository repository;
    private final com.mariastaff.Inventario.backend.data.repository.InvProductoVarianteRepository varianteRepository;
    private final com.mariastaff.Inventario.backend.data.repository.InvLoteRepository loteRepository;
    private final com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository existenciaRepository;

    public ProductoService(InvProductoRepository repository, 
                           com.mariastaff.Inventario.backend.data.repository.InvProductoVarianteRepository varianteRepository,
                           com.mariastaff.Inventario.backend.data.repository.InvLoteRepository loteRepository,
                           com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository existenciaRepository) {
        this.repository = repository;
        this.varianteRepository = varianteRepository;
        this.loteRepository = loteRepository;
        this.existenciaRepository = existenciaRepository;
    }

    public java.util.Optional<InvProducto> findById(Long id) {
        return repository.findById(id);
    }

    public List<InvProducto> findAll() {
        return repository.findAll();
    }
    
    public List<InvProducto> search(com.mariastaff.Inventario.backend.data.entity.InvCategoria categoria, 
                                    com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida uom, 
                                    Boolean activo) {
        return repository.search(categoria, uom, activo);
    }
    
    public InvProducto save(InvProducto producto) {
        return repository.save(producto);
    }

    @Transactional
    public InvProducto createProductWithInitialBatch(InvProducto product, com.mariastaff.Inventario.backend.data.entity.InvUbicacion location, java.math.BigDecimal initialStock, String batchCode, java.time.LocalDateTime expiryDate, String batchObservations) {
        // 1. Save Product
        InvProducto savedProduct = repository.save(product);

        // 2. Create Default Variant
        InvProductoVariante defaultVariant = new InvProductoVariante();
        defaultVariant.setProducto(savedProduct);
        defaultVariant.setNombreVariante(savedProduct.getNombre());
        defaultVariant.setCodigoInternoVariante(savedProduct.getCodigoInterno());
        defaultVariant.setActivo(true);
        InvProductoVariante savedVariant = varianteRepository.save(defaultVariant);

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
            com.mariastaff.Inventario.backend.data.entity.InvExistencia existencia = new com.mariastaff.Inventario.backend.data.entity.InvExistencia();
            existencia.setAlmacen(location.getAlmacen());
            existencia.setUbicacion(location);
            existencia.setProductoVariante(savedVariant);
            existencia.setLote(newLote); // Can be null if no batch created
            existencia.setCantidadDisponible(initialStock != null ? initialStock : java.math.BigDecimal.ZERO); 
            // Usually initial stock is 0 unless specified. The prompt says "create a stock register".
            // It says "The product is born with stock 0". So setting to 0 is fine, but the record must exist.
            existencia.setFechaUltimaActualizacion(java.time.LocalDateTime.now());
            existenciaRepository.save(existencia);
        }

        return savedProduct;
    }
    
    public void deleteProducto(InvProducto entity) {
        // Find all variants
        List<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> variantes = varianteRepository.findByProducto(entity);
        for (com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante : variantes) {
            deleteVariante(variante);
        }
        repository.delete(entity);
    }
    public long countProductos() { return repository.count(); }

    // Variantes
    public List<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> findAllVariantes() {
        return varianteRepository.findAll();
    }
    
    public List<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> findVariantesByProducto(InvProducto producto) {
        return varianteRepository.findByProducto(producto);
    }

    public com.mariastaff.Inventario.backend.data.entity.InvProductoVariante saveVariante(com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante) {
        return varianteRepository.save(variante);
    }

    public void deleteVariante(com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante) {
        // Find all batches (lotes) for this variant
        List<com.mariastaff.Inventario.backend.data.entity.InvLote> lotes = loteRepository.findByProductoVariante(variante);
        loteRepository.deleteAll(lotes);
        varianteRepository.delete(variante);
    }
}
