package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvMovimiento;
import com.mariastaff.Inventario.backend.service.MovimientoService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Movimientos de Stock | Inventario")
@Route(value = "inventory/movements", layout = MainLayout.class)
@PermitAll
public class MovementsView extends VerticalLayout {

    private final MovimientoService service;
    private final Grid<InvMovimiento> grid = new Grid<>(InvMovimiento.class);

    public MovementsView(MovimientoService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Movimientos de Inventario"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("tipoMovimiento", "fechaMovimiento", "observaciones");
        grid.addColumn(m -> m.getSucursal() != null ? m.getSucursal().getNombre() : "-").setHeader("Sucursal");
        grid.addColumn(m -> m.getAlmacenOrigen() != null ? m.getAlmacenOrigen().getNombre() : "-").setHeader("Origen");
        grid.addColumn(m -> m.getAlmacenDestino() != null ? m.getAlmacenDestino().getNombre() : "-").setHeader("Destino");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllMovimientos());
    }
}
