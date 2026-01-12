package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvMovimientoDetalleRepository extends JpaRepository<InvMovimientoDetalle, Long> {

    java.util.List<InvMovimientoDetalle> findByMovimiento(com.mariastaff.Inventario.backend.data.entity.InvMovimiento movimiento);
}
