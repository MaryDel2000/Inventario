package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Impuestos | Configuración")
@Route(value = "settings/taxes", layout = MainLayout.class)
@PermitAll
public class TaxesView extends VerticalLayout {
    public TaxesView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Impuestos"));
    }
}
