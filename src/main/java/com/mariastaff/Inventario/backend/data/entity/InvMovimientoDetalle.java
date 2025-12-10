package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "inv_movimiento_detalle")
public class InvMovimientoDetalle extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "movimiento_id")
    private InvMovimiento movimiento;
    
    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;
    
    @ManyToOne
    @JoinColumn(name = "lote_id")
    private InvLote lote;
    
    private BigDecimal cantidad;
    
    @ManyToOne
    @JoinColumn(name = "ubicacion_origen_id")
    private InvUbicacion ubicacionOrigen;
    
    @ManyToOne
    @JoinColumn(name = "ubicacion_destino_id")
    private InvUbicacion ubicacionDestino;

    public InvMovimiento getMovimiento() { return movimiento; }
    public void setMovimiento(InvMovimiento movimiento) { this.movimiento = movimiento; }
    
    public InvProductoVariante getProductoVariante() { return productoVariante; }
    public void setProductoVariante(InvProductoVariante pv) { this.productoVariante = pv; }
    
    public InvLote getLote() { return lote; }
    public void setLote(InvLote lote) { this.lote = lote; }
    
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    
    public InvUbicacion getUbicacionOrigen() { return ubicacionOrigen; }
    public void setUbicacionOrigen(InvUbicacion u) { this.ubicacionOrigen = u; }
    
    public InvUbicacion getUbicacionDestino() { return ubicacionDestino; }
    public void setUbicacionDestino(InvUbicacion u) { this.ubicacionDestino = u; }
}
