package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.ContAsientoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mariastaff.Inventario.backend.data.entity.ContAsiento;
import java.util.List;

public interface ContAsientoDetalleRepository extends JpaRepository<ContAsientoDetalle, Long> {
    List<ContAsientoDetalle> findByAsiento(ContAsiento asiento);
}
