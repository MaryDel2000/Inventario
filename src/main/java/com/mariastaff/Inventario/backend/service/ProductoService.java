package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.repository.InvProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final InvProductoRepository repository;
    private final com.mariastaff.Inventario.backend.data.repository.InvProductoVarianteRepository varianteRepository;

    public ProductoService(InvProductoRepository repository, com.mariastaff.Inventario.backend.data.repository.InvProductoVarianteRepository varianteRepository) {
        this.repository = repository;
        this.varianteRepository = varianteRepository;
    }

    public List<InvProducto> findAll() {
        return repository.findAll();
    }
    
    public InvProducto save(InvProducto producto) {
        return repository.save(producto);
    }
    
    public void deleteProducto(InvProducto entity) { repository.delete(entity); }
    public long countProductos() { return repository.count(); }

    // Variantes
    public List<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> findAllVariantes() {
        return varianteRepository.findAll();
    }

    public com.mariastaff.Inventario.backend.data.entity.InvProductoVariante saveVariante(com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante) {
        return varianteRepository.save(variante);
    }

    public void deleteVariante(com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante) {
        varianteRepository.delete(variante);
    }
}
