package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pos_turno")
public class PosTurno extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "caja_id")
    private PosCaja caja;
    
    @ManyToOne
    @JoinColumn(name = "usuario_cajero_id")
    private SysUsuario usuarioCajero;
    
    @ManyToOne
    @JoinColumn(name = "usuario_supervisor_id")
    private SysUsuario usuarioSupervisor;
    
    private LocalDateTime fechaHoraApertura;
    private LocalDateTime fechaHoraCierre;
    private BigDecimal montoInicialEfectivo;
    private BigDecimal montoFinalEfectivoDeclarado;
    private BigDecimal montoFinalEfectivoCalculado;
    private BigDecimal diferencia;
    private String estado;

    public PosCaja getCaja() { return caja; }
    public void setCaja(PosCaja caja) { this.caja = caja; }
    
    public SysUsuario getUsuarioCajero() { return usuarioCajero; }
    public void setUsuarioCajero(SysUsuario u) { this.usuarioCajero = u; }
    
    public SysUsuario getUsuarioSupervisor() { return usuarioSupervisor; }
    public void setUsuarioSupervisor(SysUsuario u) { this.usuarioSupervisor = u; }
    
    public LocalDateTime getFechaHoraApertura() { return fechaHoraApertura; }
    public void setFechaHoraApertura(LocalDateTime f) { this.fechaHoraApertura = f; }
    
    public LocalDateTime getFechaHoraCierre() { return fechaHoraCierre; }
    public void setFechaHoraCierre(LocalDateTime f) { this.fechaHoraCierre = f; }
    
    public BigDecimal getMontoInicialEfectivo() { return montoInicialEfectivo; }
    public void setMontoInicialEfectivo(BigDecimal m) { this.montoInicialEfectivo = m; }
    
    public BigDecimal getMontoFinalEfectivoDeclarado() { return montoFinalEfectivoDeclarado; }
    public void setMontoFinalEfectivoDeclarado(BigDecimal m) { this.montoFinalEfectivoDeclarado = m; }
    
    public BigDecimal getMontoFinalEfectivoCalculado() { return montoFinalEfectivoCalculado; }
    public void setMontoFinalEfectivoCalculado(BigDecimal m) { this.montoFinalEfectivoCalculado = m; }
    
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal d) { this.diferencia = d; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
