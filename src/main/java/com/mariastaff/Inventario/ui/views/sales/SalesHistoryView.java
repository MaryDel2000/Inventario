package com.mariastaff.Inventario.ui.views.sales;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Historial de Ventas | Ventas")
@Route(value = "sales/history", layout = MainLayout.class)
@PermitAll
public class SalesHistoryView extends VerticalLayout {
    public SalesHistoryView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Historial de Ventas"));
    }
}
