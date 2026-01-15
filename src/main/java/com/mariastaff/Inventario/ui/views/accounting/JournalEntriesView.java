package com.mariastaff.Inventario.ui.views.accounting;

import com.mariastaff.Inventario.backend.data.entity.ContAsiento;
import com.mariastaff.Inventario.backend.data.entity.ContAsientoDetalle;
import com.mariastaff.Inventario.backend.service.ContabilidadService;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
// import com.mariastaff.Inventario.ui.util.UIUtils; // Removed
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Route(value = "accounting/journal", layout = MainLayout.class)
@PageTitle("Asientos Contables")
@PermitAll
public class JournalEntriesView extends VerticalLayout {

    private final ContabilidadService contabilidadService;
    private Grid<ContAsiento> grid;
    private TextField filter;

    @Autowired
    public JournalEntriesView(ContabilidadService contabilidadService) {
        this.contabilidadService = contabilidadService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createHeader(), createContent());
        updateGrid();
    }

    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setSpacing(true);
        header.setPadding(true);
        header.setAlignItems(Alignment.CENTER);

        filter = new TextField();
        filter.setPlaceholder("Buscar por descripción...");
        filter.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        filter.addValueChangeListener(e -> updateGrid());
        filter.setClearButtonVisible(true);

        Button addBtn = new Button("Nuevo Asiento", new Icon(VaadinIcon.PLUS));
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // addBtn.addClickListener(e -> openDialog()); // TODO: Implement

        header.add(filter, addBtn);
        header.expand(filter);
        return header;
    }

    private Component createContent() {
        grid = new Grid<>(ContAsiento.class, false);
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        grid.addColumn(new LocalDateTimeRenderer<ContAsiento>(ContAsiento::getFecha, "dd/MM/yyyy HH:mm"))
            .setHeader("Fecha").setSortable(true).setAutoWidth(true);
        grid.addColumn(ContAsiento::getDescripcion).setHeader("Descripción").setSortable(true).setAutoWidth(true);
        grid.addColumn(c -> c.getSucursal() != null ? c.getSucursal().getNombre() : "-").setHeader("Sucursal").setAutoWidth(true);
        
        // Expansion logic to show details
        grid.setItemDetailsRenderer(new ComponentRenderer<>(asiento -> {
            List<ContAsientoDetalle> detalles = contabilidadService.findDetallesByAsiento(asiento);
            Grid<ContAsientoDetalle> detailsGrid = new Grid<>(ContAsientoDetalle.class, false);
            detailsGrid.addColumn(d -> d.getCuenta().getCodigo() + " - " + d.getCuenta().getNombre()).setHeader("Cuenta");
            detailsGrid.addColumn(d -> d.getDescripcionLinea()).setHeader("Detalle");
            detailsGrid.addColumn(d -> d.getDebe()).setHeader("Debe");
            detailsGrid.addColumn(d -> d.getHaber()).setHeader("Haber");
            detailsGrid.setItems(detalles);
            detailsGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
            detailsGrid.setAllRowsVisible(true);
            return detailsGrid;
        }));

        return grid;
    }

    private void updateGrid() {
        List<ContAsiento> asientos = contabilidadService.findAllAsientos();
        if (filter.getValue() != null && !filter.getValue().isEmpty()) {
            String f = filter.getValue().toLowerCase();
            asientos.removeIf(a -> a.getDescripcion() == null || !a.getDescripcion().toLowerCase().contains(f));
        }
        asientos.sort(Comparator.comparing(ContAsiento::getFecha).reversed());
        grid.setItems(asientos);
    }
}
