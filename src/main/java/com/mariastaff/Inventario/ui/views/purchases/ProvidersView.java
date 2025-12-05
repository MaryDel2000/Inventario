package com.mariastaff.Inventario.ui.views.purchases;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Proveedores | Compras")
@Route(value = "purchases/providers", layout = MainLayout.class)
@PermitAll
public class ProvidersView extends VerticalLayout {
    public ProvidersView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Proveedores"));
    }
}
