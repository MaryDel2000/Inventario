package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "inv_movimiento")
public class InvMovimiento extends AuditableEntity {

    private String tipoMovimiento;
    
    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;
    
    @ManyToOne
    @JoinColumn(name = "almacen_origen_id")
    private InvAlmacen almacenOrigen;
    
    @ManyToOne
    @JoinColumn(name = "almacen_destino_id")
    private InvAlmacen almacenDestino;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaMovimiento = LocalDateTime.now();
    
    private String referenciaTipo;
    private Long referenciaId;
    private String observaciones;

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
    
    public GenSucursal getSucursal() { return sucursal; }
    public void setSucursal(GenSucursal sucursal) { this.sucursal = sucursal; }
    
    public InvAlmacen getAlmacenOrigen() { return almacenOrigen; }
    public void setAlmacenOrigen(InvAlmacen almacenOrigen) { this.almacenOrigen = almacenOrigen; }
    
    public InvAlmacen getAlmacenDestino() { return almacenDestino; }
    public void setAlmacenDestino(InvAlmacen almacenDestino) { this.almacenDestino = almacenDestino; }
    
    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    
    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }
    
    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
