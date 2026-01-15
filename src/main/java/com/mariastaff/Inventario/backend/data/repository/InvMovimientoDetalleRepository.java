package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvMovimientoDetalleRepository extends JpaRepository<InvMovimientoDetalle, Long> {

    java.util.List<InvMovimientoDetalle> findByMovimiento(com.mariastaff.Inventario.backend.data.entity.InvMovimiento movimiento);

    @org.springframework.data.jpa.repository.Query("SELECT d FROM InvMovimientoDetalle d " +
            "JOIN FETCH d.movimiento m " +
            "LEFT JOIN FETCH m.almacenOrigen " +
            "LEFT JOIN FETCH m.almacenDestino " +
            "LEFT JOIN FETCH d.productoVariante pv " +
            "LEFT JOIN FETCH pv.producto " +
            "LEFT JOIN FETCH d.lote " +
            "LEFT JOIN FETCH d.ubicacionOrigen " +
            "LEFT JOIN FETCH d.ubicacionDestino " +
            "WHERE m.fechaMovimiento BETWEEN :start AND :end")
    java.util.List<InvMovimientoDetalle> findDetallesByDateRange(
            @org.springframework.data.repository.query.Param("start") java.time.LocalDateTime start, 
            @org.springframework.data.repository.query.Param("end") java.time.LocalDateTime end);
}
