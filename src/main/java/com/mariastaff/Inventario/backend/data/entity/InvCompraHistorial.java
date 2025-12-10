package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "inv_compra_historial")
public class InvCompraHistorial extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "compra_id")
    private InvCompra compra;
    
    private String estadoAnterior;
    private String estadoNuevo;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private SysUsuario usuario;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaCambio = LocalDateTime.now();
    
    private String observaciones;

    public InvCompra getCompra() { return compra; }
    public void setCompra(InvCompra compra) { this.compra = compra; }
    
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    
    public SysUsuario getUsuario() { return usuario; }
    public void setUsuario(SysUsuario usuario) { this.usuario = usuario; }
    
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
