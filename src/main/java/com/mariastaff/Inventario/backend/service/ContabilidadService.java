package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.ContAsiento;
import com.mariastaff.Inventario.backend.data.entity.ContCuenta;
import com.mariastaff.Inventario.backend.data.entity.ContPeriodo;
import com.mariastaff.Inventario.backend.data.repository.ContAsientoRepository;
import com.mariastaff.Inventario.backend.data.repository.ContCuentaRepository;
import com.mariastaff.Inventario.backend.data.repository.ContPeriodoRepository;
import org.springframework.stereotype.Service;
import com.mariastaff.Inventario.backend.data.entity.ContAsientoDetalle;
import java.util.List;
import java.math.BigDecimal;
import jakarta.transaction.Transactional; 
import com.mariastaff.Inventario.backend.data.repository.ContAsientoDetalleRepository;

@Service
public class ContabilidadService {

    private final ContAsientoRepository asientoRepository;
    private final ContCuentaRepository cuentaRepository;
    private final ContPeriodoRepository periodoRepository;

    private final ContAsientoDetalleRepository asientoDetalleRepository;

    public ContabilidadService(ContAsientoRepository asientoRepository, 
                               ContCuentaRepository cuentaRepository, 
                               ContPeriodoRepository periodoRepository,
                               ContAsientoDetalleRepository asientoDetalleRepository) {
        this.asientoRepository = asientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.periodoRepository = periodoRepository;
        this.asientoDetalleRepository = asientoDetalleRepository;
    }

    public List<ContAsiento> findAllAsientos() { return asientoRepository.findAll(); }
    
    @Transactional
    public ContAsiento saveAsiento(ContAsiento asiento, List<ContAsientoDetalle> detalles) {
        // 1. Validate Balance (Debe == Haber)
        BigDecimal totalDebe = detailsTotal(detalles, true);
        BigDecimal totalHaber = detailsTotal(detalles, false);
        
        if (totalDebe.compareTo(totalHaber) != 0) {
            throw new RuntimeException("El asiento está descuadrado: Debe=" + totalDebe + ", Haber=" + totalHaber);
        }

        // 2. Save Master
        ContAsiento savedAsiento = asientoRepository.save(asiento);

        // 3. Save Detalle
        for (ContAsientoDetalle detalle : detalles) {
            detalle.setAsiento(savedAsiento);
            asientoDetalleRepository.save(detalle);
        }
        
        return savedAsiento;
    }

    private BigDecimal detailsTotal(List<ContAsientoDetalle> detalles, boolean isDebe) {
        return detalles.stream()
                .map(d -> isDebe ? d.getDebe() : d.getHaber())
                .filter(val -> val != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void deleteAsiento(ContAsiento entity) { 
        // Logic to delete details first if cascade not configured
        List<ContAsientoDetalle> detalles = asientoDetalleRepository.findByAsiento(entity);
        asientoDetalleRepository.deleteAll(detalles);
        asientoRepository.delete(entity); 
    }
    
    public List<ContAsientoDetalle> findDetallesByAsiento(ContAsiento asiento) {
        return asientoDetalleRepository.findByAsiento(asiento);
    }

    public List<ContCuenta> findAllCuentas() { return cuentaRepository.findAll(); }
    public ContCuenta saveCuenta(ContCuenta entity) { return cuentaRepository.save(entity); }
    public void deleteCuenta(ContCuenta entity) { cuentaRepository.delete(entity); }

    public List<ContPeriodo> findAllPeriodos() { return periodoRepository.findAll(); }
    public ContPeriodo savePeriodo(ContPeriodo entity) { return periodoRepository.save(entity); }
    public void deletePeriodo(ContPeriodo entity) { periodoRepository.delete(entity); }

    public java.util.List<AccountBalanceDTO> getAccountBalances(java.time.LocalDate start, java.time.LocalDate end) {
        List<ContAsiento> asientos = asientoRepository.findAll();
        java.util.Map<Long, AccountBalanceDTO> map = new java.util.HashMap<>();
        
        // Initialize all accounts with 0
        List<ContCuenta> cuentas = cuentaRepository.findAll();
        for (ContCuenta c : cuentas) {
            map.put(c.getId(), new AccountBalanceDTO(c, BigDecimal.ZERO, BigDecimal.ZERO));
        }
        
        for (ContAsiento asiento : asientos) {
            java.time.LocalDate date = asiento.getFecha().toLocalDate();
            if (date.isBefore(start) || date.isAfter(end)) continue;
            
            List<ContAsientoDetalle> detalles = asientoDetalleRepository.findByAsiento(asiento);
            for (ContAsientoDetalle d : detalles) {
                if (d.getCuenta() == null) continue;
                
                AccountBalanceDTO dto = map.get(d.getCuenta().getId());
                if (dto != null) {
                    BigDecimal debe = d.getDebe() != null ? d.getDebe() : BigDecimal.ZERO;
                    BigDecimal haber = d.getHaber() != null ? d.getHaber() : BigDecimal.ZERO;
                    
                    dto.setDebe(dto.getDebe().add(debe));
                    dto.setHaber(dto.getHaber().add(haber));
                }
            }
        }
        
        return new java.util.ArrayList<>(map.values());
    }
    
    public static class AccountBalanceDTO {
        private ContCuenta cuenta;
        private BigDecimal debe;
        private BigDecimal haber;
        
        public AccountBalanceDTO(ContCuenta c, BigDecimal d, BigDecimal h) {
            this.cuenta = c;
            this.debe = d;
            this.haber = h;
        }
        
        public ContCuenta getCuenta() { return cuenta; }
        public BigDecimal getDebe() { return debe; }
        public void setDebe(BigDecimal d) { this.debe = d; }
        public BigDecimal getHaber() { return haber; }
        public void setHaber(BigDecimal h) { this.haber = h; }
        public BigDecimal getSaldo() { return debe.subtract(haber); } // Simple logic: Debit is positive
    }
}
