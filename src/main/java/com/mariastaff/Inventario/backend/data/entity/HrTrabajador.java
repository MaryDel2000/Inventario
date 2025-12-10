package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "hr_trabajador")
public class HrTrabajador extends AuditableEntity {

    @OneToOne
    @JoinColumn(name = "entidad_id")
    private GenEntidad entidad;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private SysUsuario usuario;

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;

    private String codigoEmpleado;
    private String puesto;
    private LocalDate fechaContratacion;
    private Boolean activo = true;

    public GenEntidad getEntidad() {
        return entidad;
    }

    public void setEntidad(GenEntidad entidad) {
        this.entidad = entidad;
    }

    public SysUsuario getUsuario() {
        return usuario;
    }

    public void setUsuario(SysUsuario usuario) {
        this.usuario = usuario;
    }

    public GenSucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(GenSucursal sucursal) {
        this.sucursal = sucursal;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public LocalDate getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(LocalDate fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
