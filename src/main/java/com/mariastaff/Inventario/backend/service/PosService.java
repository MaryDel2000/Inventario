package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.PosCaja;
import com.mariastaff.Inventario.backend.data.entity.PosCliente;
import com.mariastaff.Inventario.backend.data.entity.PosTurno;
import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import com.mariastaff.Inventario.backend.data.entity.PosVentaDetalle;
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

    private final MovimientoService movimientoService;
    private final com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository existenciaRepository;
    private final com.mariastaff.Inventario.backend.data.repository.GenEntidadRepository entidadRepository;
    private final ProductoService productoService;

    public PosService(PosVentaRepository ventaRepository, PosTurnoRepository turnoRepository, PosCajaRepository cajaRepository, PosClienteRepository clienteRepository,
                      MovimientoService movimientoService,
                      com.mariastaff.Inventario.backend.data.repository.InvExistenciaRepository existenciaRepository,
                      com.mariastaff.Inventario.backend.data.repository.GenEntidadRepository entidadRepository,
                      ProductoService productoService) {
        this.ventaRepository = ventaRepository;
        this.turnoRepository = turnoRepository;
        this.cajaRepository = cajaRepository;
        this.clienteRepository = clienteRepository;
        this.movimientoService = movimientoService;
        this.existenciaRepository = existenciaRepository;
        this.entidadRepository = entidadRepository;
        this.productoService = productoService;
    }

    public List<PosVenta> findAllVentas() { return ventaRepository.findAll(); }
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
    
    @org.springframework.transaction.annotation.Transactional
    public PosVenta saveVenta(PosVenta entity) {
        // 1. Save Venta
        entity = ventaRepository.save(entity);
        
        // 2. Create Movement Header (Stock Deduction)
        if (entity.getAlmacenSalida() != null && "CERRADO".equals(entity.getEstado())) {
             com.mariastaff.Inventario.backend.data.entity.InvMovimiento movimiento = new com.mariastaff.Inventario.backend.data.entity.InvMovimiento();
             movimiento.setTipoMovimiento("SALIDA");
             movimiento.setFechaMovimiento(entity.getFechaHora());
             movimiento.setAlmacenOrigen(entity.getAlmacenSalida());
             movimiento.setReferenciaTipo("POS_VENTA");
             movimiento.setReferenciaId(entity.getId());
             movimiento.setObservaciones("Venta POS #" + entity.getId());
             
             java.util.List<com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle> movDetalles = new java.util.ArrayList<>();
             
             // 3. Process Details (FIFO Stock Deduction)
             for (com.mariastaff.Inventario.backend.data.entity.PosVentaDetalle detalle : entity.getDetalles()) {
                 java.math.BigDecimal qtyRemaining = detalle.getCantidad();
                 
                 // Find all stock for this variant in the source warehouse, ordered by expiry or modification (FIFO)
                 // Simplification: just find by variant, filter by warehouse. Ideally repo has a method.
                 List<com.mariastaff.Inventario.backend.data.entity.InvExistencia> stockList = existenciaRepository.findByProductoVariante(detalle.getProductoVariante());
                 
                 // Sort by: Expiry Date (Asc), then Updated Date (Asc)
                 stockList.sort((e1, e2) -> {
                      if (e1.getLote() != null && e2.getLote() != null && e1.getLote().getFechaCaducidad() != null && e2.getLote().getFechaCaducidad() != null) {
                          return e1.getLote().getFechaCaducidad().compareTo(e2.getLote().getFechaCaducidad());
                      }
                      return e1.getFechaUltimaActualizacion().compareTo(e2.getFechaUltimaActualizacion());
                 });
                 
                 for (com.mariastaff.Inventario.backend.data.entity.InvExistencia stock : stockList) {
                      if (!stock.getAlmacen().getId().equals(entity.getAlmacenSalida().getId())) continue;
                      if (stock.getCantidadDisponible().compareTo(java.math.BigDecimal.ZERO) <= 0) continue;
                      
                      java.math.BigDecimal qtyToTake = qtyRemaining.min(stock.getCantidadDisponible());
                      
                      com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle movDet = new com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle();
                      movDet.setProductoVariante(detalle.getProductoVariante());
                      movDet.setCantidad(qtyToTake);
                      movDet.setLote(stock.getLote());
                      movDet.setUbicacionOrigen(stock.getUbicacion());
                      
                      movDetalles.add(movDet);
                      
                      qtyRemaining = qtyRemaining.subtract(qtyToTake);
                      if (qtyRemaining.compareTo(java.math.BigDecimal.ZERO) == 0) break;
                 }
                 
                 // If there is still remaining quantity, forces a deduction from a default location or generic 
                 // (or we just log/warn. Here we force a negative on the first match or null lote to ensure movement is recorded)
                 if (qtyRemaining.compareTo(java.math.BigDecimal.ZERO) > 0) {
                     com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle movDet = new com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle();
                     movDet.setProductoVariante(detalle.getProductoVariante());
                     movDet.setCantidad(qtyRemaining);
                     movDet.setLote(null); // Unknown batch
                     movDet.setUbicacionOrigen(null); // Unknown location
                     movDetalles.add(movDet);
                 }
             }
             
             if (!movDetalles.isEmpty()) {
                 movimientoService.crearMovimiento(movimiento, movDetalles);
                 entity.setMovimiento(movimiento); // Link it
                 // We don't need to save entity again if transaction is open, but being safe
             }
        }
        return entity; 
    }
    
    public void deleteCliente(PosCliente entity) { clienteRepository.delete(entity); }


}
