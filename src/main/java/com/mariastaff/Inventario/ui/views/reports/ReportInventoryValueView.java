package com.mariastaff.Inventario.ui.views.reports;

import com.mariastaff.Inventario.backend.data.entity.InvExistencia;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.mariastaff.Inventario.ui.utils.ExcelExporter;
import com.mariastaff.Inventario.ui.utils.ExcelExporter.ColumnDefinition;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Valoración de Inventario | Reportes")
@Route(value = "reports/inventory-value", layout = MainLayout.class)
@PermitAll
public class ReportInventoryValueView extends VerticalLayout {

    private final ProductoService productoService;
    private final Grid<InventoryValueItem> grid = new Grid<>();
    private final Span totalValueSpan = new Span();

    public ReportInventoryValueView(ProductoService productoService) {
        this.productoService = productoService;
        
        setSizeFull();
        addClassNames("bg-bg-secondary", "p-6");
        
        configureGrid();
        
        add(createSummary(), grid);
        updateList();
    }
    
    private Component createSummary() {
        HorizontalLayout summary = new HorizontalLayout();
        summary.addClassNames("bg-bg-surface", "p-4", "rounded-lg", "shadow", "mb-4", "w-full", "justify-between", "items-center");

        Span label = new Span(getTranslation("report.inventory.total_value") + ": ");
        label.addClassNames("text-lg", "font-medium", "mr-2");
        totalValueSpan.addClassNames("text-2xl", "font-bold", "text-primary");
        
        HorizontalLayout totals = new HorizontalLayout(label, totalValueSpan);
        totals.setAlignItems(FlexComponent.Alignment.CENTER);

        List<ColumnDefinition<InventoryValueItem>> cols = List.of(
             new ColumnDefinition<>(getTranslation("grid.header.product"), i -> i.productName),
             new ColumnDefinition<>(getTranslation("filter.warehouse"), i -> i.warehouse),
             new ColumnDefinition<>(getTranslation("grid.header.batch"), i -> i.batch),
             new ColumnDefinition<>(getTranslation("grid.header.quantity"), i -> i.quantity),
             new ColumnDefinition<>(getTranslation("report.inventory.unit_cost"), i -> i.unitCost),
             new ColumnDefinition<>(getTranslation("report.inventory.total_line"), i -> i.totalValue)
        );
        
        StreamResource resource = ExcelExporter.export(
             () -> getItems().stream(), 
             cols, 
             "ValorInventario", 
             "InventoryValue_Export"
        );
        
        Anchor anchor = new Anchor(resource, "Exportar Excel");
        anchor.addClassNames("bg-primary", "text-white", "font-semibold", "px-4", "py-2", "rounded-md", "cursor-pointer", "hover:bg-primary-600", "no-underline", "inline-block");
        anchor.getElement().setAttribute("download", true);
        
        summary.add(totals, anchor);
        return summary;
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        
        grid.addColumn(InventoryValueItem::productName).setHeader(getTranslation("grid.header.product"));
        grid.addColumn(InventoryValueItem::warehouse).setHeader(getTranslation("filter.warehouse"));
        grid.addColumn(InventoryValueItem::batch).setHeader(getTranslation("grid.header.batch"));
        grid.addColumn(InventoryValueItem::quantity).setHeader(getTranslation("grid.header.quantity"));
        grid.addColumn(item -> "$" + item.unitCost).setHeader(getTranslation("report.inventory.unit_cost"));
        grid.addColumn(item -> "$" + item.totalValue).setHeader(getTranslation("report.inventory.total_line"));
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        List<InventoryValueItem> items = getItems();
        grid.setItems(items);
        
        BigDecimal total = items.stream()
            .map(InventoryValueItem::totalValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        totalValueSpan.setText("$" + total.setScale(2, RoundingMode.HALF_UP));
    }
    
    // Extracted method to fetch data
    private List<InventoryValueItem> getItems() {
        List<InvExistencia> existencias = productoService.findAllExistencias();
        List<InventoryValueItem> items = new ArrayList<>();
        
        for (InvExistencia exist : existencias) {
             if (exist.getCantidadDisponible().compareTo(BigDecimal.ZERO) == 0) continue;
             
             BigDecimal cost = productoService.getCostoActual(exist.getProductoVariante(), "USD"); 
             BigDecimal total = cost.multiply(exist.getCantidadDisponible());
             
             String prodName = exist.getProductoVariante().getNombreVariante();
             String whName = exist.getUbicacion() != null ? exist.getUbicacion().getAlmacen().getNombre() : "General";
             String batch = exist.getLote() != null ? exist.getLote().getCodigoLote() : "-";
             
             items.add(new InventoryValueItem(prodName, whName, batch, exist.getCantidadDisponible(), cost, total));
        }
        return items;
    }
    
    private record InventoryValueItem(String productName, String warehouse, String batch, BigDecimal quantity, BigDecimal unitCost, BigDecimal totalValue) {}
}
