package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.GenEntidad;
import com.mariastaff.Inventario.backend.data.entity.GenMoneda;
import com.mariastaff.Inventario.backend.data.entity.GenSucursal;
import com.mariastaff.Inventario.backend.data.repository.GenEntidadRepository;
import com.mariastaff.Inventario.backend.data.repository.GenMonedaRepository;
import com.mariastaff.Inventario.backend.data.repository.GenSucursalRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GeneralService {

    private final GenEntidadRepository entidadRepository;
    private final GenMonedaRepository monedaRepository;
    private final GenSucursalRepository sucursalRepository;

    public GeneralService(GenEntidadRepository entidadRepository, GenMonedaRepository monedaRepository, GenSucursalRepository sucursalRepository) {
        this.entidadRepository = entidadRepository;
        this.monedaRepository = monedaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    public List<GenEntidad> findAllEntidades() { return entidadRepository.findAll(); }
    public GenEntidad saveEntidad(GenEntidad entity) { return entidadRepository.save(entity); }
    public void deleteEntidad(GenEntidad entity) { entidadRepository.delete(entity); }

    public List<GenMoneda> findAllMonedas() { return monedaRepository.findAll(); }
    public GenMoneda saveMoneda(GenMoneda entity) { return monedaRepository.save(entity); }
    public void deleteMoneda(GenMoneda entity) { monedaRepository.delete(entity); }

    public List<GenSucursal> findAllSucursales() { return sucursalRepository.findAll(); }
    public GenSucursal saveSucursal(GenSucursal entity) { return sucursalRepository.save(entity); }
    public void deleteSucursal(GenSucursal entity) { sucursalRepository.delete(entity); }
}
