package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.GenEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenEntidadRepository extends JpaRepository<GenEntidad, Long> {
}
