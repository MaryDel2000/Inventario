package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.repository.InvProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final InvProductoRepository repository;

    public ProductoService(InvProductoRepository repository) {
        this.repository = repository;
    }

    public List<InvProducto> findAll() {
        return repository.findAll();
    }
    
    public InvProducto save(InvProducto producto) {
        return repository.save(producto);
    }
    
    public void deleteProducto(InvProducto entity) { repository.delete(entity); }
    public long countProductos() { return repository.count(); }
}
