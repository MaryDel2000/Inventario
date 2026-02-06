package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "pos_venta")
public class PosVenta extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;

    @ManyToOne
    @JoinColumn(name = "punto_venta_id")
    private PosPuntoVenta puntoVenta;

    @ManyToOne
    @JoinColumn(name = "turno_id")
    private PosTurno turno;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private PosCliente cliente;

    @ManyToOne
    @JoinColumn(name = "usuario_vendedor_id")
    private SysUsuario usuarioVendedor;

    private String tipoDocumento;
    private String numeroDocumento;

    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaHora = LocalDateTime.now();

    private BigDecimal totalBruto;
    private BigDecimal descuentoTotal;
    private BigDecimal impuestosTotal;
    private BigDecimal totalNeto;
    private String estado;
    private String estadoPago;

    @ManyToOne
    @JoinColumn(name = "almacen_salida_id")
    private InvAlmacen almacenSalida;

    @ManyToOne
    @JoinColumn(name = "movimiento_id")
    private InvMovimiento movimiento;

    @jakarta.persistence.OneToMany(mappedBy = "venta", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PosVentaDetalle> detalles = new java.util.ArrayList<>();

    @jakarta.persistence.OneToMany(mappedBy = "venta", fetch = jakarta.persistence.FetchType.EAGER)
    private java.util.List<PosPago> pagos = new java.util.ArrayList<>();

    public java.util.List<PosPago> getPagos() {
        return pagos;
    }

    public void setPagos(java.util.List<PosPago> pagos) {
        this.pagos = pagos;
    }

    public java.util.List<PosVentaDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(java.util.List<PosVentaDetalle> detalles) {
        this.detalles = detalles;
    }

    public void addDetalle(PosVentaDetalle detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }

    public void removeDetalle(PosVentaDetalle detalle) {
        detalles.remove(detalle);
        detalle.setVenta(null);
    }

    public GenSucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(GenSucursal sucursal) {
        this.sucursal = sucursal;
    }

    public PosPuntoVenta getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(PosPuntoVenta pv) {
        this.puntoVenta = pv;
    }

    public PosTurno getTurno() {
        return turno;
    }

    public void setTurno(PosTurno turno) {
        this.turno = turno;
    }

    public PosCliente getCliente() {
        return cliente;
    }

    public void setCliente(PosCliente cliente) {
        this.cliente = cliente;
    }

    public SysUsuario getUsuarioVendedor() {
        return usuarioVendedor;
    }

    public void setUsuarioVendedor(SysUsuario u) {
        this.usuarioVendedor = u;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime f) {
        this.fechaHora = f;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public void setTotalBruto(BigDecimal t) {
        this.totalBruto = t;
    }

    public BigDecimal getDescuentoTotal() {
        return descuentoTotal;
    }

    public void setDescuentoTotal(BigDecimal t) {
        this.descuentoTotal = t;
    }

    public BigDecimal getImpuestosTotal() {
        return impuestosTotal;
    }

    public void setImpuestosTotal(BigDecimal t) {
        this.impuestosTotal = t;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public void setTotalNeto(BigDecimal t) {
        this.totalNeto = t;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String e) {
        this.estadoPago = e;
    }

    public InvAlmacen getAlmacenSalida() {
        return almacenSalida;
    }

    public void setAlmacenSalida(InvAlmacen a) {
        this.almacenSalida = a;
    }

    public InvMovimiento getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(InvMovimiento m) {
        this.movimiento = m;
    }
}
