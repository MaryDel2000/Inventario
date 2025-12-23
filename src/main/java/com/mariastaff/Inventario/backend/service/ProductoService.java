package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.repository.InvProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final InvProductoRepository repository;
    private final com.mariastaff.Inventario.backend.data.repository.InvProductoVarianteRepository varianteRepository;
    private final com.mariastaff.Inventario.backend.data.repository.InvLoteRepository loteRepository;

    public ProductoService(InvProductoRepository repository, 
                           com.mariastaff.Inventario.backend.data.repository.InvProductoVarianteRepository varianteRepository,
                           com.mariastaff.Inventario.backend.data.repository.InvLoteRepository loteRepository) {
        this.repository = repository;
        this.varianteRepository = varianteRepository;
        this.loteRepository = loteRepository;
    }

    public java.util.Optional<InvProducto> findById(Long id) {
        return repository.findById(id);
    }

    public List<InvProducto> findAll() {
        return repository.findAll();
    }
    
    public InvProducto save(InvProducto producto) {
        return repository.save(producto);
    }
    
    public void deleteProducto(InvProducto entity) {
        // Find all variants
        List<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> variantes = varianteRepository.findByProducto(entity);
        for (com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante : variantes) {
            deleteVariante(variante);
        }
        repository.delete(entity);
    }
    public long countProductos() { return repository.count(); }

    // Variantes
    public List<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> findAllVariantes() {
        return varianteRepository.findAll();
    }
    
    public List<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> findVariantesByProducto(InvProducto producto) {
        return varianteRepository.findByProducto(producto);
    }

    public com.mariastaff.Inventario.backend.data.entity.InvProductoVariante saveVariante(com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante) {
        return varianteRepository.save(variante);
    }

    public void deleteVariante(com.mariastaff.Inventario.backend.data.entity.InvProductoVariante variante) {
        // Find all batches (lotes) for this variant
        List<com.mariastaff.Inventario.backend.data.entity.InvLote> lotes = loteRepository.findByProductoVariante(variante);
        loteRepository.deleteAll(lotes);
        varianteRepository.delete(variante);
    }
}
