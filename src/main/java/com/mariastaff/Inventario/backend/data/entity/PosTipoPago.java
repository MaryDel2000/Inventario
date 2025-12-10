package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "pos_tipo_pago")
public class PosTipoPago extends AbstractEntity {

    @NotEmpty
    private String nombre;
    private String codigoInterno;
    private Boolean requiereReferencia = false;
    private Boolean activo = true;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCodigoInterno() { return codigoInterno; }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }
    
    public Boolean getRequiereReferencia() { return requiereReferencia; }
    public void setRequiereReferencia(Boolean r) { this.requiereReferencia = r; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
