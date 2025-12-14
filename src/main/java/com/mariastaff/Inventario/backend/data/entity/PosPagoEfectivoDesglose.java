package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "pos_pago_efectivo_desglose")
public class PosPagoEfectivoDesglose extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "pago_id")
    private PosPago pago;
    
    @ManyToOne
    @JoinColumn(name = "moneda_denominacion_id")
    private GenMonedaDenominacion monedaDenominacion;
    
    private Integer cantidad;
    private BigDecimal montoTotal;
    private String tipoMovimiento;

    public PosPago getPago() { return pago; }
    public void setPago(PosPago pago) { this.pago = pago; }
    
    public GenMonedaDenominacion getMonedaDenominacion() { return monedaDenominacion; }
    public void setMonedaDenominacion(GenMonedaDenominacion m) { this.monedaDenominacion = m; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    
    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
}
