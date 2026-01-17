package com.mariastaff.Inventario.ui.views.reports;

import com.mariastaff.Inventario.backend.service.ContabilidadService;
import com.mariastaff.Inventario.backend.service.ContabilidadService.AccountBalanceDTO;
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
import java.util.List;

@PageTitle("Balance de Comprobación | Reportes")
@Route(value = "reports/trial-balance", layout = MainLayout.class)
@PermitAll
public class ReportTrialBalanceView extends VerticalLayout {

    private final ContabilidadService contabilidadService;
    private final Grid<AccountBalanceDTO> grid = new Grid<>();
    private final TailwindDatePicker startDate = new TailwindDatePicker(getTranslation("filter.date.start"));
    private final TailwindDatePicker endDate = new TailwindDatePicker(getTranslation("filter.date.end"));

    public ReportTrialBalanceView(ContabilidadService contabilidadService) {
        this.contabilidadService = contabilidadService;
        
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
        
        List<ColumnDefinition<AccountBalanceDTO>> cols = List.of(
             new ColumnDefinition<>(getTranslation("report.trial.code"), i -> i.getCuenta().getCodigo()),
             new ColumnDefinition<>(getTranslation("report.trial.account"), i -> i.getCuenta().getNombre()),
             new ColumnDefinition<>(getTranslation("report.trial.debit"), i -> i.getDebe()),
             new ColumnDefinition<>(getTranslation("report.trial.credit"), i -> i.getHaber()),
             new ColumnDefinition<>(getTranslation("report.trial.balance"), i -> i.getSaldo())
        );
        
        StreamResource resource = ExcelExporter.export(
             () -> getItems().stream(), 
             cols, 
             "BalanzaComprobacion", 
             "TrialBalance_Export"
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
        startDate.setValue(LocalDate.now().minusMonths(3));
        endDate.setValue(LocalDate.now());
        
        startDate.addValueChangeListener(e -> updateList());
        endDate.addValueChangeListener(e -> updateList());
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        
        grid.addColumn(item -> item.getCuenta().getCodigo()).setHeader(getTranslation("report.trial.code"));
        grid.addColumn(item -> item.getCuenta().getNombre()).setHeader(getTranslation("report.trial.account"));
        grid.addColumn(item -> "$" + item.getDebe()).setHeader(getTranslation("report.trial.debit"));
        grid.addColumn(item -> "$" + item.getHaber()).setHeader(getTranslation("report.trial.credit"));
        grid.addColumn(item -> "$" + item.getSaldo()).setHeader(getTranslation("report.trial.balance"));
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(getItems());
    }
    
    private List<AccountBalanceDTO> getItems() {
        LocalDate startVal = startDate.getValue();
        LocalDate endVal = endDate.getValue();
        
        if (startVal == null) startVal = LocalDate.now().minusMonths(3);
        if (endVal == null) endVal = LocalDate.now();
        
        List<AccountBalanceDTO> balances = contabilidadService.getAccountBalances(startVal, endVal);
        
        // Filter out zero balance accounts if needed, or keep all
        balances.removeIf(b -> b.getDebe().compareTo(BigDecimal.ZERO) == 0 && b.getHaber().compareTo(BigDecimal.ZERO) == 0);
        
        // Sort by Code
        balances.sort((a, b) -> a.getCuenta().getCodigo().compareTo(b.getCuenta().getCodigo()));
        return balances;
    }
}
