package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_configuracion")
public class SysConfiguracion extends AbstractEntity { // Custom audit fields in SQL, but close enough to AuditableEntity if mapped?
    // SQL: usuario_creacion_id, fecha_creacion, usuario_modificacion_id, fecha_ultima_actualizacion
    // AuditableEntity: ... fecha_modificacion.
    // I need to be careful with 'fecha_ultima_actualizacion'.
    
    @ManyToOne
    @JoinColumn(name = "moneda_default_id")
    private GenMoneda monedaDefault;
    
    private BigDecimal ivaPorcentajeDefault;
    
    private Long usuarioCreacionId;
    private LocalDateTime fechaCreacion;
    private Long usuarioModificacionId;
    private LocalDateTime fechaUltimaActualizacion;

    private String nombreEmpresa;
    private String direccion;
    private String telefono;
    private String nit;

    public GenMoneda getMonedaDefault() { return monedaDefault; }
    public void setMonedaDefault(GenMoneda monedaDefault) { this.monedaDefault = monedaDefault; }
    
    public BigDecimal getIvaPorcentajeDefault() { return ivaPorcentajeDefault; }
    public void setIvaPorcentajeDefault(BigDecimal ivaPorcentajeDefault) { this.ivaPorcentajeDefault = ivaPorcentajeDefault; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    
    public Long getUsuarioCreacionId() { return usuarioCreacionId; }
    public void setUsuarioCreacionId(Long u) { this.usuarioCreacionId = u; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime f) { this.fechaCreacion = f; }
    
    public Long getUsuarioModificacionId() { return usuarioModificacionId; }
    public void setUsuarioModificacionId(Long u) { this.usuarioModificacionId = u; }
    
    public LocalDateTime getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
    public void setFechaUltimaActualizacion(LocalDateTime f) { this.fechaUltimaActualizacion = f; }
}
