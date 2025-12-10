package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_costo")
public class InvCosto extends AbstractEntity { // Note: SQL only has creation audit, but extending AuditableEntity is safe (just ignored or matches). SQL has creacion only.
    // AuditableEntity has modif fields too. They will be null in DB if column doesn't exist, OR fail if JPA expects them.
    // SQL V1 created inv_costo with ONLY usuario_creacion_id, fecha_creacion.
    // AuditableEntity has usuario_modificacion_id, fecha_modificacion.
    // This will cause "Column not found" error if JPA tries to read/write them.
    // I should create a simpler base or override attribute overrides to Insertable=false, Updatable=false?
    // Or simpler: Just add the modification columns to inv_costo in SQL V1? 
    // Wait, the user said "do not change V1" implicitly? No, user said "make a plan...".
    // I already created V1. V1 is written.
    // Options:
    // 1. Modify V1 to include mod fields (easiest for consistency).
    // 2. Make InvCosto extend AbstractEntity and manually add creation fields.
    // I'll choose option 2 for safety regarding V1 stability.
    
    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;
    
    @ManyToOne
    @JoinColumn(name = "moneda_id")
    private GenMoneda moneda;
    
    private BigDecimal costoUnitario;
    private LocalDateTime fechaInicioVigencia;
    private LocalDateTime fechaFinVigencia;
    
    private Long usuarioCreacionId;
    private LocalDateTime fechaCreacion;

    public InvProductoVariante getProductoVariante() { return productoVariante; }
    public void setProductoVariante(InvProductoVariante pv) { this.productoVariante = pv; }
    
    public GenMoneda getMoneda() { return moneda; }
    public void setMoneda(GenMoneda moneda) { this.moneda = moneda; }
    
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
    
    public LocalDateTime getFechaInicioVigencia() { return fechaInicioVigencia; }
    public void setFechaInicioVigencia(LocalDateTime f) { this.fechaInicioVigencia = f; }
    
    public LocalDateTime getFechaFinVigencia() { return fechaFinVigencia; }
    public void setFechaFinVigencia(LocalDateTime f) { this.fechaFinVigencia = f; }
    
    public Long getUsuarioCreacionId() { return usuarioCreacionId; }
    public void setUsuarioCreacionId(Long u) { this.usuarioCreacionId = u; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime f) { this.fechaCreacion = f; }
}
