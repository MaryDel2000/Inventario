package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.GenMoneda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenMonedaRepository extends JpaRepository<GenMoneda, Long> {
    java.util.Optional<GenMoneda> findByCodigo(String codigo);
}
