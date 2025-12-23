package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvProductoVarianteRepository extends JpaRepository<InvProductoVariante, Long> {
    java.util.List<InvProductoVariante> findByProducto(com.mariastaff.Inventario.backend.data.entity.InvProducto producto);
}
