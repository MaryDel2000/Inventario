package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvLote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvLoteRepository extends JpaRepository<InvLote, Long> {
}
