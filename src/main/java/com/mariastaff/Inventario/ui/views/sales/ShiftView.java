package com.mariastaff.Inventario.ui.views.sales;

import com.mariastaff.Inventario.backend.data.entity.PosTurno;
import com.mariastaff.Inventario.backend.service.PosService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Turnos y Cierres | Ventas")
@Route(value = "sales/shifts", layout = MainLayout.class)
@PermitAll
public class ShiftView extends VerticalLayout {

    private final PosService service;
    private final Grid<PosTurno> grid = new Grid<>(PosTurno.class);

    public ShiftView(PosService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Turnos de Caja"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("fechaHoraApertura", "fechaHoraCierre", "montoInicialEfectivo", "diferencia", "estado");
        // grid.addColumn(t -> t.getUsuarioCajero().getUsername()).setHeader("Cajero"); // Assuming getter exists
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllTurnos());
    }
}
