package com.mariastaff.Inventario.ui.views.accounting;

import com.mariastaff.Inventario.backend.data.entity.ContAsiento;
import com.mariastaff.Inventario.backend.service.ContabilidadService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Libro Diario | Contabilidad")
@Route(value = "accounting/journal", layout = MainLayout.class)
@PermitAll
public class JournalView extends VerticalLayout {

    private final ContabilidadService service;
    private final Grid<ContAsiento> grid = new Grid<>(ContAsiento.class);

    public JournalView(ContabilidadService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Libro Diario"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("fecha", "descripcion", "origen");
        grid.addColumn(a -> a.getSucursal() != null ? a.getSucursal().getNombre() : "-").setHeader("Sucursal");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllAsientos());
    }
}
