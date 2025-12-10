package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_existencia")
public class InvExistencia extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private InvAlmacen almacen;
    
    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private InvUbicacion ubicacion;
    
    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;
    
    @ManyToOne
    @JoinColumn(name = "lote_id")
    private InvLote lote;
    
    private BigDecimal cantidadDisponible;
    private LocalDateTime fechaUltimaActualizacion;

    public InvAlmacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(InvAlmacen almacen) {
        this.almacen = almacen;
    }

    public InvUbicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(InvUbicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public InvProductoVariante getProductoVariante() {
        return productoVariante;
    }

    public void setProductoVariante(InvProductoVariante productoVariante) {
        this.productoVariante = productoVariante;
    }

    public InvLote getLote() {
        return lote;
    }

    public void setLote(InvLote lote) {
        this.lote = lote;
    }

    public BigDecimal getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(BigDecimal cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }
}
