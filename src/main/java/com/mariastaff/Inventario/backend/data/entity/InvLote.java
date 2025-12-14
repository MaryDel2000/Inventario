package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_lote")
public class InvLote extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;

    private String codigoLote;
    private LocalDateTime fechaCaducidad;
    private String observaciones;

    public InvProductoVariante getProductoVariante() {
        return productoVariante;
    }

    public void setProductoVariante(InvProductoVariante productoVariante) {
        this.productoVariante = productoVariante;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public LocalDateTime getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDateTime fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
