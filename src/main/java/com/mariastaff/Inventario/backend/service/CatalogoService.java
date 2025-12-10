package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvCategoria;
import com.mariastaff.Inventario.backend.data.entity.InvImpuesto;
import com.mariastaff.Inventario.backend.data.entity.InvListaPrecio;
import com.mariastaff.Inventario.backend.data.entity.InvPromocion;
import com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida;
import com.mariastaff.Inventario.backend.data.repository.InvCategoriaRepository;
import com.mariastaff.Inventario.backend.data.repository.InvImpuestoRepository;
import com.mariastaff.Inventario.backend.data.repository.InvListaPrecioRepository;
import com.mariastaff.Inventario.backend.data.repository.InvPromocionRepository;
import com.mariastaff.Inventario.backend.data.repository.InvUnidadMedidaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CatalogoService {

    private final InvCategoriaRepository categoriaRepository;
    private final InvUnidadMedidaRepository unidadMedidaRepository;
    private final InvImpuestoRepository impuestoRepository;
    private final InvListaPrecioRepository listaPrecioRepository;
    private final InvPromocionRepository promocionRepository;

    public CatalogoService(InvCategoriaRepository categoriaRepository, 
                           InvUnidadMedidaRepository unidadMedidaRepository,
                           InvImpuestoRepository impuestoRepository,
                           InvListaPrecioRepository listaPrecioRepository,
                           InvPromocionRepository promocionRepository) {
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.impuestoRepository = impuestoRepository;
        this.listaPrecioRepository = listaPrecioRepository;
        this.promocionRepository = promocionRepository;
    }

    public List<InvCategoria> findAllCategorias() { return categoriaRepository.findAll(); }
    public InvCategoria saveCategoria(InvCategoria entity) { return categoriaRepository.save(entity); }
    public void deleteCategoria(InvCategoria entity) { categoriaRepository.delete(entity); }

    public List<InvUnidadMedida> findAllUnidadesMedida() { return unidadMedidaRepository.findAll(); }
    public InvUnidadMedida saveUnidadMedida(InvUnidadMedida entity) { return unidadMedidaRepository.save(entity); }
    public void deleteUnidadMedida(InvUnidadMedida entity) { unidadMedidaRepository.delete(entity); }

    public List<InvImpuesto> findAllImpuestos() { return impuestoRepository.findAll(); }
    public InvImpuesto saveImpuesto(InvImpuesto entity) { return impuestoRepository.save(entity); }
    public void deleteImpuesto(InvImpuesto entity) { impuestoRepository.delete(entity); }

    public List<InvListaPrecio> findAllListasPrecio() { return listaPrecioRepository.findAll(); }
    public InvListaPrecio saveListaPrecio(InvListaPrecio entity) { return listaPrecioRepository.save(entity); }
    public void deleteListaPrecio(InvListaPrecio entity) { listaPrecioRepository.delete(entity); }

    public List<InvPromocion> findAllPromociones() { return promocionRepository.findAll(); }
    public InvPromocion savePromocion(InvPromocion entity) { return promocionRepository.save(entity); }
    public void deletePromocion(InvPromocion entity) { promocionRepository.delete(entity); }
}
