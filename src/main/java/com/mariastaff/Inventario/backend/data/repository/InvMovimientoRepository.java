package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvMovimientoRepository extends JpaRepository<InvMovimiento, Long> {
}
