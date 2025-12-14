package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inv_ubicacion")
public class InvUbicacion extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private InvAlmacen almacen;

    private String codigo;
    private String descripcion;
    private Boolean activo = true;

    public InvAlmacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(InvAlmacen almacen) {
        this.almacen = almacen;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
