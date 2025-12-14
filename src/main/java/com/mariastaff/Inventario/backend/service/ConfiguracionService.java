package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.SysConfiguracion;
import com.mariastaff.Inventario.backend.data.repository.SysConfiguracionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConfiguracionService {

    private final SysConfiguracionRepository configuracionRepository;

    public ConfiguracionService(SysConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    // Usually config is a single record, but finding all is safe default
    public List<SysConfiguracion> findAll() { return configuracionRepository.findAll(); }
    public SysConfiguracion save(SysConfiguracion entity) { return configuracionRepository.save(entity); }
    public void delete(SysConfiguracion entity) { configuracionRepository.delete(entity); }
}
