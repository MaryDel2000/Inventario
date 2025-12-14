package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_compra_detalle")
public class InvCompraDetalle extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private InvCompra compra;
    
    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;
    
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
    private LocalDateTime fechaCaducidad;

    public InvCompra getCompra() { return compra; }
    public void setCompra(InvCompra compra) { this.compra = compra; }
    
    public InvProductoVariante getProductoVariante() { return productoVariante; }
    public void setProductoVariante(InvProductoVariante pv) { this.productoVariante = pv; }
    
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public LocalDateTime getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(LocalDateTime fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }
}
