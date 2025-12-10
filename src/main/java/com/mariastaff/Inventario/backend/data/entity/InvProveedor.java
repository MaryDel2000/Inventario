package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inv_proveedor")
public class InvProveedor extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "entidad_id")
    private GenEntidad entidad;

    private Boolean activo = true;

    public GenEntidad getEntidad() {
        return entidad;
    }

    public void setEntidad(GenEntidad entidad) {
        this.entidad = entidad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
