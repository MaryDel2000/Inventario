package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "cont_periodo")
public class ContPeriodo extends AbstractEntity {

    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    
    @ManyToOne
    @JoinColumn(name = "usuario_cierre_id")
    private SysUsuario usuarioCierre;
    
    private LocalDateTime fechaCierre;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime f) { this.fechaInicio = f; }
    
    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime f) { this.fechaFin = f; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public SysUsuario getUsuarioCierre() { return usuarioCierre; }
    public void setUsuarioCierre(SysUsuario u) { this.usuarioCierre = u; }
    
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime f) { this.fechaCierre = f; }
}
