package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "pos_turno_arqueo")
public class PosTurnoArqueo extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "turno_id")
    private PosTurno turno;
    
    @ManyToOne
    @JoinColumn(name = "moneda_denominacion_id")
    private GenMonedaDenominacion monedaDenominacion;
    
    private Integer cantidad;
    private BigDecimal totalValor;
    private String tipoArqueo; // APERTURA, CIERRE

    public PosTurno getTurno() { return turno; }
    public void setTurno(PosTurno turno) { this.turno = turno; }
    
    public GenMonedaDenominacion getMonedaDenominacion() { return monedaDenominacion; }
    public void setMonedaDenominacion(GenMonedaDenominacion m) { this.monedaDenominacion = m; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getTotalValor() { return totalValor; }
    public void setTotalValor(BigDecimal totalValor) { this.totalValor = totalValor; }
    
    public String getTipoArqueo() { return tipoArqueo; }
    public void setTipoArqueo(String tipoArqueo) { this.tipoArqueo = tipoArqueo; }
}
