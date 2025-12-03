package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Historial de Compras | Compras")
@Route(value = "purchases/history", layout = MainLayout.class)
@PermitAll
public class PurchasesHistoryView extends VerticalLayout {
    public PurchasesHistoryView() {
        addClassNames("w-full", "h-full", "bg-[var(--color-bg-secondary)]", "p-6");
        add(new AppLabel("Próximamente Historial de Compras"));
    }
}
