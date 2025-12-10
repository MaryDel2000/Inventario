package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.backend.service.MovimientoService;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Dashboard Inventario | Inventario")
@Route(value = "inventory/dashboard", layout = MainLayout.class)
@PermitAll
public class InventoryDashboardView extends VerticalLayout {

    private final ProductoService productoService;
    private final AlmacenService almacenService;
    private final MovimientoService movimientoService;

    public InventoryDashboardView(ProductoService productoService, AlmacenService almacenService, MovimientoService movimientoService) {
        this.productoService = productoService;
        this.almacenService = almacenService;
        this.movimientoService = movimientoService;
        
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        add(new AppLabel("view.inventory.dashboard.title"));
        
        HorizontalLayout stats = new HorizontalLayout();
        stats.addClassNames("w-full", "gap-4", "flex-wrap");
        
        stats.add(createCard(getTranslation("view.inventory.card.products"), String.valueOf(productoService.countProductos())));
        stats.add(createCard(getTranslation("view.inventory.card.warehouses"), String.valueOf(almacenService.countAlmacenes())));
        stats.add(createCard(getTranslation("view.inventory.card.stock"), String.valueOf(almacenService.countExistencias())));
        stats.add(createCard(getTranslation("view.inventory.card.movements"), String.valueOf(movimientoService.countMovimientos())));
        
        add(stats);
    }
    
    private VerticalLayout createCard(String title, String value) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames("bg-white", "rounded-lg", "shadow", "p-6", "items-center", "justify-center");
        card.setWidth("200px");
        
        Span valSpan = new Span(value);
        valSpan.addClassNames("text-4xl", "font-bold", "text-primary");
        
        Span titleSpan = new Span(title);
        titleSpan.addClassNames("text-secondary", "font-medium");
        
        card.add(valSpan, titleSpan);
        return card;
    }
}
