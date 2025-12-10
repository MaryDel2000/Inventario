package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "cont_asiento_detalle")
public class ContAsientoDetalle extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "asiento_id")
    private ContAsiento asiento;
    
    @ManyToOne
    @JoinColumn(name = "cuenta_id")
    private ContCuenta cuenta;
    
    private String descripcionLinea;
    private BigDecimal debe;
    private BigDecimal haber;

    public ContAsiento getAsiento() { return asiento; }
    public void setAsiento(ContAsiento asiento) { this.asiento = asiento; }
    
    public ContCuenta getCuenta() { return cuenta; }
    public void setCuenta(ContCuenta cuenta) { this.cuenta = cuenta; }
    
    public String getDescripcionLinea() { return descripcionLinea; }
    public void setDescripcionLinea(String d) { this.descripcionLinea = d; }
    
    public BigDecimal getDebe() { return debe; }
    public void setDebe(BigDecimal debe) { this.debe = debe; }
    
    public BigDecimal getHaber() { return haber; }
    public void setHaber(BigDecimal haber) { this.haber = haber; }
}
