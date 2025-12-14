package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "gen_moneda_tasa")
public class GenMonedaTasa extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "moneda_origen_id")
    private GenMoneda monedaOrigen;
    
    @ManyToOne
    @JoinColumn(name = "moneda_destino_id")
    private GenMoneda monedaDestino;
    
    private BigDecimal tasaConversion;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    public GenMoneda getMonedaOrigen() { return monedaOrigen; }
    public void setMonedaOrigen(GenMoneda m) { this.monedaOrigen = m; }
    
    public GenMoneda getMonedaDestino() { return monedaDestino; }
    public void setMonedaDestino(GenMoneda m) { this.monedaDestino = m; }
    
    public BigDecimal getTasaConversion() { return tasaConversion; }
    public void setTasaConversion(BigDecimal t) { this.tasaConversion = t; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime f) { this.fechaActualizacion = f; }
}
