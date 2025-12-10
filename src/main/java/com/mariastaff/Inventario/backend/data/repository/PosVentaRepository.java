package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosVentaRepository extends JpaRepository<PosVenta, Long> {
    java.util.List<PosVenta> findByEstadoPagoNot(String estadoPago);
}
