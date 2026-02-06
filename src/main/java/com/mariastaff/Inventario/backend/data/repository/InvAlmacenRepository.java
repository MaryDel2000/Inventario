package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvAlmacenRepository extends JpaRepository<InvAlmacen, Long> {
    java.util.List<InvAlmacen> findByProveedor(com.mariastaff.Inventario.backend.data.entity.InvProveedor proveedor);

    java.util.List<InvAlmacen> findByTipoAlmacen(String tipoAlmacen);
}
