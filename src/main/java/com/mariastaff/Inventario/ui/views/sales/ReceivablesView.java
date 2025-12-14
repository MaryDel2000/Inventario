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

@PageTitle("Cuentas por Cobrar | Ventas")
@Route(value = "sales/receivables", layout = MainLayout.class)
@PermitAll
public class ReceivablesView extends VerticalLayout {

    private final PosService service;
    private final Grid<PosVenta> grid = new Grid<>(PosVenta.class);

    public ReceivablesView(PosService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Cuentas por Cobrar1"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("numeroDocumento", "fechaHora", "totalNeto", "estadoPago");
        grid.addColumn(v -> v.getCliente() != null ? v.getCliente().getEntidad().getNombreCompleto() : "Anónimo").setHeader("Cliente");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findVentasPorCobrar());
    }
}
