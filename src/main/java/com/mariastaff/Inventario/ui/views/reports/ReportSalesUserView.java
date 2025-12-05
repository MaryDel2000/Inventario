package com.mariastaff.Inventario.ui.views.reports;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Ventas por Usuario | Reportes")
@Route(value = "reports/sales-user", layout = MainLayout.class)
@PermitAll
public class ReportSalesUserView extends VerticalLayout {
    public ReportSalesUserView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Ventas por Usuario"));
    }
}
