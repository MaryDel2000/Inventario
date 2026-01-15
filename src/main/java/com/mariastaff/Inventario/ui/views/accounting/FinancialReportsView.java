package com.mariastaff.Inventario.ui.views.accounting;

import com.mariastaff.Inventario.backend.service.ContabilidadService;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "accounting/reports", layout = MainLayout.class)
@PageTitle("Reportes Financieros")
@PermitAll
public class FinancialReportsView extends VerticalLayout {

    public FinancialReportsView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        add(new H2("Reportes Financieros - En Construcción"));
    }
}
