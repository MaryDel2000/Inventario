package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvProductoRepository extends JpaRepository<InvProducto, Long> {
    // Basic search methods can be added here
}
