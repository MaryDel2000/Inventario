package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.PosTurno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosTurnoRepository extends JpaRepository<PosTurno, Long> {
    java.util.List<PosTurno> findTop7ByEstadoOrderByFechaHoraCierreDesc(String estado);
}
