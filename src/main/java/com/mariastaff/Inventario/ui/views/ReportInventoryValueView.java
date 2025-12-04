package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Valoración de Inventario | Reportes")
@Route(value = "reports/inventory-value", layout = MainLayout.class)
@PermitAll
public class ReportInventoryValueView extends VerticalLayout {
    public ReportInventoryValueView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Valoración de Inventario"));
    }
}
