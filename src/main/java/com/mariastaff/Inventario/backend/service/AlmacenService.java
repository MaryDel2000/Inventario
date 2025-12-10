package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.mariastaff.Inventario.backend.data.entity.InvExistencia;
import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
import com.mariastaff.Inventario.backend.data.repository.InvAlmacenRepository;
import com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository;
import com.mariastaff.Inventario.backend.data.repository.InvUbicacionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlmacenService {

    private final InvAlmacenRepository almacenRepository;
    private final InvUbicacionRepository ubicacionRepository;
    private final InvExistenciaRepository existenciaRepository;

    public AlmacenService(InvAlmacenRepository almacenRepository, InvUbicacionRepository ubicacionRepository, InvExistenciaRepository existenciaRepository) {
        this.almacenRepository = almacenRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.existenciaRepository = existenciaRepository;
    }

    public List<InvAlmacen> findAllAlmacenes() { return almacenRepository.findAll(); }
    public InvAlmacen saveAlmacen(InvAlmacen entity) { return almacenRepository.save(entity); }
    public void deleteAlmacen(InvAlmacen entity) { almacenRepository.delete(entity); }

    public List<InvUbicacion> findAllUbicaciones() { return ubicacionRepository.findAll(); }
    public InvUbicacion saveUbicacion(InvUbicacion entity) { return ubicacionRepository.save(entity); }
    public void deleteUbicacion(InvUbicacion entity) { ubicacionRepository.delete(entity); }

    public List<InvExistencia> findAllExistencias() { return existenciaRepository.findAll(); }
    public InvExistencia saveExistencia(InvExistencia entity) { return existenciaRepository.save(entity); }
    // No delete for existences ideally, but standard patterns apply
}
