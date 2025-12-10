package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.HrTrabajador;
import com.mariastaff.Inventario.backend.data.repository.HrTrabajadorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HrService {

    private final HrTrabajadorRepository trabajadorRepository;

    public HrService(HrTrabajadorRepository trabajadorRepository) {
        this.trabajadorRepository = trabajadorRepository;
    }

    public List<HrTrabajador> findAllTrabajadores() { return trabajadorRepository.findAll(); }
    public HrTrabajador saveTrabajador(HrTrabajador entity) { return trabajadorRepository.save(entity); }
    public void deleteTrabajador(HrTrabajador entity) { trabajadorRepository.delete(entity); }
}
