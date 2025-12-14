package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@MappedSuperclass
public abstract class AuditableEntity extends AbstractEntity {

    private Long usuarioCreacionId;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private Long usuarioModificacionId;
    
    private LocalDateTime fechaModificacion;

    public Long getUsuarioCreacionId() {
        return usuarioCreacionId;
    }

    public void setUsuarioCreacionId(Long usuarioCreacionId) {
        this.usuarioCreacionId = usuarioCreacionId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getUsuarioModificacionId() {
        return usuarioModificacionId;
    }

    public void setUsuarioModificacionId(Long usuarioModificacionId) {
        this.usuarioModificacionId = usuarioModificacionId;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }
}
