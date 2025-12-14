package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvExistencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvExistenciaRepository extends JpaRepository<InvExistencia, Long> {
}
