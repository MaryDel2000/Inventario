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

import java.util.stream.Collectors;

@PageTitle("Cierres de Caja | Ventas")
@Route(value = "sales/closures", layout = MainLayout.class)
@PermitAll
public class ClosuresView extends VerticalLayout {

    private final PosService service;
    private final Grid<PosTurno> grid = new Grid<>(PosTurno.class);

    public ClosuresView(PosService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(new AppLabel("Cierres de Caja (Histórico)"), grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-white", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("fechaHoraCierre", "montoFinalEfectivoDeclarado", "diferencia", "estado");
        grid.addColumn(t -> t.getUsuarioCajero() != null ? t.getUsuarioCajero().getUsername() : "-").setHeader("Cajero");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        // Filter only closed shifts. Ideally service should do this.
        grid.setItems(service.findAllTurnos().stream()
                .filter(t -> "CERRADO".equals(t.getEstado()))
                .collect(Collectors.toList()));
    }
}
