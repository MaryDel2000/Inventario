package com.mariastaff.Inventario.ui.views.accounting;

import com.mariastaff.Inventario.backend.data.entity.ContCuenta;
import com.mariastaff.Inventario.backend.service.ContabilidadService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Plan de Cuentas | Contabilidad")
@Route(value = "accounting/chart-of-accounts", layout = MainLayout.class)
@PermitAll
public class ChartOfAccountsView extends VerticalLayout {

    private final ContabilidadService service;
    private final Grid<ContCuenta> grid = new Grid<>(ContCuenta.class);

    public ChartOfAccountsView(ContabilidadService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Plan de Cuentas"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-white", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("codigo", "nombre", "tipo", "nivel");
        grid.addColumn(c -> c.getActiva() ? "Sí" : "No").setHeader("Activa");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllCuentas());
    }
}
