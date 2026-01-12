package com.mariastaff.Inventario.ui.components.dialogs;

import com.mariastaff.Inventario.backend.data.entity.InvCategoria;
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

public class CategoryManagementDialog extends TailwindModal {

    private final CatalogoService catalogoService;
    private final Grid<InvCategoria> grid;

    public CategoryManagementDialog(CatalogoService catalogoService) {
        super("Listado de Categorías");
        this.catalogoService = catalogoService;
        this.setDialogMaxWidth("max-w-5xl");

        this.grid = new Grid<>(InvCategoria.class, false);
        configureGrid();

        Button addBtn = new Button("Nueva Categoría", VaadinIcon.PLUS.create());
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
        
        grid.addColumn(InvCategoria::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(InvCategoria::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        
        grid.addColumn(new ComponentRenderer<>(category -> {
            TailwindToggle toggle = new TailwindToggle("");
            toggle.getStyle().set("margin-top", "0");
            toggle.setValue(Boolean.TRUE.equals(category.getActivo()));
            
            toggle.addValueChangeListener(event -> {
                category.setActivo(event.getValue());
                try {
                    catalogoService.saveCategoria(category);
                    TailwindNotification.show("Estado actualizado", TailwindNotification.Type.SUCCESS);
                } catch (Exception e) {
                    toggle.setValue(event.getOldValue());
                    TailwindNotification.show("Error al actualizar", TailwindNotification.Type.ERROR);
                }
            });
            return toggle;
        })).setHeader("Activo").setAutoWidth(true);

        grid.addComponentColumn(category -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            editBtn.addClickListener(e -> openEditDialog(category));

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            deleteBtn.addClickListener(event -> deleteCategory(category));
            
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);
    }

    private void refreshGrid() {
        grid.setItems(catalogoService.findAllCategorias());
    }

    private void openAddDialog() {
        TailwindModal addModal = new TailwindModal("Nueva Categoría");
        TextField nameField = new TextField("Nombre");
        nameField.addClassName("w-full");
        
        TextField descField = new TextField("Descripción");
        descField.addClassName("w-full");
        
        FormLayout layout = new FormLayout(nameField, descField);
        layout.addClassName("w-full");
        addModal.addContent(layout);
        
        Button saveAddBtn = new Button("Guardar", event -> {
            if (nameField.isEmpty()) {
                TailwindNotification.show("El nombre es obligatorio", TailwindNotification.Type.ERROR);
                return;
            }
            InvCategoria newCategory = new InvCategoria();
            newCategory.setNombre(nameField.getValue());
            newCategory.setDescripcion(descField.getValue());
            newCategory.setActivo(true);
            
            try {
                catalogoService.saveCategoria(newCategory);
                refreshGrid();
                TailwindNotification.show("Categoría creada", TailwindNotification.Type.SUCCESS);
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
        
        // Note: In real app, we need to add this modal to the UI. 
        // Since we are inside a TailwindModal (which is a Div), checking if we can add it to ourselves or need to find parent.
        // TailwindModal doesn't automatically attach to body. 
        // Ideally we should use the UI instance.
        // For simplicity reusing the same pattern as ProductsView:
        this.add(addModal); 
        addModal.open();
    }

    private void openEditDialog(InvCategoria category) {
        TailwindModal editModal = new TailwindModal("Editar Categoría");
        TextField nameField = new TextField("Nombre");
        nameField.addClassName("w-full");
        nameField.setValue(category.getNombre());
        
        TextField descField = new TextField("Descripción");
        descField.addClassName("w-full");
        if (category.getDescripcion() != null) descField.setValue(category.getDescripcion());
        
        FormLayout layout = new FormLayout(nameField, descField);
        layout.addClassName("w-full");
        editModal.addContent(layout);
        
        Button saveEditBtn = new Button("Guardar", event -> {
            category.setNombre(nameField.getValue());
            category.setDescripcion(descField.getValue());
            try {
                catalogoService.saveCategoria(category);
                refreshGrid();
                TailwindNotification.show("Categoría actualizada", TailwindNotification.Type.SUCCESS);
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

    private void deleteCategory(InvCategoria category) {
        try {
            catalogoService.deleteCategoria(category);
            refreshGrid();
            TailwindNotification.show("Categoría eliminada", TailwindNotification.Type.SUCCESS);
        } catch (DataIntegrityViolationException e) {
            TailwindNotification.show("No se puede eliminar: La categoría está en uso", TailwindNotification.Type.ERROR);
        } catch (Exception e) {
            TailwindNotification.show("Error al eliminar categoría", TailwindNotification.Type.ERROR);
        }
    }
}
