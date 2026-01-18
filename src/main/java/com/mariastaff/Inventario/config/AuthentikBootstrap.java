package com.mariastaff.Inventario.config;

import com.mariastaff.Inventario.backend.service.AuthentikService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador de Authentik al arranque de la aplicación.
 * Asegura que todos los roles/entitlements requeridos por la aplicación existan en el proveedor.
 */
@Component
public class AuthentikBootstrap implements CommandLineRunner {

    private final AuthentikService authentikService;

    public AuthentikBootstrap(AuthentikService authentikService) {
        this.authentikService = authentikService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- AuthentikBootstrap: Iniciando sincronización de roles ---");
        // Al iniciar la aplicación, aseguramos que los entitlements base existan.
        // Esto usa el token de sistema (Service Account) para operar sin usuario logueado.
        authentikService.ensureEntitlementsExist(AppRoles.DEFINITIONS);
        System.out.println("--- AuthentikBootstrap: Finalizado ---");
    }
}
