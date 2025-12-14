package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.mariastaff.Inventario.backend.data.entity.InvExistencia;
import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
import com.mariastaff.Inventario.backend.data.repository.InvAlmacenRepository;
import com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository;
import com.mariastaff.Inventario.backend.data.repository.InvUbicacionRepository;
import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.data.repository.InvLoteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlmacenService {

    private final InvAlmacenRepository almacenRepository;
    private final InvUbicacionRepository ubicacionRepository;
    private final InvExistenciaRepository existenciaRepository;
    private final InvLoteRepository loteRepository;

    public AlmacenService(InvAlmacenRepository almacenRepository, InvUbicacionRepository ubicacionRepository, InvExistenciaRepository existenciaRepository, InvLoteRepository loteRepository) {
        this.almacenRepository = almacenRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.existenciaRepository = existenciaRepository;
        this.loteRepository = loteRepository;
    }

    public List<InvAlmacen> findAllAlmacenes() { return almacenRepository.findAll(); }
    public InvAlmacen saveAlmacen(InvAlmacen entity) { return almacenRepository.save(entity); }
    public void deleteAlmacen(InvAlmacen entity) { almacenRepository.delete(entity); }

    public List<InvUbicacion> findAllUbicaciones() { return ubicacionRepository.findAll(); }
    public InvUbicacion saveUbicacion(InvUbicacion entity) { return ubicacionRepository.save(entity); }
    public void deleteUbicacion(InvUbicacion entity) { ubicacionRepository.delete(entity); }

    public List<InvExistencia> findAllExistencias() { return existenciaRepository.findAll(); }
    public InvExistencia saveExistencia(InvExistencia entity) { return existenciaRepository.save(entity); }
    
    public List<InvLote> findAllLotes() { return loteRepository.findAll(); }
    public InvLote saveLote(InvLote entity) { return loteRepository.save(entity); }
    public void deleteLote(InvLote entity) { loteRepository.delete(entity); }
    
    public long countAlmacenes() { return almacenRepository.count(); }
    public long countExistencias() { return existenciaRepository.count(); }
}
