package com.mariastaff.Inventario.backend.data.repository;

import com.mariastaff.Inventario.backend.data.entity.SysUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysUsuarioRepository extends JpaRepository<SysUsuario, Long> {
    SysUsuario findByUsername(String username);
    SysUsuario findByAuthentikUuid(String authentikUuid);
}
