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

    @org.springframework.transaction.annotation.Transactional
    public void generateDemoData(com.mariastaff.Inventario.backend.data.entity.SysUsuario currentUser) {
        // 1. Create Sample Clients if empty
        if (clienteRepository.count() < 5) {
            String[] names = {"Juan Pérez", "Maria Garcia", "Pedro Lopez", "Ana Martinez", "Carlos Sanchez"};
            for (String name : names) {
                com.mariastaff.Inventario.backend.data.entity.GenEntidad entidad = new com.mariastaff.Inventario.backend.data.entity.GenEntidad();
                entidad.setNombreCompleto(name);
                entidad.setTipoEntidad("PERSONA");
                entidad = entidadRepository.save(entidad);
                
                PosCliente cliente = new PosCliente();
                cliente.setEntidad(entidad);
                clienteRepository.save(cliente);
            }
        }

        // 2. Determine a Box (Caja)
        List<PosCaja> cajas = cajaRepository.findAll();
        PosCaja caja = cajas.isEmpty() ? saveCaja(new PosCaja()) : cajas.get(0);
        if (cajas.isEmpty()) { caja.setNombre("Caja Principal"); cajaRepository.save(caja); }

        // 3. Create a Closed Shift (Historical)
        PosTurno closedTurno = new PosTurno();
        closedTurno.setCaja(caja);
        closedTurno.setUsuarioCajero(currentUser);
        closedTurno.setFechaHoraApertura(java.time.LocalDateTime.now().minusDays(1).withHour(8));
        closedTurno.setFechaHoraCierre(java.time.LocalDateTime.now().minusDays(1).withHour(17));
        closedTurno.setMontoInicialEfectivo(new java.math.BigDecimal("1000.00"));
        closedTurno.setMontoFinalEfectivoDeclarado(new java.math.BigDecimal("5000.00"));
        closedTurno.setEstado("CERRADO");
        turnoRepository.save(closedTurno);

        // 4. Create Sales for the Closed Shift
        List<PosCliente> clientes = clienteRepository.findAll();
        List<com.mariastaff.Inventario.backend.data.entity.InvExistencia> stock = existenciaRepository.findAll();
        // Filter stock > 0
        stock = stock.stream().filter(e -> e.getCantidadDisponible().compareTo(java.math.BigDecimal.ZERO) > 0).collect(java.util.stream.Collectors.toList());

        if (stock.isEmpty()) return; // No stock to sell

        java.util.Random rand = new java.util.Random();
        java.math.BigDecimal totalCalculado = closedTurno.getMontoInicialEfectivo();

        for (int i = 0; i < 5; i++) {
            PosVenta venta = new PosVenta();
            venta.setCliente(clientes.get(rand.nextInt(clientes.size())));
            venta.setUsuarioVendedor(currentUser);
            venta.setTurno(closedTurno); // Link to shift
            venta.setFechaHora(closedTurno.getFechaHoraApertura().plusHours(rand.nextInt(8)));
            venta.setAlmacenSalida(stock.get(0).getAlmacen()); // Simplified
            venta.setEstado("CERRADO");
            venta.setEstadoPago("PAGADO - EFECTIVO");

            PosVentaDetalle detalle = new PosVentaDetalle();
            com.mariastaff.Inventario.backend.data.entity.InvExistencia itemStock = stock.get(rand.nextInt(stock.size()));
            detalle.setProductoVariante(itemStock.getProductoVariante());
            detalle.setCantidad(java.math.BigDecimal.ONE);
            detalle.setPrecioUnitario(productoService.getPrecioVentaActual(detalle.getProductoVariante(), "USD"));
            detalle.setSubtotal(detalle.getPrecioUnitario().multiply(detalle.getCantidad()));
            
            venta.addDetalle(detalle);
            venta.setTotalBruto(detalle.getSubtotal());
            venta.setTotalNeto(detalle.getSubtotal());
            venta.setImpuestosTotal(java.math.BigDecimal.ZERO);
            venta.setDescuentoTotal(java.math.BigDecimal.ZERO);
            
            saveVenta(venta);
            totalCalculado = totalCalculado.add(venta.getTotalNeto());
        }
        
        closedTurno.setMontoFinalEfectivoCalculado(totalCalculado);
        closedTurno.setDiferencia(closedTurno.getMontoFinalEfectivoDeclarado().subtract(totalCalculado));
        turnoRepository.save(closedTurno);
    }
}
