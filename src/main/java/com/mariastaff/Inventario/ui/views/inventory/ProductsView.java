package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Productos | Inventario")
@Route(value = "inventory/products", layout = MainLayout.class)
@PermitAll
public class ProductsView extends VerticalLayout {
    public ProductsView() {
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        add(new AppLabel("Próximamente Productos"));
    }
}
