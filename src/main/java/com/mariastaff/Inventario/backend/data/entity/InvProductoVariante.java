package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inv_producto_variante")
public class InvProductoVariante extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private InvProducto producto;

    private String nombreVariante;
    private String codigoBarras;
    private String codigoInternoVariante;
    private Boolean activo = true;

    public InvProducto getProducto() {
        return producto;
    }

    public void setProducto(InvProducto producto) {
        this.producto = producto;
    }

    public String getNombreVariante() {
        return nombreVariante;
    }

    public void setNombreVariante(String nombreVariante) {
        this.nombreVariante = nombreVariante;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getCodigoInternoVariante() {
        return codigoInternoVariante;
    }

    public void setCodigoInternoVariante(String codigoInternoVariante) {
        this.codigoInternoVariante = codigoInternoVariante;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
