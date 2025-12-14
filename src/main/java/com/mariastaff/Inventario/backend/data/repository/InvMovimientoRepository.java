package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvMovimientoRepository extends JpaRepository<InvMovimiento, Long> {

    @org.springframework.data.jpa.repository.Query("SELECT CAST(m.fechaMovimiento AS LocalDate), COUNT(m) FROM InvMovimiento m GROUP BY CAST(m.fechaMovimiento AS LocalDate) ORDER BY CAST(m.fechaMovimiento AS LocalDate)")
    java.util.List<Object[]> findDailyMovements();
}
