package com.mariastaff.Inventario.ui.views;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Nueva Compra | Compras")
@Route(value = "purchases/new", layout = MainLayout.class)
@PermitAll
public class NewPurchaseView extends VerticalLayout {
    public NewPurchaseView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Nueva Compra"));
    }
}
