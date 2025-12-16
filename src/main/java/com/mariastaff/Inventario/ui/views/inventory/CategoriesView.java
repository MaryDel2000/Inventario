package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvCategoria;
import com.mariastaff.Inventario.backend.service.CatalogoService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.annotation.security.PermitAll;

@PageTitle("Categorías | Inventario")
@Route(value = "inventory/categories", layout = MainLayout.class)
@PermitAll
public class CategoriesView extends VerticalLayout {

    private final CatalogoService service;
    private final Grid<InvCategoria> grid = new Grid<>(InvCategoria.class);

    public CategoriesView(CatalogoService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        configureGrid();
        
        
        Button addBtn = new Button("Nueva Categoría", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openCategoryDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Listado de Categorías"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "descripcion");
        grid.addColumn(c -> c.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllCategorias());
    }

    private void openCategoryDialog() {
        TailwindModal modal = new TailwindModal("Nueva Categoría");
        
        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full", "max-w-lg");
        
        TextField nombre = new TextField("Nombre");
        nombre.addClassName("w-full");
        TextField descripcion = new TextField("Descripción");
        descripcion.addClassName("w-full");
        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(true);

        formLayout.add(nombre, descripcion, activo);
        modal.addContent(formLayout);
        
        Button saveButton = new Button("Guardar", e -> {
            Notification.show("Categoría preparada para guardar (Simulación)", 3000, Notification.Position.BOTTOM_END);
            modal.close();
        });
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button("Cancelar", e -> modal.close());
        cancelButton.addClassNames("bg-gray-200", "text-gray-700", "font-medium", "py-2", "px-4", "rounded-lg");

        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);
        
        add(modal);
        modal.open();
    }
}
