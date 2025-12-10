package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Roles y Permisos | Configuración")
@Route(value = "settings/roles", layout = MainLayout.class)
@PermitAll
public class RolesView extends VerticalLayout {

    public RolesView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Roles y Permisos"));
        add(new Paragraph("La gestión de roles y permisos se realiza centralizadamente a través de Authentik."));
    }
}
