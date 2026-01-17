package com.mariastaff.Inventario.ui.views.reports;

import com.mariastaff.Inventario.backend.data.entity.InvMovimiento;
import com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle;
import com.mariastaff.Inventario.backend.service.MovimientoService;
import com.mariastaff.Inventario.ui.components.base.TailwindDatePicker;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.mariastaff.Inventario.ui.utils.ExcelExporter;
import com.mariastaff.Inventario.ui.utils.ExcelExporter.ColumnDefinition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Kardex | Reportes")
@Route(value = "reports/kardex", layout = MainLayout.class)
@PermitAll
public class ReportKardexView extends VerticalLayout {

    private final MovimientoService movimientoService;
    private final Grid<KardexItem> grid = new Grid<>();
    private final TailwindDatePicker startDate = new TailwindDatePicker(getTranslation("filter.date.start"));
    private final TailwindDatePicker endDate = new TailwindDatePicker(getTranslation("filter.date.end"));

    public ReportKardexView(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
        
        setSizeFull();
        addClassNames("bg-bg-secondary", "p-6");
        
        configureFilters();
        configureGrid();
        
        add(createToolbar(), grid);
        updateList();
    }
    
    private Component createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(startDate, endDate);
        toolbar.addClassName("mb-4");
        
        List<ColumnDefinition<KardexItem>> cols = List.of(
             new ColumnDefinition<>(getTranslation("grid.header.date"), i -> i.date),
             new ColumnDefinition<>(getTranslation("report.account.type"), i -> i.type),
             new ColumnDefinition<>(getTranslation("report.reference"), i -> i.ref),
             new ColumnDefinition<>(getTranslation("grid.header.product"), i -> i.productName),
             new ColumnDefinition<>(getTranslation("grid.header.batch"), i -> i.batch),
             new ColumnDefinition<>(getTranslation("grid.header.quantity"), i -> i.quantity),
             new ColumnDefinition<>(getTranslation("grid.header.location.origin"), i -> i.origin),
             new ColumnDefinition<>(getTranslation("grid.header.location.destination"), i -> i.dest)
        );
        
        StreamResource resource = ExcelExporter.export(
             () -> getItems().stream(), 
             cols, 
             "Kardex", 
             "Kardex_Export"
        );
        
        Anchor anchor = new Anchor(resource, getTranslation("action.export.excel"));
        anchor.addClassNames("bg-primary", "text-white", "font-semibold", "px-4", "py-2", "rounded-md", "cursor-pointer", "hover:bg-primary-600", "no-underline", "inline-block");
        anchor.getElement().setAttribute("download", true);
        
        HorizontalLayout container = new HorizontalLayout(toolbar, anchor);
        container.setWidthFull();
        container.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        
        return container;
    }

    private void configureFilters() {
        startDate.setValue(LocalDate.now().minusMonths(1));
        endDate.setValue(LocalDate.now());
        
        startDate.addValueChangeListener(e -> updateList());
        endDate.addValueChangeListener(e -> updateList());
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        
        grid.addColumn(KardexItem::date).setHeader(getTranslation("grid.header.date")).setWidth("150px").setFlexGrow(0);
        grid.addColumn(KardexItem::type).setHeader(getTranslation("report.account.type")).setWidth("100px").setFlexGrow(0);
        grid.addColumn(KardexItem::ref).setHeader(getTranslation("report.reference"));
        grid.addColumn(KardexItem::productName).setHeader(getTranslation("grid.header.product"));
        grid.addColumn(KardexItem::batch).setHeader(getTranslation("grid.header.batch"));
        grid.addColumn(KardexItem::quantity).setHeader(getTranslation("grid.header.quantity"));
        grid.addColumn(KardexItem::origin).setHeader(getTranslation("grid.header.location.origin"));
        grid.addColumn(KardexItem::dest).setHeader(getTranslation("grid.header.location.destination"));

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(getItems());
    }
    
    // Removed unused 'items' field
    
    private List<KardexItem> getItems() {
        LocalDate startVal = startDate.getValue();
        LocalDate endVal = endDate.getValue();
        
        if (startVal == null) startVal = LocalDate.now().minusMonths(1);
        if (endVal == null) endVal = LocalDate.now();

        LocalDateTime start = startVal.atStartOfDay();
        LocalDateTime end = endVal.atTime(23, 59, 59);
        
        // Fetch details directly with eager loading of headers and relations
        List<InvMovimientoDetalle> detalles = movimientoService.findDetallesByDateRange(start, end);
        List<KardexItem> result = new ArrayList<>();
        
        for (InvMovimientoDetalle det : detalles) {
            InvMovimiento mov = det.getMovimiento();
            
            String batch = det.getLote() != null ? det.getLote().getCodigoLote() : "-";
            String orig = mov.getAlmacenOrigen() != null ? mov.getAlmacenOrigen().getNombre() : "-";
            if (det.getUbicacionOrigen() != null) orig += " (" + det.getUbicacionOrigen().getCodigo() + ")";
            
            String dst = mov.getAlmacenDestino() != null ? mov.getAlmacenDestino().getNombre() : "-";
            if (det.getUbicacionDestino() != null) dst += " (" + det.getUbicacionDestino().getCodigo() + ")";
            
            result.add(new KardexItem(
                mov.getFechaMovimiento(),
                mov.getTipoMovimiento(),
                mov.getReferenciaTipo() + " #" + mov.getReferenciaId(),
                det.getProductoVariante().getNombreVariante(),
                batch,
                det.getCantidad(),
                orig, dst
            ));
        }
        
        result.sort((a, b) -> b.date.compareTo(a.date));
        return result;
    }
    
    private record KardexItem(LocalDateTime date, String type, String ref, String productName, String batch, BigDecimal quantity, String origin, String dest) {}
}
