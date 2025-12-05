package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Dashboard Financiero | Contabilidad")
@Route(value = "accounting/dashboard", layout = MainLayout.class)
@PermitAll
public class FinancialDashboardView extends VerticalLayout {
    public FinancialDashboardView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Dashboard Financiero"));
    }
}
