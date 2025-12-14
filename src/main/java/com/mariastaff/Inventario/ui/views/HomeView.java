package com.mariastaff.Inventario.ui.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("")
@PermitAll
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return;
        }

        // Lógica de redirección basada en roles
        // Puedes personalizar los roles y las rutas según tus necesidades
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (isAdmin) {
            // Redirigir a Dashboard para administradores
            event.forwardTo("inventory/dashboard");
        } else {
            // Redirigir a otra vista por defecto para otros usuarios (ej. POS o Ventas)
            // Ajusta esta ruta según la vista que desees para usuarios normales
            event.forwardTo("inventory/dashboard"); 
        }
    }
}
