package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvMovimiento;
import com.mariastaff.Inventario.backend.data.repository.InvMovimientoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.math.BigDecimal;

@Service
public class MovimientoService {


    private final InvMovimientoRepository movimientoRepository;
    private final com.mariastaff.Inventario.backend.data.repository.InvMovimientoDetalleRepository detalleRepository;
    private final com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository existenciaRepository;

    public MovimientoService(InvMovimientoRepository movimientoRepository,
                             com.mariastaff.Inventario.backend.data.repository.InvMovimientoDetalleRepository detalleRepository,
                             com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository existenciaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.detalleRepository = detalleRepository;
        this.existenciaRepository = existenciaRepository;
    }

    public List<InvMovimiento> findAllMovimientos() { return movimientoRepository.findAll(); }
    public List<com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle> findDetallesByMovimiento(InvMovimiento m) { return detalleRepository.findByMovimiento(m); }
    public InvMovimiento saveMovimiento(InvMovimiento entity) { return movimientoRepository.save(entity); }
    public void deleteMovimiento(InvMovimiento entity) { movimientoRepository.delete(entity); }
    public long countMovimientos() { return movimientoRepository.count(); }

    public java.util.Map<java.time.LocalDate, Long> getDailyMovements() {
        return movimientoRepository.findDailyMovements().stream()
            .collect(java.util.stream.Collectors.toMap(
                row -> (java.time.LocalDate) row[0],
                row -> (Long) row[1]
            ));
    }

    @org.springframework.transaction.annotation.Transactional
    public void crearMovimiento(InvMovimiento movimiento, List<com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle> detalles) {
        // 0. Validate Header
        String tipo = movimiento.getTipoMovimiento();
        if ("ENTRADA".equals(tipo)) {
            if (movimiento.getAlmacenDestino() == null) {
                throw new IllegalArgumentException("Para una ENTRADA, se requiere un Almacén de Destino.");
            }
        } else if ("SALIDA".equals(tipo)) {
             if (movimiento.getAlmacenOrigen() == null) {
                 throw new IllegalArgumentException("Para una SALIDA, se requiere un Almacén de Origen.");
             }
        } else if ("TRASPASO".equals(tipo)) {
             if (movimiento.getAlmacenOrigen() == null || movimiento.getAlmacenDestino() == null) {
                 throw new IllegalArgumentException("Para un TRASPASO, se requieren Almacén de Origen y de Destino.");
             }
        } else {
            throw new IllegalArgumentException("Tipo de movimiento no válido: " + tipo);
        }

        // 1. Save Header
        movimiento = movimientoRepository.save(movimiento);

        // 2. Process Details
        for (com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle detalle : detalles) {
            detalle.setMovimiento(movimiento);
            detalleRepository.save(detalle);

            // 3. Update Inventory (Existencia)
            if ("ENTRADA".equals(tipo)) {
                updateStock(movimiento.getAlmacenDestino(), detalle.getProductoVariante(), detalle.getUbicacionDestino(), detalle.getLote(), detalle.getCantidad());
            } else if ("SALIDA".equals(tipo)) {
                updateStock(movimiento.getAlmacenOrigen(), detalle.getProductoVariante(), detalle.getUbicacionOrigen(), detalle.getLote(), detalle.getCantidad().negate());
            } else if ("TRASPASO".equals(tipo)) {
                // Out from Origin
                updateStock(movimiento.getAlmacenOrigen(), detalle.getProductoVariante(), detalle.getUbicacionOrigen(), detalle.getLote(), detalle.getCantidad().negate());
                // In to Destination
                updateStock(movimiento.getAlmacenDestino(), detalle.getProductoVariante(), detalle.getUbicacionDestino(), detalle.getLote(), detalle.getCantidad());
            }
        }
    }

    private void updateStock(com.mariastaff.Inventario.backend.data.entity.InvAlmacen almacen,
                             com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante,
                             com.mariastaff.Inventario.backend.data.entity.InvUbicacion ubicacion,
                             com.mariastaff.Inventario.backend.data.entity.InvLote lote,
                             BigDecimal cantidadDelta) {
        
        com.mariastaff.Inventario.backend.data.entity.InvExistencia existencia = existenciaRepository
            .findByAlmacenAndProductoVarianteAndUbicacionAndLote(almacen, variante, ubicacion, lote)
            .orElse(null);

        if (existencia == null) {
            if (cantidadDelta.compareTo(BigDecimal.ZERO) < 0) {
                 // Check if user allows negative stock. Assuming STRICT correctness for now.
                 throw new IllegalArgumentException("Stock no encontrado para este producto en la ubicación seleccionada. Verifique el inventario.");
            } else {
                 existencia = new com.mariastaff.Inventario.backend.data.entity.InvExistencia();
                 existencia.setAlmacen(almacen);
                 existencia.setProductoVariante(variante);
                 existencia.setUbicacion(ubicacion);
                 existencia.setLote(lote);
                 existencia.setCantidadDisponible(BigDecimal.ZERO);
            }
        }

        BigDecimal nuevoSaldo = existencia.getCantidadDisponible().add(cantidadDelta);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stock insuficiente para el producto " + variante.getProducto().getNombre() + 
                                               ". Disponible: " + existencia.getCantidadDisponible() + 
                                               ", Requerido: " + cantidadDelta.abs());
        }

        existencia.setCantidadDisponible(nuevoSaldo);
        existencia.setFechaUltimaActualizacion(java.time.LocalDateTime.now());
        existenciaRepository.save(existencia);
    }
}
