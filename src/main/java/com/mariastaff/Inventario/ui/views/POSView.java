package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Punto de Venta | Inventario")
@Route(value = "pos", layout = MainLayout.class)
@PermitAll
public class POSView extends VerticalLayout {

    public POSView() {
        addClassNames("w-full", "h-full", "bg-[var(--color-bg-secondary)]", "p-6");
        setSpacing(false);
        setPadding(false);

        add(new AppLabel("Próximamente Punto de Venta"));
    }
}
