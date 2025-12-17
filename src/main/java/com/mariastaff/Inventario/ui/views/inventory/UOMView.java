package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida;
import com.mariastaff.Inventario.backend.service.CatalogoService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.components.base.TailwindToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.annotation.security.PermitAll;

@PageTitle("Unidades de Medida | Inventario")
@Route(value = "inventory/uom", layout = MainLayout.class)
@PermitAll
public class UOMView extends VerticalLayout {

    private final CatalogoService service;
    private final Grid<InvUnidadMedida> grid = new Grid<>(InvUnidadMedida.class);

    public UOMView(CatalogoService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        configureGrid();
        
        
        Button addBtn = new Button("Nueva Unidad", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openUOMDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Unidades de Medida"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "abreviatura");
        grid.addColumn(u -> u.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllUnidadesMedida());
    }

    private void openUOMDialog() {
        TailwindModal modal = new TailwindModal("Nueva Unidad de Medida");
        
        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full", "max-w-lg");
        
        TextField nombre = new TextField("Nombre");
        nombre.addClassName("w-full");
        TextField abreviatura = new TextField("Abreviatura");
        abreviatura.addClassName("w-full");
        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(true);

        formLayout.add(nombre, abreviatura, activo);
        modal.addContent(formLayout);
        
        Button saveButton = new Button("Guardar", e -> {
            TailwindNotification.show("Nueva Unidad de Medida guardada correctamente", TailwindNotification.Type.SUCCESS);
            modal.close();
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
