package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvImpuesto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvImpuestoRepository extends JpaRepository<InvImpuesto, Long> {
}
