package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@PageTitle("Dashboard Inventario | Inventario")
@Route(value = "inventory/dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class InventoryDashboardView extends VerticalLayout {
    public InventoryDashboardView() {
        addClassNames("w-full", "h-full", "bg-[var(--color-bg-secondary)]", "p-6");
        add(new AppLabel("view.inventory.dashboard.coming_soon"));
    }
}
