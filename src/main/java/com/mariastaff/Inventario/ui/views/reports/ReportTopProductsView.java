package com.mariastaff.Inventario.ui.views.reports;

import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import com.mariastaff.Inventario.backend.data.entity.PosVentaDetalle;
import com.mariastaff.Inventario.backend.service.PosService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Productos más vendidos | Reportes")
@Route(value = "reports/top-products", layout = MainLayout.class)
@PermitAll
public class ReportTopProductsView extends VerticalLayout {

    private final PosService posService;
    private final Grid<TopProductItem> grid = new Grid<>();
    private final TailwindDatePicker startDate = new TailwindDatePicker(getTranslation("filter.date.start"));
    private final TailwindDatePicker endDate = new TailwindDatePicker(getTranslation("filter.date.end"));

    public ReportTopProductsView(PosService posService) {
        this.posService = posService;
        
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
        
        List<ColumnDefinition<TopProductItem>> cols = List.of(
             new ColumnDefinition<>(getTranslation("report.top.product"), i -> i.productName),
             new ColumnDefinition<>(getTranslation("report.top.quantity"), i -> i.quantity),
             new ColumnDefinition<>(getTranslation("report.top.total"), i -> i.totalNet)
        );
        
        StreamResource resource = ExcelExporter.export(
             () -> getItems().stream(), 
             cols, 
             "TopProductos", 
             "TopProducts_Export"
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
        
        grid.addColumn(TopProductItem::productName).setHeader(getTranslation("report.top.product"));
        grid.addColumn(TopProductItem::quantity).setHeader(getTranslation("report.top.quantity"));
        grid.addColumn(item -> "$" + item.totalNet).setHeader(getTranslation("report.top.total"));
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(getItems());
    }
    
    private List<TopProductItem> getItems() {
        LocalDate startVal = startDate.getValue();
        LocalDate endVal = endDate.getValue();
        
        if (startVal == null) startVal = LocalDate.now().minusMonths(1);
        if (endVal == null) endVal = LocalDate.now();

        LocalDateTime start = startVal.atStartOfDay();
        LocalDateTime end = endVal.atTime(23, 59, 59);
        
        List<PosVenta> ventas = posService.findAllVentasWithDetails();
        Map<String, TopProductItem> stats = new HashMap<>();
        
        for (PosVenta venta : ventas) {
             if (venta.getFechaHora().isBefore(start) || venta.getFechaHora().isAfter(end)) continue;
             if (!"COMPLETADA".equals(venta.getEstado())) continue;
             
             for (PosVentaDetalle det : venta.getDetalles()) {
                  if (det.getProductoVariante() == null) continue;
                  String var = det.getProductoVariante().getNombreVariante();
                  
                  stats.compute(var, (k, v) -> {
                      if (v == null) return new TopProductItem(var, det.getCantidad(), det.getSubtotal());
                      return new TopProductItem(var, v.quantity.add(det.getCantidad()), v.totalNet.add(det.getSubtotal()));
                  });
             }
        }
        
        List<TopProductItem> result = new ArrayList<>(stats.values());
        result.sort((a, b) -> b.totalNet.compareTo(a.totalNet));
        return result;
    }
    
    private record TopProductItem(String productName, BigDecimal quantity, BigDecimal totalNet) {}
}
