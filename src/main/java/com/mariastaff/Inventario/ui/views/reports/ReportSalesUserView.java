package com.mariastaff.Inventario.ui.views.reports;

import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import com.mariastaff.Inventario.backend.data.entity.SysUsuario;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PageTitle("Ventas por Usuario | Reportes")
@Route(value = "reports/sales-user", layout = MainLayout.class)
@PermitAll
public class ReportSalesUserView extends VerticalLayout {

    private final PosService posService;
    private final Grid<UserSalesSummary> grid = new Grid<>();
    private final TailwindDatePicker startDate = new TailwindDatePicker(getTranslation("filter.date.start"));
    private final TailwindDatePicker endDate = new TailwindDatePicker(getTranslation("filter.date.end"));

    public ReportSalesUserView(PosService posService) {
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
        
        List<ColumnDefinition<UserSalesSummary>> cols = List.of(
             new ColumnDefinition<>(getTranslation("user.username"), i -> i.username),
             new ColumnDefinition<>(getTranslation("user.fullname"), i -> i.fullName),
             new ColumnDefinition<>(getTranslation("report.sales.count"), i -> i.transactionCount),
             new ColumnDefinition<>(getTranslation("report.sales.total_usd"), i -> i.totalAmount)
        );
        
        StreamResource resource = ExcelExporter.export(
             () -> getItems().stream(), 
             cols, 
             "VentasUsuario", 
             "UserSales_Export"
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
        
        grid.addColumn(UserSalesSummary::username).setHeader(getTranslation("user.username"));
        grid.addColumn(UserSalesSummary::fullName).setHeader(getTranslation("user.fullname"));
        grid.addColumn(UserSalesSummary::transactionCount).setHeader(getTranslation("report.sales.count"));
        grid.addColumn(item -> "$" + item.totalAmount).setHeader(getTranslation("report.sales.total_usd"));
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(getItems());
    }
    
    // Extracted method to fetch data based on current UI state
    private List<UserSalesSummary> getItems() {
        LocalDate startVal = startDate.getValue();
        LocalDate endVal = endDate.getValue();
        
        // Default check to avoid null pointer if called too early
        if (startVal == null) startVal = LocalDate.now().minusMonths(1);
        if (endVal == null) endVal = LocalDate.now();

        LocalDateTime start = startVal.atStartOfDay();
        LocalDateTime end = endVal.atTime(23, 59, 59);
        
        List<PosVenta> ventas = posService.findAllVentas();
        
        Map<String, UserSalesSummary> summaryMap = new HashMap<>();
        
        for (PosVenta venta : ventas) {
            // Filter
            if (venta.getFechaHora().isBefore(start) || venta.getFechaHora().isAfter(end)) continue;
            if (!"COMPLETADA".equals(venta.getEstado())) continue;
            
            SysUsuario user = venta.getUsuarioVendedor();
            String key = user != null ? user.getUsername() : getTranslation("common.na");
            String name = (user != null && user.getEntidad() != null) ? user.getEntidad().getNombreCompleto() : getTranslation("common.unknown");
            
            summaryMap.compute(key, (k, v) -> {
                if (v == null) return new UserSalesSummary(key, name, 1, venta.getTotalNeto());
                return new UserSalesSummary(key, name, v.transactionCount + 1, v.totalAmount.add(venta.getTotalNeto()));
            });
        }
        
        return new ArrayList<>(summaryMap.values());
    }
    
    private record UserSalesSummary(String username, String fullName, int transactionCount, java.math.BigDecimal totalAmount) {}
}
