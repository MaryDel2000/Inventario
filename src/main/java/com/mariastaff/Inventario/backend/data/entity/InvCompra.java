package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inv_compra")
public class InvCompra extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;
    
    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private InvProveedor proveedor;
    
    @ManyToOne
    @JoinColumn(name = "almacen_destino_id")
    private InvAlmacen almacenDestino;
    
    private LocalDateTime fechaCompra;
    private String tipoDocumento;
    private String numeroDocumento;
    private BigDecimal totalCompra;
    private String estado;
    private String observaciones;

    public GenSucursal getSucursal() { return sucursal; }
    public void setSucursal(GenSucursal sucursal) { this.sucursal = sucursal; }
    
    public InvProveedor getProveedor() { return proveedor; }
    public void setProveedor(InvProveedor proveedor) { this.proveedor = proveedor; }
    
    public InvAlmacen getAlmacenDestino() { return almacenDestino; }
    public void setAlmacenDestino(InvAlmacen almacenDestino) { this.almacenDestino = almacenDestino; }
    
    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }
    
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    
    public BigDecimal getTotalCompra() { return totalCompra; }
    public void setTotalCompra(BigDecimal totalCompra) { this.totalCompra = totalCompra; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
