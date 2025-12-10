package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inv_promocion_aplicacion")
public class InvPromocionAplicacion extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "promocion_id")
    private InvPromocion promocion;
    
    private String referenciaTipo;
    private Long referenciaId;

    public InvPromocion getPromocion() { return promocion; }
    public void setPromocion(InvPromocion promocion) { this.promocion = promocion; }
    
    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }
    
    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }
}
