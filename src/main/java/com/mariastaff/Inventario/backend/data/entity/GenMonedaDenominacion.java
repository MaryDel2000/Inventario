package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "gen_moneda_denominacion")
public class GenMonedaDenominacion extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "moneda_id")
    private GenMoneda moneda;
    
    private String nombre;
    private BigDecimal valor;
    private String tipo; // BILLETE, MONEDA
    private Boolean activo = true;

    public GenMoneda getMoneda() { return moneda; }
    public void setMoneda(GenMoneda moneda) { this.moneda = moneda; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
