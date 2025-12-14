package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "pos_venta_promocion")
public class PosVentaPromocion extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private PosVenta venta;
    
    @ManyToOne
    @JoinColumn(name = "promocion_id")
    private InvPromocion promocion;
    
    private BigDecimal montoDescuento;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaAplicacion = LocalDateTime.now();

    public PosVenta getVenta() { return venta; }
    public void setVenta(PosVenta venta) { this.venta = venta; }
    
    public InvPromocion getPromocion() { return promocion; }
    public void setPromocion(InvPromocion promocion) { this.promocion = promocion; }
    
    public BigDecimal getMontoDescuento() { return montoDescuento; }
    public void setMontoDescuento(BigDecimal m) { this.montoDescuento = m; }
    
    public LocalDateTime getFechaAplicacion() { return fechaAplicacion; }
    public void setFechaAplicacion(LocalDateTime f) { this.fechaAplicacion = f; }
}
