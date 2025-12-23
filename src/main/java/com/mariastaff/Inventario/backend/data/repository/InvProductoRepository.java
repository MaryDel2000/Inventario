package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvProductoRepository extends JpaRepository<InvProducto, Long> {
    @org.springframework.data.jpa.repository.Query("select p from InvProducto p " +
           "where (?1 is null or p.categoria = ?1) " +
           "and (?2 is null or p.unidadMedida = ?2) " +
           "and (?3 is null or p.activo = ?3)")
    java.util.List<InvProducto> search(com.mariastaff.Inventario.backend.data.entity.InvCategoria categoria, 
                                       com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida uom, 
                                       Boolean activo);
}
