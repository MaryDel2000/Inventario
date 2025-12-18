package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.backend.data.entity.GenMoneda;
import com.mariastaff.Inventario.backend.service.GeneralService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.components.base.TailwindToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Monedas | Configuración")
@Route(value = "settings/currencies", layout = MainLayout.class)
@PermitAll
public class CurrenciesView extends VerticalLayout {

    private final GeneralService service;
    private final Grid<GenMoneda> grid = new Grid<>(GenMoneda.class);

    public CurrenciesView(GeneralService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        Button addBtn = new Button("Nueva Moneda", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Monedas"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");
        
        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("codigo", "nombre", "simbolo");
        grid.addColumn(m -> m.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllMonedas());
    }

    private void openDialog() {
        TailwindModal modal = new TailwindModal("Nueva Moneda");
        
        GenMoneda item = new GenMoneda();
        Binder<GenMoneda> binder = new Binder<>(GenMoneda.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full");
        
        TextField codigo = new TextField("Código (ISO 4217)");
        codigo.addClassName("w-full");
        
        TextField nombre = new TextField("Nombre");
        nombre.addClassName("w-full");
        
        TextField simbolo = new TextField("Símbolo");
        simbolo.addClassName("w-full");

        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(true);

        formLayout.add(codigo, nombre, simbolo, activo);
        
        modal.addContent(formLayout);

        binder.forField(codigo).asRequired("El código es obligatorio").bind(GenMoneda::getCodigo, GenMoneda::setCodigo);
        binder.forField(nombre).asRequired("El nombre es obligatorio").bind(GenMoneda::getNombre, GenMoneda::setNombre);
        binder.forField(simbolo).asRequired("Símbolo requerido").bind(GenMoneda::getSimbolo, GenMoneda::setSimbolo);
        binder.forField(activo).bind(GenMoneda::getActivo, GenMoneda::setActivo);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(item);
                TailwindNotification.show("Moneda guardada (Simulación)", TailwindNotification.Type.SUCCESS);
                updateList();
                modal.close();
            } catch (ValidationException ex) {
                TailwindNotification.show("Revise los campos requeridos", TailwindNotification.Type.ERROR);
            }
        });
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button("Cancelar", e -> {
            TailwindNotification.show("Cambios descartados", TailwindNotification.Type.INFO);
            modal.close();
        });
        cancelButton.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");

        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);
        
        add(modal);
        modal.open();
    }
}
