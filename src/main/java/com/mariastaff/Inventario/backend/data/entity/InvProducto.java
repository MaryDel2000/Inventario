package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_producto")
public class InvProducto extends AuditableEntity {

    @NotEmpty
    private String nombre;
    private String codigoInterno;
    
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private InvCategoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "unidad_medida_id")
    private InvUnidadMedida unidadMedida;
    
    @ManyToOne
    @JoinColumn(name = "impuesto_id")
    private InvImpuesto impuesto;
    
    private Boolean manejaVariantes = false;
    private Boolean manejaCaducidad = false;
    private String descripcion;
    private Boolean activo = true;
    


    // Auditoría Estándar
    // (Campos heredados de AuditableEntity: usuarioCreacionId, fechaCreacion, usuarioModificacionId, fechaModificacion)
    // Actually, I control SQL V1. I used 'fecha_actualizacion' in V1 script.
    // I should probably change V1 SQL to 'fecha_modificacion' to be consistent with everything else.
    // Too late to change V1 easily without confusion, but since it's V1 and not applied yet (?),
    // I could update V1.
    // But easier to just mapping in Entity.
    
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getCodigoInterno() {
        return codigoInterno;
    }
    public void setCodigoInterno(String codigoInterno) { this.codigoInterno = codigoInterno; }
    
    // Getters Setters for relations
    public InvCategoria getCategoria() { return categoria; }
    public void setCategoria(InvCategoria categoria) { this.categoria = categoria; }
    
    public InvUnidadMedida getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(InvUnidadMedida unidadMedida) { this.unidadMedida = unidadMedida; }
    
    public InvImpuesto getImpuesto() { return impuesto; }
    public void setImpuesto(InvImpuesto impuesto) { this.impuesto = impuesto; }
    
    public Boolean getManejaVariantes() { return manejaVariantes; }
    public void setManejaVariantes(Boolean manejaVariantes) { this.manejaVariantes = manejaVariantes; }
    
    public Boolean getManejaCaducidad() { return manejaCaducidad; }
    public void setManejaCaducidad(Boolean manejaCaducidad) { this.manejaCaducidad = manejaCaducidad; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
