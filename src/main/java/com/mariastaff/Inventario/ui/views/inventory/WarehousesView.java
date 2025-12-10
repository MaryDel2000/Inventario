package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Almacenes | Inventario")
@Route(value = "inventory/warehouses", layout = MainLayout.class)
@PermitAll
public class WarehousesView extends VerticalLayout {

    private final AlmacenService service;
    private final Grid<InvAlmacen> grid = new Grid<>(InvAlmacen.class);

    public WarehousesView(AlmacenService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Listado de Almacenes"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-white", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "codigo", "tipoAlmacen", "direccion");
        grid.addColumn(a -> a.getSucursal() != null ? a.getSucursal().getNombre() : "Global").setHeader("Sucursal");
        grid.addColumn(a -> a.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllAlmacenes());
    }
}
