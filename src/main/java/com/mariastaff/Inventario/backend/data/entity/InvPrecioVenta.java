package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_precio_venta")
public class InvPrecioVenta extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "lista_precio_id")
    private InvListaPrecio listaPrecio;
    
    @ManyToOne
    @JoinColumn(name = "producto_variante_id")
    private InvProductoVariante productoVariante;

    @ManyToOne
    @JoinColumn(name = "moneda_id")
    private GenMoneda moneda;
    
    private BigDecimal precioVenta;
    private LocalDateTime fechaInicioVigencia;
    private LocalDateTime fechaFinVigencia;
    private String motivoCambio;

    public InvListaPrecio getListaPrecio() { return listaPrecio; }
    public void setListaPrecio(InvListaPrecio listaPrecio) { this.listaPrecio = listaPrecio; }
    
    public InvProductoVariante getProductoVariante() { return productoVariante; }
    public void setProductoVariante(InvProductoVariante pv) { this.productoVariante = pv; }
    
    public GenMoneda getMoneda() { return moneda; }
    public void setMoneda(GenMoneda moneda) { this.moneda = moneda; }
    
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    
    public LocalDateTime getFechaInicioVigencia() { return fechaInicioVigencia; }
    public void setFechaInicioVigencia(LocalDateTime f) { this.fechaInicioVigencia = f; }
    
    public LocalDateTime getFechaFinVigencia() { return fechaFinVigencia; }
    public void setFechaFinVigencia(LocalDateTime f) { this.fechaFinVigencia = f; }
    
    public String getMotivoCambio() { return motivoCambio; }
    public void setMotivoCambio(String motivoCambio) { this.motivoCambio = motivoCambio; }
}
