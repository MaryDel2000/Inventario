package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.annotation.security.PermitAll;

@PageTitle("Ubicaciones | Inventario")
@Route(value = "inventory/locations", layout = MainLayout.class)
@PermitAll
public class LocationsView extends VerticalLayout {

    private final AlmacenService service;
    private final Grid<InvUbicacion> grid = new Grid<>(InvUbicacion.class);

    public LocationsView(AlmacenService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        configureGrid();
        
        
        Button addBtn = new Button("Nueva Ubicación", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openLocationDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Ubicaciones"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("codigo", "descripcion");
        grid.addColumn(u -> u.getAlmacen() != null ? u.getAlmacen().getNombre() : "-").setHeader("Almacén");
        grid.addColumn(u -> u.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllUbicaciones());
    }

    private void openLocationDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Nueva Ubicación");
        
        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full", "max-w-lg");
        
        TextField codigo = new TextField("Código");
        TextField descripcion = new TextField("Descripción");
        ComboBox<InvAlmacen> almacen = new ComboBox<>("Almacén");
        almacen.setItems(service.findAllAlmacenes());
        almacen.setItemLabelGenerator(InvAlmacen::getNombre);
        
        Checkbox activo = new Checkbox("Activo");
        activo.setValue(true);

        formLayout.add(codigo, descripcion, almacen, activo);
        
        Button saveButton = new Button("Guardar", e -> {
            Notification.show("Ubicación preparada para guardar (Simulación)", 3000, Notification.Position.BOTTOM_END);
            dialog.close();
        });
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        cancelButton.addClassNames("bg-gray-200", "text-gray-700", "font-medium", "py-2", "px-4", "rounded-lg");

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        
        dialog.add(formLayout);
        dialog.open();
    }
}
