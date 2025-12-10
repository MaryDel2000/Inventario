package com.mariastaff.Inventario.ui.views.purchases;

import com.mariastaff.Inventario.backend.data.entity.InvProveedor;
import com.mariastaff.Inventario.backend.service.CompraService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Proveedores | Compras")
@Route(value = "purchases/providers", layout = MainLayout.class)
@PermitAll
public class ProvidersView extends VerticalLayout {

    private final CompraService service;
    private final Grid<InvProveedor> grid = new Grid<>(InvProveedor.class);

    public ProvidersView(CompraService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Listado de Proveedores"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-white", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(p -> p.getEntidad() != null ? p.getEntidad().getNombreCompleto() : "-").setHeader("Nombre");
        grid.addColumn(p -> p.getEntidad() != null ? p.getEntidad().getIdentificacion() : "-").setHeader("Identificación");
        grid.addColumn(p -> p.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllProveedores());
    }
}
