package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvCompra;
import com.mariastaff.Inventario.backend.data.entity.InvCompraDetalle;
import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.data.entity.InvMovimiento;
import com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle;
import com.mariastaff.Inventario.backend.data.entity.InvProveedor;
import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
import com.mariastaff.Inventario.backend.data.repository.InvCompraDetalleRepository;
import com.mariastaff.Inventario.backend.data.repository.InvCompraRepository;
import com.mariastaff.Inventario.backend.data.repository.InvLoteRepository;
import com.mariastaff.Inventario.backend.data.repository.InvProveedorRepository;
import com.mariastaff.Inventario.backend.data.repository.InvUbicacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompraService {

    private final InvCompraRepository compraRepository;
    private final InvCompraDetalleRepository compraDetalleRepository;
    private final InvProveedorRepository proveedorRepository;
    private final InvLoteRepository loteRepository;
    private final MovimientoService movimientoService;
    private final InvUbicacionRepository ubicacionRepository;

    public CompraService(InvCompraRepository compraRepository, 
                         InvCompraDetalleRepository compraDetalleRepository,
                         InvProveedorRepository proveedorRepository,
                         InvLoteRepository loteRepository,
                         MovimientoService movimientoService,
                         InvUbicacionRepository ubicacionRepository) {
        this.compraRepository = compraRepository;
        this.compraDetalleRepository = compraDetalleRepository;
        this.proveedorRepository = proveedorRepository;
        this.loteRepository = loteRepository;
        this.movimientoService = movimientoService;
        this.ubicacionRepository = ubicacionRepository;
    }

    public List<InvCompra> findAllCompras() { return compraRepository.findAll(); }
    public InvCompra saveCompra(InvCompra entity) { return compraRepository.save(entity); }
    public void deleteCompra(InvCompra entity) { compraRepository.delete(entity); }

    public List<InvProveedor> findAllProveedores() { return proveedorRepository.findAll(); }
    public InvProveedor saveProveedor(InvProveedor entity) { return proveedorRepository.save(entity); }
    public void deleteProveedor(InvProveedor entity) { proveedorRepository.delete(entity); }
    
    @Transactional
    public void saveCompraWithDetails(InvCompra compra, List<InvCompraDetalle> detalles) {
        // 1. Save Header
        InvCompra savedCompra = compraRepository.save(compra);
        
        // 2. Prepare Movement
        InvMovimiento movimiento = new InvMovimiento();
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setFechaMovimiento(compra.getFechaCompra() != null ? compra.getFechaCompra() : java.time.LocalDateTime.now());
        movimiento.setAlmacenDestino(compra.getAlmacenDestino());
        movimiento.setObservaciones("Generado por Compra " + (compra.getNumeroDocumento() != null ? compra.getNumeroDocumento() : ""));
        
        List<InvMovimientoDetalle> movDetalles = new ArrayList<>();
        
        // Find a default location for the warehouse just in case
        InvUbicacion defaultLocation = null;
        if (compra.getAlmacenDestino() != null) {
            defaultLocation = ubicacionRepository.findByAlmacen(compra.getAlmacenDestino()).stream().findFirst().orElse(null);
        }

        // 3. Process Items
        for (InvCompraDetalle det : detalles) {
            det.setCompra(savedCompra);
            
            // Generate Batch
            InvLote lote = new InvLote();
            lote.setProductoVariante(det.getProductoVariante());
            String dateCode = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            // Simple batch code generation: VAR_ID - DATE - RANDOM
            lote.setCodigoLote("L-" + det.getProductoVariante().getId() + "-" + dateCode + "-" + (int)(Math.random()*1000));
            lote.setFechaCaducidad(det.getFechaCaducidad());
            lote.setObservaciones("Compra #" + savedCompra.getId());
            
            InvLote savedLote = loteRepository.save(lote);
            
            compraDetalleRepository.save(det);
            
            // Prepare Movement Detail
            InvMovimientoDetalle movDet = new InvMovimientoDetalle();
            movDet.setProductoVariante(det.getProductoVariante());
            movDet.setCantidad(det.getCantidad());
            movDet.setLote(savedLote);
            // Use default location if we can't get specific one from UI yet (UI passed it? No, we skipped it in entity)
            // Ideally we should pass it. For now, use default.
            movDet.setUbicacionDestino(defaultLocation);
            
            movDetalles.add(movDet);
        }
        
        // 4. Create Movement (updates stock)
        movimientoService.crearMovimiento(movimiento, movDetalles);
    }
}
