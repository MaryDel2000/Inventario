package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Lotes | Inventario")
@Route(value = "inventory/batches", layout = MainLayout.class)
@PermitAll
public class BatchesView extends VerticalLayout {

    private final AlmacenService service;
    private final Grid<InvLote> grid = new Grid<>(InvLote.class);

    public BatchesView(AlmacenService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Lotes y Caducidades"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-white", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("codigoLote", "fechaCaducidad", "observaciones");
        // We assume product variant can be displayed, if properly fetched.
        // grid.addColumn(l -> l.getProductoVariante()...).setHeader("Producto");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllLotes());
    }
}
