package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "sys_usuario")
public class SysUsuario extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "entidad_id")
    private GenEntidad entidad;

    private String authentikUuid;
    
    @NotEmpty
    private String username;
    
    private Boolean activo = true;

    public GenEntidad getEntidad() {
        return entidad;
    }

    public void setEntidad(GenEntidad entidad) {
        this.entidad = entidad;
    }

    public String getAuthentikUuid() {
        return authentikUuid;
    }

    public void setAuthentikUuid(String authentikUuid) {
        this.authentikUuid = authentikUuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
