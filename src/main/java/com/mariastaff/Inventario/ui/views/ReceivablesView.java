package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Cuentas por Cobrar | Ventas")
@Route(value = "sales/receivables", layout = MainLayout.class)
@PermitAll
public class ReceivablesView extends VerticalLayout {
    public ReceivablesView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Cuentas por Cobrar"));
    }
}
