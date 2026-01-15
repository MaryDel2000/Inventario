package com.mariastaff.Inventario.ui.views.accounting;

import com.mariastaff.Inventario.backend.data.entity.ContCuenta;
import com.mariastaff.Inventario.backend.service.ContabilidadService;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
// import com.mariastaff.Inventario.ui.components.FlexBoxLayout; // Removed
// import com.mariastaff.Inventario.ui.layout.size.Horizontal; // Removed
// import com.mariastaff.Inventario.ui.layout.size.Top; // Removed
// import com.mariastaff.Inventario.ui.util.UIUtils; // Removed
// import com.mariastaff.Inventario.ui.util.css.BoxSizing; // Removed
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
// import com.vaadin.flow.data.builder.BeanValidations; // Removed
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Route(value = "accounting/accounts", layout = MainLayout.class)
@PageTitle("Catálogo de Cuentas")
@PermitAll
public class ChartOfAccountsView extends VerticalLayout {

    private final ContabilidadService contabilidadService;
    private Grid<ContCuenta> grid;
    private TextField filter;

    @Autowired
    public ChartOfAccountsView(ContabilidadService contabilidadService) {
        this.contabilidadService = contabilidadService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createHeader(), createContent());
        updateGrid();
    }

    private Component createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setId("header");
        header.setWidthFull();
        header.setSpacing(true);
        header.setPadding(true);
        header.setAlignItems(Alignment.CENTER);

        filter = new TextField();
        filter.setPlaceholder("Buscar cuenta...");
        filter.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        filter.addValueChangeListener(e -> updateGrid());
        filter.setClearButtonVisible(true);

        Button addBtn = new Button("Nueva Cuenta", new Icon(VaadinIcon.PLUS));
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // addBtn.addClickListener(e -> openDialog(new ContCuenta())); // TODO: Implement Dialog

        header.add(filter, addBtn);
        header.expand(filter);
        return header;
    }

    private Component createContent() {
        grid = new Grid<>(ContCuenta.class, false);
        grid.setSizeFull();
        grid.addColumn(ContCuenta::getCodigo).setHeader("Código").setSortable(true).setAutoWidth(true);
        grid.addColumn(ContCuenta::getNombre).setHeader("Nombre").setSortable(true).setAutoWidth(true);
        grid.addColumn(ContCuenta::getTipo).setHeader("Tipo").setSortable(true);
        grid.addColumn(c -> c.getActiva() ? "Sí" : "No").setHeader("Activa");
        
        return grid;
    }

    private void updateGrid() {
        List<ContCuenta> cuentas = contabilidadService.findAllCuentas();
        // Simple client-side filtering for now
        if (filter.getValue() != null && !filter.getValue().isEmpty()) {
            String f = filter.getValue().toLowerCase();
            cuentas = cuentas.stream()
                .filter(c -> c.getCodigo().toLowerCase().contains(f) || c.getNombre().toLowerCase().contains(f))
                .collect(Collectors.toList());
        }
        cuentas.sort(Comparator.comparing(ContCuenta::getCodigo));
        grid.setItems(cuentas);
    }
}
