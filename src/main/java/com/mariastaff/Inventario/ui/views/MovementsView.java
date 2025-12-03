package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Movimientos | Inventario")
@Route(value = "inventory/movements", layout = MainLayout.class)
@PermitAll
public class MovementsView extends VerticalLayout {
    public MovementsView() {
        addClassNames("w-full", "h-full", "bg-[var(--color-bg-secondary)]", "p-6");
        add(new AppLabel("Próximamente Movimientos"));
    }
}
