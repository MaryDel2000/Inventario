package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.backend.data.entity.InvImpuesto;
import com.mariastaff.Inventario.backend.service.CatalogoService;
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
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Impuestos | Configuración")
@Route(value = "settings/taxes", layout = MainLayout.class)
@PermitAll
public class TaxesView extends VerticalLayout {

    private final CatalogoService service;
    private final Grid<InvImpuesto> grid = new Grid<>(InvImpuesto.class);

    public TaxesView(CatalogoService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        Button addBtn = new Button("Nuevo Impuesto", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openDialog(new InvImpuesto()));

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Tasas de Impuesto"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "porcentaje");
        grid.addColumn(i -> i.getActivo() ? "Sí" : "No").setHeader("Activo");
        
        grid.addComponentColumn(tax -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addClassNames("text-text-secondary", "hover:text-primary", "p-2");
            editBtn.addClickListener(e -> openDialog(tax));
            return editBtn;
        }).setHeader("Acciones");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllImpuestos());
    }

    private void openDialog(InvImpuesto taxToEdit) {
        boolean isNew = taxToEdit.getId() == null;
        TailwindModal modal = new TailwindModal(isNew ? "Nuevo Impuesto" : "Editar Impuesto");
        
        InvImpuesto item = taxToEdit;
        Binder<InvImpuesto> binder = new Binder<>(InvImpuesto.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full");
        
        TextField nombre = new TextField("Nombre Impuesto");
        nombre.addClassName("w-full");
        
        BigDecimalField porcentaje = new BigDecimalField("Porcentaje (%)");
        porcentaje.addClassName("w-full");

        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(item.getActivo() != null ? item.getActivo() : true);

        formLayout.add(nombre, porcentaje, activo);
        
        modal.addContent(formLayout);

        binder.forField(nombre).asRequired("El nombre es obligatorio").bind(InvImpuesto::getNombre, InvImpuesto::setNombre);
        binder.forField(porcentaje).asRequired("Porcentaje requerido").bind(InvImpuesto::getPorcentaje, InvImpuesto::setPorcentaje);
        binder.forField(activo).bind(InvImpuesto::getActivo, InvImpuesto::setActivo);

        binder.readBean(item);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(item);
                service.saveImpuesto(item); // Real save
                TailwindNotification.show("Impuesto guardado exitosamente", TailwindNotification.Type.SUCCESS);
                updateList();
                modal.close();
            } catch (ValidationException ex) {
                TailwindNotification.show("Revise los campos requeridos", TailwindNotification.Type.ERROR);
            }
        });
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button("Cancelar", e -> {
            modal.close();
        });
        cancelButton.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");

        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);
        
        add(modal);
        modal.open();
    }
}
