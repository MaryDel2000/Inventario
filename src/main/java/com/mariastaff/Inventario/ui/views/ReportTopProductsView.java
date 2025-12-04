package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Productos más vendidos | Reportes")
@Route(value = "reports/top-products", layout = MainLayout.class)
@PermitAll
public class ReportTopProductsView extends VerticalLayout {
    public ReportTopProductsView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Productos más vendidos"));
    }
}
