package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "cont_asiento")
public class ContAsiento extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "periodo_id")
    private ContPeriodo periodo;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fecha = LocalDateTime.now();
    
    private String descripcion;
    private String origen;
    private Long origenId;
    
    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;
    
    @ManyToOne
    @JoinColumn(name = "usuario_registro_id")
    private SysUsuario usuarioRegistro;
    
    private LocalDateTime fechaCreacion;

    public ContPeriodo getPeriodo() { return periodo; }
    public void setPeriodo(ContPeriodo periodo) { this.periodo = periodo; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime f) { this.fecha = f; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }
    
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    
    public Long getOrigenId() { return origenId; }
    public void setOrigenId(Long origenId) { this.origenId = origenId; }
    
    public GenSucursal getSucursal() { return sucursal; }
    public void setSucursal(GenSucursal sucursal) { this.sucursal = sucursal; }
    
    public SysUsuario getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(SysUsuario u) { this.usuarioRegistro = u; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime f) { this.fechaCreacion = f; }
}
