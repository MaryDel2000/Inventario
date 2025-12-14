package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "pos_devolucion_detalle")
public class PosDevolucionDetalle extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "devolucion_id")
    private PosDevolucion devolucion;
    
    @ManyToOne
    @JoinColumn(name = "venta_detalle_id")
    private PosVentaDetalle ventaDetalle;
    
    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;
    
    private BigDecimal cantidadDevuelta;
    private BigDecimal precioReembolsoUnitario;
    private BigDecimal montoReembolsoLinea;

    public PosDevolucion getDevolucion() { return devolucion; }
    public void setDevolucion(PosDevolucion devolucion) { this.devolucion = devolucion; }
    
    public PosVentaDetalle getVentaDetalle() { return ventaDetalle; }
    public void setVentaDetalle(PosVentaDetalle ventaDetalle) { this.ventaDetalle = ventaDetalle; }
    
    public InvProductoVariante getProductoVariante() { return productoVariante; }
    public void setProductoVariante(InvProductoVariante pv) { this.productoVariante = pv; }
    
    public BigDecimal getCantidadDevuelta() { return cantidadDevuelta; }
    public void setCantidadDevuelta(BigDecimal c) { this.cantidadDevuelta = c; }
    
    public BigDecimal getPrecioReembolsoUnitario() { return precioReembolsoUnitario; }
    public void setPrecioReembolsoUnitario(BigDecimal p) { this.precioReembolsoUnitario = p; }
    
    public BigDecimal getMontoReembolsoLinea() { return montoReembolsoLinea; }
    public void setMontoReembolsoLinea(BigDecimal m) { this.montoReembolsoLinea = m; }
}
