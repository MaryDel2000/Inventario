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
@com.vaadin.flow.router.RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class InventoryDashboardView extends VerticalLayout {

    private final ProductoService productoService;
    private final AlmacenService almacenService;
    private final MovimientoService movimientoService;
    private final com.mariastaff.Inventario.backend.service.PosService posService;

    public InventoryDashboardView(ProductoService productoService, AlmacenService almacenService, MovimientoService movimientoService, com.mariastaff.Inventario.backend.service.PosService posService) {
        this.productoService = productoService;
        this.almacenService = almacenService;
        this.movimientoService = movimientoService;
        this.posService = posService;
        
        addClassNames("w-full", "min-h-full", "bg-bg-secondary", "p-6", "box-border");
        
        add(new AppLabel("view.inventory.dashboard.title"));
        
        HorizontalLayout stats = new HorizontalLayout();
        stats.addClassNames("w-full", "gap-4", "flex-wrap");
        
        stats.add(new com.mariastaff.Inventario.ui.components.composite.StatCard(getTranslation("view.inventory.card.products"), String.valueOf(productoService.countProductos())));
        stats.add(new com.mariastaff.Inventario.ui.components.composite.StatCard(getTranslation("view.inventory.card.warehouses"), String.valueOf(almacenService.countAlmacenes())));
        stats.add(new com.mariastaff.Inventario.ui.components.composite.StatCard(getTranslation("view.inventory.card.stock"), String.valueOf(almacenService.countExistencias())));
        stats.add(new com.mariastaff.Inventario.ui.components.composite.StatCard(getTranslation("view.inventory.card.movements"), String.valueOf(movimientoService.countMovimientos())));
        
        add(stats);
        
        com.vaadin.flow.component.html.Div chartsGrid = new com.vaadin.flow.component.html.Div();
        chartsGrid.addClassNames("grid", "grid-cols-1", "md:grid-cols-2", "gap-10", "mt-6", "w-full");

        com.mariastaff.Inventario.ui.components.charts.SalesByTurnoChart salesChart = new com.mariastaff.Inventario.ui.components.charts.SalesByTurnoChart(posService.getSalesByTurno());
        com.mariastaff.Inventario.ui.components.charts.StockByWarehouseChart stockChart = new com.mariastaff.Inventario.ui.components.charts.StockByWarehouseChart(almacenService.getStockByAlmacen());

        com.vaadin.flow.component.html.Div salesCard = new com.vaadin.flow.component.html.Div();
        salesCard.addClassNames("bg-transparent", "p-4", "rounded-xl", "flex", "flex-col", "min-w-0");
        
        com.vaadin.flow.component.html.H3 salesTitle = new com.vaadin.flow.component.html.H3(getTranslation("chart.sales_by_turno.title", "Ventas por Cierre de Turno"));
        salesTitle.addClassNames("text-lg", "font-semibold", "mb-4", "text-gray-900", "dark:text-white");
        
        salesCard.add(salesTitle, salesChart);
        
        com.vaadin.flow.component.html.Div stockCard = new com.vaadin.flow.component.html.Div();
        stockCard.addClassNames("bg-transparent", "p-4", "rounded-xl", "flex", "flex-col", "min-w-0");
        
        com.vaadin.flow.component.html.H3 stockTitle = new com.vaadin.flow.component.html.H3("Distribución de Stock");
        stockTitle.addClassNames("text-lg", "font-semibold", "mb-4", "text-gray-900", "dark:text-white");
        
        stockCard.add(stockTitle, stockChart);

        chartsGrid.add(salesCard, stockCard);
        
        add(chartsGrid);
    }
}
