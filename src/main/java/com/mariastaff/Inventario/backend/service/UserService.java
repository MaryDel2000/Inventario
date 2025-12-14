package com.mariastaff.Inventario.backend.service;

import com.mariastaff.Inventario.backend.data.entity.SysUsuario;
import com.mariastaff.Inventario.backend.data.repository.SysUsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final SysUsuarioRepository usuarioRepository;

    public UserService(SysUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<SysUsuario> findAll() { return usuarioRepository.findAll(); }
    public SysUsuario save(SysUsuario entity) { return usuarioRepository.save(entity); }
    public void delete(SysUsuario entity) { usuarioRepository.delete(entity); }
}
