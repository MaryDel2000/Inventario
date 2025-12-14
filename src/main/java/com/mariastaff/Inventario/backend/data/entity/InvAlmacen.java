package com.mariastaff.Inventario.backend.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "inv_almacen")
public class InvAlmacen extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "sucursal_id")
    private GenSucursal sucursal;

    @NotEmpty
    private String nombre;
    private String codigo;
    private String tipoAlmacen;
    private Boolean esExterno = false;
    private Boolean permiteVenta = true;
    private String direccion;
    private String responsableContacto;
    private String descripcion;
    private Boolean activo = true;

    public GenSucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(GenSucursal sucursal) {
        this.sucursal = sucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipoAlmacen() {
        return tipoAlmacen;
    }

    public void setTipoAlmacen(String tipoAlmacen) {
        this.tipoAlmacen = tipoAlmacen;
    }

    public Boolean getEsExterno() {
        return esExterno;
    }

    public void setEsExterno(Boolean esExterno) {
        this.esExterno = esExterno;
    }

    public Boolean getPermiteVenta() {
        return permiteVenta;
    }

    public void setPermiteVenta(Boolean permiteVenta) {
        this.permiteVenta = permiteVenta;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getResponsableContacto() {
        return responsableContacto;
    }

    public void setResponsableContacto(String responsableContacto) {
        this.responsableContacto = responsableContacto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
