package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "pos_venta_detalle")
public class PosVentaDetalle extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private PosVenta venta;
    
    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;
    
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    
    @ManyToOne
    @JoinColumn(name = "impuesto_id")
    private InvImpuesto impuesto;
    
    private BigDecimal descuentoMonto;
    private BigDecimal subtotal;
    private BigDecimal impuestosMonto;

    public PosVenta getVenta() { return venta; }
    public void setVenta(PosVenta venta) { this.venta = venta; }
    
    public InvProductoVariante getProductoVariante() { return productoVariante; }
    public void setProductoVariante(InvProductoVariante pv) { this.productoVariante = pv; }
    
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    public InvImpuesto getImpuesto() { return impuesto; }
    public void setImpuesto(InvImpuesto impuesto) { this.impuesto = impuesto; }
    
    public BigDecimal getDescuentoMonto() { return descuentoMonto; }
    public void setDescuentoMonto(BigDecimal d) { this.descuentoMonto = d; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getImpuestosMonto() { return impuestosMonto; }
    public void setImpuestosMonto(BigDecimal i) { this.impuestosMonto = i; }
}
