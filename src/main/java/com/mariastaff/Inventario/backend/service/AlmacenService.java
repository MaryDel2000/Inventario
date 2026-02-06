package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.mariastaff.Inventario.backend.data.entity.InvExistencia;
import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
import com.mariastaff.Inventario.backend.data.repository.InvAlmacenRepository;
import com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository;
import com.mariastaff.Inventario.backend.data.repository.InvUbicacionRepository;
import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.data.repository.InvLoteRepository;
import com.mariastaff.Inventario.backend.data.entity.GenSucursal;
import com.mariastaff.Inventario.backend.data.repository.GenSucursalRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlmacenService {

    private final InvAlmacenRepository almacenRepository;
    private final InvUbicacionRepository ubicacionRepository;
    private final InvExistenciaRepository existenciaRepository;
    private final InvLoteRepository loteRepository;
    private final GenSucursalRepository sucursalRepository;

    public AlmacenService(InvAlmacenRepository almacenRepository, InvUbicacionRepository ubicacionRepository,
            InvExistenciaRepository existenciaRepository, InvLoteRepository loteRepository,
            GenSucursalRepository sucursalRepository) {
        this.almacenRepository = almacenRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.existenciaRepository = existenciaRepository;
        this.loteRepository = loteRepository;
        this.sucursalRepository = sucursalRepository;
    }

    public List<InvAlmacen> findAllAlmacenes() {
        return almacenRepository.findAll();
    }

    public InvAlmacen saveAlmacen(InvAlmacen entity) {
        return almacenRepository.save(entity);
    }

    public void deleteAlmacen(InvAlmacen entity) {
        almacenRepository.delete(entity);
    }

    public List<GenSucursal> findAllSucursales() {
        return sucursalRepository.findAll();
    }

    public List<InvUbicacion> findAllUbicaciones() {
        return ubicacionRepository.findAll();
    }

    public InvUbicacion saveUbicacion(InvUbicacion entity) {
        return ubicacionRepository.save(entity);
    }

    public void deleteUbicacion(InvUbicacion entity) {
        ubicacionRepository.delete(entity);
    }

    public List<InvUbicacion> findUbicacionesByAlmacen(InvAlmacen almacen) {
        return ubicacionRepository.findByAlmacen(almacen);
    }

    public List<InvAlmacen> findAlmacenesByProveedor(
            com.mariastaff.Inventario.backend.data.entity.InvProveedor proveedor) {
        return almacenRepository.findByProveedor(proveedor);
    }

    public List<InvAlmacen> findAlmacenesByTipo(String tipo) {
        return almacenRepository.findByTipoAlmacen(tipo);
    }

    public List<InvExistencia> findAllExistencias() {
        return existenciaRepository.findAll();
    }

    public InvExistencia saveExistencia(InvExistencia entity) {
        return existenciaRepository.save(entity);
    }

    public List<InvLote> findAllLotes() {
        return loteRepository.findAll();
    }

    public InvLote saveLote(InvLote entity) {
        return loteRepository.save(entity);
    }

    public void deleteLote(InvLote entity) {
        loteRepository.delete(entity);
    }

    public List<InvLote> findLotesByVariante(
            com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante) {
        return loteRepository.findByProductoVariante(variante);
    }

    public long countAlmacenes() {
        return almacenRepository.count();
    }

    public long countExistencias() {
        return existenciaRepository.count();
    }

    public java.math.BigDecimal getStockTotal(Long productoId) {
        java.math.BigDecimal total = existenciaRepository.sumStockByProducto(productoId);
        return total != null ? total : java.math.BigDecimal.ZERO;
    }

    public java.util.Map<String, java.math.BigDecimal> getStockByAlmacen() {
        java.util.List<Object[]> results = existenciaRepository.sumStockByAlmacen();
        java.util.Map<String, java.math.BigDecimal> map = new java.util.HashMap<>();
        for (Object[] row : results) {
            String almacen = (String) row[0];
            java.math.BigDecimal total = (java.math.BigDecimal) row[1];
            map.put(almacen, total);
        }
        return map;
    }
}
