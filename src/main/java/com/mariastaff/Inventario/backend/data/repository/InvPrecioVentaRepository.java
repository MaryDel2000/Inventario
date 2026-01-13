package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvPrecioVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvPrecioVentaRepository extends JpaRepository<InvPrecioVenta, Long> {
    java.util.List<InvPrecioVenta> findByProductoVariante(com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante);
}
