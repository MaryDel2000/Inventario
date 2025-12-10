package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.PosCliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosClienteRepository extends JpaRepository<PosCliente, Long> {
}
