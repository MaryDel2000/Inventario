package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "pos_caja_usuario")
public class PosCajaUsuario extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "caja_id")
    private PosCaja caja;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private SysUsuario usuario;
    
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean activo = true;

    public PosCaja getCaja() { return caja; }
    public void setCaja(PosCaja caja) { this.caja = caja; }
    
    public SysUsuario getUsuario() { return usuario; }
    public void setUsuario(SysUsuario usuario) { this.usuario = usuario; }
    
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime f) { this.fechaInicio = f; }
    
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime f) { this.fechaFin = f; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
