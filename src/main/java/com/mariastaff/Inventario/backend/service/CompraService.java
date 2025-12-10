package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.InvCompra;
import com.mariastaff.Inventario.backend.data.entity.InvProveedor;
import com.mariastaff.Inventario.backend.data.repository.InvCompraRepository;
import com.mariastaff.Inventario.backend.data.repository.InvProveedorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompraService {

    private final InvCompraRepository compraRepository;
    private final InvProveedorRepository proveedorRepository;

    public CompraService(InvCompraRepository compraRepository, InvProveedorRepository proveedorRepository) {
        this.compraRepository = compraRepository;
        this.proveedorRepository = proveedorRepository;
    }

    public List<InvCompra> findAllCompras() { return compraRepository.findAll(); }
    public InvCompra saveCompra(InvCompra entity) { return compraRepository.save(entity); }
    public void deleteCompra(InvCompra entity) { compraRepository.delete(entity); }

    public List<InvProveedor> findAllProveedores() { return proveedorRepository.findAll(); }
    public InvProveedor saveProveedor(InvProveedor entity) { return proveedorRepository.save(entity); }
    public void deleteProveedor(InvProveedor entity) { proveedorRepository.delete(entity); }
}
