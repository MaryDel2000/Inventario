package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pos_caja")
public class PosCaja extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;
    
    @ManyToOne
    @JoinColumn(name = "punto_venta_id")
    private PosPuntoVenta puntoVenta;
    
    private String nombre;
    private String cookieKey;
    private Boolean activo = true;

    public GenSucursal getSucursal() { return sucursal; }
    public void setSucursal(GenSucursal sucursal) { this.sucursal = sucursal; }
    
    public PosPuntoVenta getPuntoVenta() { return puntoVenta; }
    public void setPuntoVenta(PosPuntoVenta puntoVenta) { this.puntoVenta = puntoVenta; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCookieKey() { return cookieKey; }
    public void setCookieKey(String cookieKey) { this.cookieKey = cookieKey; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
