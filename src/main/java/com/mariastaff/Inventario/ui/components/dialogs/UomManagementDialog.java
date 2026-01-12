package com.mariastaff.Inventario.ui.components.dialogs;

import com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida;
import com.mariastaff.Inventario.backend.service.CatalogoService;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.components.base.TailwindToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.springframework.dao.DataIntegrityViolationException;

public class UomManagementDialog extends TailwindModal {

    private final CatalogoService catalogoService;
    private final Grid<InvUnidadMedida> grid;

    public UomManagementDialog(CatalogoService catalogoService) {
        super("Listado de Unidades de Medida");
        this.catalogoService = catalogoService;
        this.setDialogMaxWidth("max-w-5xl");

        this.grid = new Grid<>(InvUnidadMedida.class, false);
        configureGrid();

        Button addBtn = new Button("Nueva Unidad", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "mb-4", "w-fit");
        addBtn.addClickListener(e -> openAddDialog());

        Div gridContainer = new Div(grid);
        gridContainer.addClassNames("w-full", "h-96", "overflow-hidden", "flex", "flex-col");
        grid.setHeightFull();

        VerticalLayout content = new VerticalLayout(addBtn, gridContainer);
        content.setPadding(false);
        content.setSpacing(true);
        content.addClassNames("w-full", "h-full");

        this.addContent(content);

        Button closeBtn = new Button("Cerrar", e -> this.close());
        closeBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        this.addFooterButton(closeBtn);
        
        refreshGrid();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        
        grid.addColumn(InvUnidadMedida::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(InvUnidadMedida::getAbreviatura).setHeader("Abreviatura").setAutoWidth(true);
        
        grid.addColumn(new ComponentRenderer<>(uom -> {
            TailwindToggle toggle = new TailwindToggle("");
            toggle.getStyle().set("margin-top", "0");
            toggle.setValue(Boolean.TRUE.equals(uom.getActivo()));
            
            toggle.addValueChangeListener(event -> {
                uom.setActivo(event.getValue());
                try {
                    catalogoService.saveUnidadMedida(uom);
                    TailwindNotification.show("Estado actualizado", TailwindNotification.Type.SUCCESS);
                } catch (Exception e) {
                    toggle.setValue(event.getOldValue());
                    TailwindNotification.show("Error al actualizar", TailwindNotification.Type.ERROR);
                }
            });
            return toggle;
        })).setHeader("Activo").setAutoWidth(true);

        grid.addComponentColumn(uom -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            editBtn.addClickListener(e -> openEditDialog(uom));

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            deleteBtn.addClickListener(event -> deleteUom(uom));
            
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);
    }

    private void refreshGrid() {
        grid.setItems(catalogoService.findAllUnidadesMedida());
    }

    private void openAddDialog() {
        TailwindModal addModal = new TailwindModal("Nueva Unidad de Medida");
        TextField nameField = new TextField("Nombre");
        nameField.addClassName("w-full");
        
        TextField abbrField = new TextField("Abreviatura");
        abbrField.addClassName("w-full");
        
        FormLayout layout = new FormLayout(nameField, abbrField);
        layout.addClassName("w-full");
        addModal.addContent(layout);
        
        Button saveAddBtn = new Button("Guardar", event -> {
            if (nameField.isEmpty()) {
                TailwindNotification.show("El nombre es obligatorio", TailwindNotification.Type.ERROR);
                return;
            }
            InvUnidadMedida newUom = new InvUnidadMedida();
            newUom.setNombre(nameField.getValue());
            newUom.setAbreviatura(abbrField.getValue());
            if (newUom.getAbreviatura() == null || newUom.getAbreviatura().isEmpty()) {
                newUom.setAbreviatura(nameField.getValue().length() > 5 ? nameField.getValue().substring(0, 5).toUpperCase() : nameField.getValue().toUpperCase());
            }
            newUom.setActivo(true);
            
            try {
                catalogoService.saveUnidadMedida(newUom);
                refreshGrid();
                TailwindNotification.show("Unidad creada", TailwindNotification.Type.SUCCESS);
                addModal.close();
            } catch (Exception ex) {
                TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
            }
        });
        saveAddBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        
        Button cancelAddBtn = new Button("Cancelar", event -> addModal.close());
        cancelAddBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
        
        addModal.addFooterButton(cancelAddBtn);
        addModal.addFooterButton(saveAddBtn);
        
        this.add(addModal);
        addModal.open();
    }

    private void openEditDialog(InvUnidadMedida uom) {
        TailwindModal editModal = new TailwindModal("Editar Unidad de Medida");
        TextField nameField = new TextField("Nombre");
        nameField.addClassName("w-full");
        nameField.setValue(uom.getNombre());
        
        TextField abbrField = new TextField("Abreviatura");
        abbrField.addClassName("w-full");
        if (uom.getAbreviatura() != null) abbrField.setValue(uom.getAbreviatura());
        
        FormLayout layout = new FormLayout(nameField, abbrField);
        layout.addClassName("w-full");
        editModal.addContent(layout);
        
        Button saveEditBtn = new Button("Guardar", event -> {
            uom.setNombre(nameField.getValue());
            uom.setAbreviatura(abbrField.getValue());
            try {
                catalogoService.saveUnidadMedida(uom);
                refreshGrid();
                TailwindNotification.show("Unidad actualizada", TailwindNotification.Type.SUCCESS);
                editModal.close();
            } catch (Exception ex) {
                TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
            }
        });
        saveEditBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        
        Button cancelEditBtn = new Button("Cancelar", event -> editModal.close());
        cancelEditBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
        
        editModal.addFooterButton(cancelEditBtn);
        editModal.addFooterButton(saveEditBtn);
        this.add(editModal);
        editModal.open();
    }

    private void deleteUom(InvUnidadMedida uom) {
        try {
            catalogoService.deleteUnidadMedida(uom);
            refreshGrid();
            TailwindNotification.show("Unidad eliminada", TailwindNotification.Type.SUCCESS);
        } catch (DataIntegrityViolationException e) {
            TailwindNotification.show("No se puede eliminar: La unidad está en uso", TailwindNotification.Type.ERROR);
        } catch (Exception e) {
            TailwindNotification.show("Error al eliminar unidad", TailwindNotification.Type.ERROR);
        }
    }
}
