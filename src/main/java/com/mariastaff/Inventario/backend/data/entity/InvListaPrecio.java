package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_lista_precio")
public class InvListaPrecio extends AuditableEntity {

    private String nombre;
    private String descripcion;
    
    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;
    
    private Integer prioridad;
    private Boolean activo = true;
    private LocalDateTime fechaInicioVigencia;
    private LocalDateTime fechaFinVigencia;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public GenSucursal getSucursal() { return sucursal; }
    public void setSucursal(GenSucursal sucursal) { this.sucursal = sucursal; }
    
    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getFechaInicioVigencia() { return fechaInicioVigencia; }
    public void setFechaInicioVigencia(LocalDateTime f) { this.fechaInicioVigencia = f; }
    
    public LocalDateTime getFechaFinVigencia() { return fechaFinVigencia; }
    public void setFechaFinVigencia(LocalDateTime f) { this.fechaFinVigencia = f; }
}
