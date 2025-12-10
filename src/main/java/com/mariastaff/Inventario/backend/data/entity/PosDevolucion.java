package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "pos_devolucion")
public class PosDevolucion extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private PosVenta venta;
    
    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;
    
    @ManyToOne
    @JoinColumn(name = "movimiento_id")
    private InvMovimiento movimiento;
    
    private String motivoGeneral;
    private BigDecimal totalReembolso;
    private String estado;

    public PosVenta getVenta() { return venta; }
    public void setVenta(PosVenta venta) { this.venta = venta; }
    
    public GenSucursal getSucursal() { return sucursal; }
    public void setSucursal(GenSucursal sucursal) { this.sucursal = sucursal; }
    
    public InvMovimiento getMovimiento() { return movimiento; }
    public void setMovimiento(InvMovimiento movimiento) { this.movimiento = movimiento; }
    
    public String getMotivoGeneral() { return motivoGeneral; }
    public void setMotivoGeneral(String motivoGeneral) { this.motivoGeneral = motivoGeneral; }
    
    public BigDecimal getTotalReembolso() { return totalReembolso; }
    public void setTotalReembolso(BigDecimal totalReembolso) { this.totalReembolso = totalReembolso; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
