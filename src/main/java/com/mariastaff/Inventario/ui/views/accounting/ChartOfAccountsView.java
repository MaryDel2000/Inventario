package com.mariastaff.Inventario.ui.views.accounting;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Catálogo de Cuentas | Contabilidad")
@Route(value = "accounting/chart", layout = MainLayout.class)
@PermitAll
public class ChartOfAccountsView extends VerticalLayout {
    public ChartOfAccountsView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Catálogo de Cuentas"));
    }
}
