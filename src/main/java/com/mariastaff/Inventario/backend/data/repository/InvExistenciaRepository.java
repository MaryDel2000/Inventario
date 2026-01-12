package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.InvExistencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvExistenciaRepository extends JpaRepository<InvExistencia, Long> {


    @org.springframework.data.jpa.repository.Query("SELECT SUM(e.cantidadDisponible) FROM InvExistencia e WHERE e.productoVariante.producto.id = :productoId")
    java.math.BigDecimal sumStockByProducto(@org.springframework.data.repository.query.Param("productoId") Long productoId);

    java.util.Optional<InvExistencia> findByAlmacenAndProductoVarianteAndUbicacionAndLote(
        com.mariastaff.Inventario.backend.data.entity.InvAlmacen almacen,
        com.mariastaff.Inventario.backend.data.entity.InvProductoVariante productoVariante,
        com.mariastaff.Inventario.backend.data.entity.InvUbicacion ubicacion,
        com.mariastaff.Inventario.backend.data.entity.InvLote lote
    );
}
