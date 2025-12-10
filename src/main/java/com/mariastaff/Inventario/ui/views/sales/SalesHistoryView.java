package com.mariastaff.Inventario.ui.views.sales;

import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import com.mariastaff.Inventario.backend.service.PosService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Historial de Ventas | Ventas")
@Route(value = "sales/history", layout = MainLayout.class)
@PermitAll
public class SalesHistoryView extends VerticalLayout {

    private final PosService service;
    private final Grid<PosVenta> grid = new Grid<>(PosVenta.class);

    public SalesHistoryView(PosService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Historial de Ventas"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-white", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("numeroDocumento", "fechaHora", "totalNeto", "estado", "estadoPago");
        grid.addColumn(v -> v.getCliente() != null ? v.getCliente().getEntidad().getNombreCompleto() : "Consumidor Final").setHeader("Cliente");
        grid.addColumn(v -> v.getUsuarioVendedor() != null ? v.getUsuarioVendedor().getUsername() : "-").setHeader("Vendedor");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllVentas());
    }
}
