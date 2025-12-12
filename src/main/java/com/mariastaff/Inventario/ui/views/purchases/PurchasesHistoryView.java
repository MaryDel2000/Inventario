package com.mariastaff.Inventario.ui.views.purchases;

import com.mariastaff.Inventario.backend.data.entity.InvCompra;
import com.mariastaff.Inventario.backend.service.CompraService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Historial de Compras | Compras")
@Route(value = "purchases/history", layout = MainLayout.class)
@PermitAll
public class PurchasesHistoryView extends VerticalLayout {

    private final CompraService service;
    private final Grid<InvCompra> grid = new Grid<>(InvCompra.class);

    public PurchasesHistoryView(CompraService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Historial de Compras"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("numeroDocumento", "fechaCompra", "totalCompra", "estado", "tipoDocumento");
        grid.addColumn(c -> c.getProveedor() != null ? c.getProveedor().getEntidad().getNombreCompleto() : "-").setHeader("Proveedor");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllCompras());
    }
}
