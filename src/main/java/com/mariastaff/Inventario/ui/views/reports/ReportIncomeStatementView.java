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
import java.time.LocalDate;
import java.util.List;

@PageTitle("Estado de Resultados | Reportes")
@Route(value = "reports/income-statement", layout = MainLayout.class)
@PermitAll
public class ReportIncomeStatementView extends VerticalLayout {

    private final ContabilidadService contabilidadService;
    private final Grid<AccountBalanceDTO> grid = new Grid<>();
    private final TailwindDatePicker startDate = new TailwindDatePicker(getTranslation("filter.date.start"));
    private final TailwindDatePicker endDate = new TailwindDatePicker(getTranslation("filter.date.end"));
    private final Span netIncomeSpan = new Span();

    public ReportIncomeStatementView(ContabilidadService contabilidadService) {
        this.contabilidadService = contabilidadService;
        
        setSizeFull();
        addClassNames("bg-bg-secondary", "p-6");
        
        configureFilters();
        configureGrid();
        
        add(createToolbar(), grid, createFooter());
        updateList();
    }
    
    private Component createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(startDate, endDate);
        toolbar.addClassName("mb-4");
        
        List<ColumnDefinition<AccountBalanceDTO>> cols = List.of(
             new ColumnDefinition<>(getTranslation("report.trial.code"), i -> i.getCuenta().getCodigo()),
             new ColumnDefinition<>(getTranslation("report.trial.account"), i -> i.getCuenta().getNombre()),
             new ColumnDefinition<>(getTranslation("report.account.type"), i -> i.getCuenta().getTipo()),
             new ColumnDefinition<>(getTranslation("report.amount"), i -> i.getSaldo().abs())
        );
        
        StreamResource resource = ExcelExporter.export(
             () -> getItems().stream(), 
             cols, 
             "EstadoResultados", 
             "IncomeStatement_Export"
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

    private Component createFooter() {
         HorizontalLayout footer = new HorizontalLayout();
         footer.addClassNames("bg-bg-surface", "p-4", "rounded-lg", "shadow", "mt-4", "w-full", "justify-end");
         
         Span label = new Span(getTranslation("report.income.net_income") + ": ");
         label.addClassNames("text-lg", "font-medium", "mr-2");
         
         netIncomeSpan.addClassNames("text-2xl", "font-bold");
         
         footer.add(label, netIncomeSpan);
         return footer;
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
        grid.addColumn(item -> item.getCuenta().getTipo()).setHeader(getTranslation("report.account.type"));
        grid.addColumn(item -> "$" + item.getSaldo().abs()).setHeader(getTranslation("report.amount")); // Show absolute value? Usually Income Statement shows columns or signed.
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        List<AccountBalanceDTO> balances = getItems();
        grid.setItems(balances);
        
        BigDecimal sum = balances.stream()
               .map(AccountBalanceDTO::getSaldo)
               .reduce(BigDecimal.ZERO, BigDecimal::add);
               
        BigDecimal netIncome = sum.negate();
        
        netIncomeSpan.setText("$" + netIncome.setScale(2, RoundingMode.HALF_UP));
        if (netIncome.compareTo(BigDecimal.ZERO) >= 0) {
            netIncomeSpan.getStyle().set("color", "green");
        } else {
            netIncomeSpan.getStyle().set("color", "red");
        }
    }
    
    private List<AccountBalanceDTO> getItems() {
        LocalDate startVal = startDate.getValue();
        LocalDate endVal = endDate.getValue();
        
        if (startVal == null) startVal = LocalDate.now().minusMonths(3);
        if (endVal == null) endVal = LocalDate.now();
        
        List<AccountBalanceDTO> balances = contabilidadService.getAccountBalances(startVal, endVal);
        
        balances.removeIf(b -> {
             String type = b.getCuenta().getTipo();
             if (type == null) return true;
             type = type.toUpperCase();
             return !(type.contains("INGRESO") || type.contains("COSTO") || type.contains("GASTO"));
        });
        
        balances.removeIf(b -> b.getSaldo().compareTo(BigDecimal.ZERO) == 0);
        return balances;
    }
}
