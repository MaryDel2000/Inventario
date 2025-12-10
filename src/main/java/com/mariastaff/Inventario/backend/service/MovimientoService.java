package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvMovimiento;
import com.mariastaff.Inventario.backend.data.repository.InvMovimientoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MovimientoService {

    private final InvMovimientoRepository movimientoRepository;

    public MovimientoService(InvMovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    public List<InvMovimiento> findAllMovimientos() { return movimientoRepository.findAll(); }
    public InvMovimiento saveMovimiento(InvMovimiento entity) { return movimientoRepository.save(entity); }
    public void deleteMovimiento(InvMovimiento entity) { movimientoRepository.delete(entity); }
    public long countMovimientos() { return movimientoRepository.count(); }
}
