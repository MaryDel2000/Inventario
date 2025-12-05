package com.mariastaff.Inventario.ui.views.sales;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Terminal Punto de Venta | Ventas")
@Route(value = "sales/pos", layout = MainLayout.class)
@PermitAll
public class POSView extends VerticalLayout {
    public POSView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Terminal Punto de Venta"));
    }
}
