package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.ContAsiento;
import com.mariastaff.Inventario.backend.data.entity.ContCuenta;
import com.mariastaff.Inventario.backend.data.entity.ContPeriodo;
import com.mariastaff.Inventario.backend.data.repository.ContAsientoRepository;
import com.mariastaff.Inventario.backend.data.repository.ContCuentaRepository;
import com.mariastaff.Inventario.backend.data.repository.ContPeriodoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContabilidadService {

    private final ContAsientoRepository asientoRepository;
    private final ContCuentaRepository cuentaRepository;
    private final ContPeriodoRepository periodoRepository;

    public ContabilidadService(ContAsientoRepository asientoRepository, ContCuentaRepository cuentaRepository, ContPeriodoRepository periodoRepository) {
        this.asientoRepository = asientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.periodoRepository = periodoRepository;
    }

    public List<ContAsiento> findAllAsientos() { return asientoRepository.findAll(); }
    public ContAsiento saveAsiento(ContAsiento entity) { return asientoRepository.save(entity); }
    public void deleteAsiento(ContAsiento entity) { asientoRepository.delete(entity); }

    public List<ContCuenta> findAllCuentas() { return cuentaRepository.findAll(); }
    public ContCuenta saveCuenta(ContCuenta entity) { return cuentaRepository.save(entity); }
    public void deleteCuenta(ContCuenta entity) { cuentaRepository.delete(entity); }

    public List<ContPeriodo> findAllPeriodos() { return periodoRepository.findAll(); }
    public ContPeriodo savePeriodo(ContPeriodo entity) { return periodoRepository.save(entity); }
    public void deletePeriodo(ContPeriodo entity) { periodoRepository.delete(entity); }
}
