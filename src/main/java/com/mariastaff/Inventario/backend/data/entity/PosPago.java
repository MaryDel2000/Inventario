package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "pos_pago")
public class PosPago extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private PosVenta venta;
    
    @ManyToOne
    @JoinColumn(name = "tipo_pago_id")
    private PosTipoPago tipoPago;
    
    private BigDecimal montoTotal;
    private BigDecimal montoRecibido;
    private BigDecimal montoVuelto;
    private String referenciaPago;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaPago = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "usuario_registro_id")
    private SysUsuario usuarioRegistro;

    public PosVenta getVenta() { return venta; }
    public void setVenta(PosVenta venta) { this.venta = venta; }
    
    public PosTipoPago getTipoPago() { return tipoPago; }
    public void setTipoPago(PosTipoPago tipoPago) { this.tipoPago = tipoPago; }
    
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    
    public BigDecimal getMontoRecibido() { return montoRecibido; }
    public void setMontoRecibido(BigDecimal montoRecibido) { this.montoRecibido = montoRecibido; }
    
    public BigDecimal getMontoVuelto() { return montoVuelto; }
    public void setMontoVuelto(BigDecimal montoVuelto) { this.montoVuelto = montoVuelto; }
    
    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }
    
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime f) { this.fechaPago = f; }
    
    public SysUsuario getUsuarioRegistro() { return usuarioRegistro; }
    public void setUsuarioRegistro(SysUsuario u) { this.usuarioRegistro = u; }
}
