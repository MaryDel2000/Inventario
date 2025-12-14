package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_promocion")
public class InvPromocion extends AbstractEntity {

    private String codigoCupon;
    private String nombre;
    private String tipoDescuento;
    private BigDecimal valor;
    private LocalDateTime fechaInicioVigencia;
    private LocalDateTime fechaFinVigencia;
    private Boolean activo = true;
    private Long usuarioCreacionId;

    public String getCodigoCupon() { return codigoCupon; }
    public void setCodigoCupon(String codigoCupon) { this.codigoCupon = codigoCupon; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getTipoDescuento() { return tipoDescuento; }
    public void setTipoDescuento(String tipoDescuento) { this.tipoDescuento = tipoDescuento; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public LocalDateTime getFechaInicioVigencia() { return fechaInicioVigencia; }
    public void setFechaInicioVigencia(LocalDateTime f) { this.fechaInicioVigencia = f; }
    
    public LocalDateTime getFechaFinVigencia() { return fechaFinVigencia; }
    public void setFechaFinVigencia(LocalDateTime f) { this.fechaFinVigencia = f; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Long getUsuarioCreacionId() { return usuarioCreacionId; }
    public void setUsuarioCreacionId(Long u) { this.usuarioCreacionId = u; }
}
