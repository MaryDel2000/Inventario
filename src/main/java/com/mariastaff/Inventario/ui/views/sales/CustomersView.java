package com.mariastaff.Inventario.ui.views.sales;

import com.mariastaff.Inventario.backend.data.entity.PosCliente;
import com.mariastaff.Inventario.backend.service.PosService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Clientes | Ventas")
@Route(value = "sales/customers", layout = MainLayout.class)
@PermitAll
public class CustomersView extends VerticalLayout {

    private final PosService service;
    private final Grid<PosCliente> grid = new Grid<>(PosCliente.class);

    public CustomersView(PosService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Listado de Clientes"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("limiteCredito", "diasCredito");
        grid.addColumn(c -> c.getEntidad() != null ? c.getEntidad().getNombreCompleto() : "-").setHeader("Nombre");
        grid.addColumn(c -> c.getEntidad() != null ? c.getEntidad().getIdentificacion() : "-").setHeader("Identificación");
        grid.addColumn(c -> c.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllClientes());
    }
}
