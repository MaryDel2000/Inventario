package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.PosCaja;
import com.mariastaff.Inventario.backend.data.entity.PosCliente;
import com.mariastaff.Inventario.backend.data.entity.PosTurno;
import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import com.mariastaff.Inventario.backend.data.repository.PosCajaRepository;
import com.mariastaff.Inventario.backend.data.repository.PosClienteRepository;
import com.mariastaff.Inventario.backend.data.repository.PosTurnoRepository;
import com.mariastaff.Inventario.backend.data.repository.PosVentaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PosService {

    private final PosVentaRepository ventaRepository;
    private final PosTurnoRepository turnoRepository;
    private final PosCajaRepository cajaRepository;
    private final PosClienteRepository clienteRepository;

    public PosService(PosVentaRepository ventaRepository, PosTurnoRepository turnoRepository, PosCajaRepository cajaRepository, PosClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.turnoRepository = turnoRepository;
        this.cajaRepository = cajaRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<PosVenta> findAllVentas() { return ventaRepository.findAll(); }
    public PosVenta saveVenta(PosVenta entity) { return ventaRepository.save(entity); }
    public void deleteVenta(PosVenta entity) { ventaRepository.delete(entity); }
    public List<PosVenta> findVentasPorCobrar() { return ventaRepository.findByEstadoPagoNot("PAGADO"); }

    public List<PosTurno> findAllTurnos() { return turnoRepository.findAll(); }
    public PosTurno saveTurno(PosTurno entity) { return turnoRepository.save(entity); }
    public void deleteTurno(PosTurno entity) { turnoRepository.delete(entity); }

    public List<PosCaja> findAllCajas() { return cajaRepository.findAll(); }
    public PosCaja saveCaja(PosCaja entity) { return cajaRepository.save(entity); }
    public void deleteCaja(PosCaja entity) { cajaRepository.delete(entity); }

    public List<PosCliente> findAllClientes() { return clienteRepository.findAll(); }
    public PosCliente saveCliente(PosCliente entity) { return clienteRepository.save(entity); }
    public void deleteCliente(PosCliente entity) { clienteRepository.delete(entity); }
}
