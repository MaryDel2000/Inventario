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
}
