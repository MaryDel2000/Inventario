package com.mariastaff.Inventario.ui.views.reports;

import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import com.mariastaff.Inventario.backend.data.entity.PosVentaDetalle;
import com.mariastaff.Inventario.backend.service.PosService;
import com.mariastaff.Inventario.backend.service.ProductoService;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Margen de Ganancia | Reportes")
@Route(value = "reports/margins", layout = MainLayout.class)
@PermitAll
public class ReportMarginsView extends VerticalLayout {

    private final PosService posService;
    private final ProductoService productoService;
    private final Grid<MarginItem> grid = new Grid<>();
    private final TailwindDatePicker startDate = new TailwindDatePicker(getTranslation("filter.date.start"));
    private final TailwindDatePicker endDate = new TailwindDatePicker(getTranslation("filter.date.end"));

    public ReportMarginsView(PosService posService, ProductoService productoService) {
        this.posService = posService;
        this.productoService = productoService;
        
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
        
        List<ColumnDefinition<MarginItem>> cols = List.of(
             new ColumnDefinition<>(getTranslation("report.top.product"), i -> i.productName),
             new ColumnDefinition<>(getTranslation("report.margin.avg_price"), i -> i.avgPrice),
             new ColumnDefinition<>(getTranslation("report.margin.cost"), i -> i.unitCost),
             new ColumnDefinition<>(getTranslation("report.margin.profit"), i -> i.marginAbs),
             new ColumnDefinition<>(getTranslation("report.margin.percent"), i -> i.marginPct)
        );
        
        StreamResource resource = ExcelExporter.export(
             () -> getItems().stream(), 
             cols, 
             "Margenes", 
             "Margins_Export"
        );
        
        Anchor anchor = new Anchor(resource, "Exportar Excel");
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
        
        grid.addColumn(MarginItem::productName).setHeader(getTranslation("report.top.product"));
        grid.addColumn(item -> "$" + item.avgPrice).setHeader(getTranslation("report.margin.avg_price"));
        grid.addColumn(item -> "$" + item.unitCost).setHeader(getTranslation("report.margin.cost"));
        grid.addColumn(item -> "$" + item.marginAbs).setHeader(getTranslation("report.margin.profit"));
        grid.addColumn(item -> item.marginPct + "%").setHeader(getTranslation("report.margin.percent"));
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(getItems());
    }
    
    // Extracted method to fetch data based on current UI state
    private List<MarginItem> getItems() {
        LocalDate startVal = startDate.getValue();
        LocalDate endVal = endDate.getValue();
        
        if (startVal == null) startVal = LocalDate.now().minusMonths(1);
        if (endVal == null) endVal = LocalDate.now();

        LocalDateTime start = startVal.atStartOfDay();
        LocalDateTime end = endVal.atTime(23, 59, 59);
        
        List<PosVenta> ventas = posService.findAllVentasWithDetails();
        Map<String, MarginData> stats = new HashMap<>();
        
        for (PosVenta venta : ventas) {
             if (venta.getFechaHora().isBefore(start) || venta.getFechaHora().isAfter(end)) continue;
             if (!"COMPLETADA".equals(venta.getEstado())) continue;
             
             for (PosVentaDetalle det : venta.getDetalles()) {
                  if (det.getProductoVariante() == null) continue;
                  if (det.getCantidad() == null || det.getCantidad().compareTo(BigDecimal.ZERO) == 0) continue;

                  String varName = det.getProductoVariante().getNombreVariante();
                  BigDecimal soldPrice = det.getSubtotal().divide(det.getCantidad(), RoundingMode.HALF_UP);
                  
                  // Need to use final or effectively final variable for lambda
                  final BigDecimal finalSoldPrice = soldPrice;
                  final InvProductoVariante finalVariant = det.getProductoVariante();
                  
                  stats.compute(varName, (k, v) -> {
                      if (v == null) return new MarginData(finalVariant, finalSoldPrice.multiply(det.getCantidad()), det.getCantidad());
                      return new MarginData(finalVariant, v.totalPrice.add(finalSoldPrice.multiply(det.getCantidad())), v.totalQty.add(det.getCantidad()));
                  });
             }
        }
        
        List<MarginItem> result = new ArrayList<>();
        for (MarginData data : stats.values()) {
             if (data.totalQty.compareTo(BigDecimal.ZERO) == 0) continue;

             BigDecimal avgPrice = data.totalPrice.divide(data.totalQty, 2, RoundingMode.HALF_UP);
             BigDecimal unitCost = productoService.getCostoActual(data.variant, "USD"); 
             
             BigDecimal profit = avgPrice.subtract(unitCost);
             BigDecimal marginPct = BigDecimal.ZERO;
             if (avgPrice.compareTo(BigDecimal.ZERO) > 0) {
                 marginPct = profit.divide(avgPrice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
             }
             
             result.add(new MarginItem(data.variant.getNombreVariante(), avgPrice, unitCost, profit, marginPct));
        }
        
        result.sort((a, b) -> b.marginAbs.compareTo(a.marginAbs));
        return result;
    }
    
    private record MarginData(InvProductoVariante variant, BigDecimal totalPrice, BigDecimal totalQty) {}
    private record MarginItem(String productName, BigDecimal avgPrice, BigDecimal unitCost, BigDecimal marginAbs, BigDecimal marginPct) {}
}
