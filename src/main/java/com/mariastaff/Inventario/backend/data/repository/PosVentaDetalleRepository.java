package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.PosVentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosVentaDetalleRepository extends JpaRepository<PosVentaDetalle, Long> {
}
