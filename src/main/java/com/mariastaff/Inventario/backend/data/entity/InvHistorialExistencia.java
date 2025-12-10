package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "inv_historial_existencia")
public class InvHistorialExistencia extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private InvAlmacen almacen;
    
    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private InvUbicacion ubicacion;
    
    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;
    
    @ManyToOne
    @JoinColumn(name = "lote_id")
    private InvLote lote;
    
    private BigDecimal cantidadAnterior;
    private BigDecimal cantidadNueva;
    private BigDecimal diferencia;
    private String tipoMovimiento;
    private Long referenciaId;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaHistorial = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "usuario_responsable_id")
    private SysUsuario usuarioResponsable;

    // Getters and Setters omitted for brevity but should be here
    public InvAlmacen getAlmacen() { return almacen; }
    public void setAlmacen(InvAlmacen almacen) { this.almacen = almacen; }
    
    public InvUbicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(InvUbicacion ubicacion) { this.ubicacion = ubicacion; }
    
    public InvProductoVariante getProductoVariante() { return productoVariante; }
    public void setProductoVariante(InvProductoVariante pv) { this.productoVariante = pv; }
    
    public InvLote getLote() { return lote; }
    public void setLote(InvLote lote) { this.lote = lote; }
    
    public BigDecimal getCantidadAnterior() { return cantidadAnterior; }
    public void setCantidadAnterior(BigDecimal c) { this.cantidadAnterior = c; }
    
    public BigDecimal getCantidadNueva() { return cantidadNueva; }
    public void setCantidadNueva(BigDecimal c) { this.cantidadNueva = c; }
    
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal d) { this.diferencia = d; }
    
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String t) { this.tipoMovimiento = t; }
    
    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long r) { this.referenciaId = r; }
    
    public LocalDateTime getFechaHistorial() { return fechaHistorial; }
    public void setFechaHistorial(LocalDateTime f) { this.fechaHistorial = f; }
    
    public SysUsuario getUsuarioResponsable() { return usuarioResponsable; }
    public void setUsuarioResponsable(SysUsuario u) { this.usuarioResponsable = u; }
}
