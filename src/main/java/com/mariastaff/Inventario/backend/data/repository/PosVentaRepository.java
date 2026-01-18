package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosVentaRepository extends JpaRepository<PosVenta, Long> {
    java.util.List<PosVenta> findByEstadoPagoNot(String estadoPago);
    
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT v FROM PosVenta v LEFT JOIN FETCH v.detalles d LEFT JOIN FETCH d.productoVariante pv")
    java.util.List<PosVenta> findAllWithDetails();
    @org.springframework.data.jpa.repository.Query("SELECT SUM(v.totalNeto) FROM PosVenta v WHERE v.turno = :turno AND v.estado = 'CERRADO'")
    java.math.BigDecimal sumTotalNetoByTurno(@org.springframework.data.repository.query.Param("turno") com.mariastaff.Inventario.backend.data.entity.PosTurno turno);
}
