package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.mariastaff.Inventario.backend.service.AlmacenService;
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

@PageTitle("Almacenes | Inventario")
@Route(value = "inventory/warehouses", layout = MainLayout.class)
@PermitAll
public class WarehousesView extends VerticalLayout {

    private final AlmacenService service;
    private final Grid<InvAlmacen> grid = new Grid<>(InvAlmacen.class);

    public WarehousesView(AlmacenService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        configureGrid();
        
        
        Button addBtn = new Button("Nuevo Almacén", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openWarehouseDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Listado de Almacenes"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "codigo", "tipoAlmacen", "direccion");
        grid.addColumn(a -> a.getSucursal() != null ? a.getSucursal().getNombre() : "Global").setHeader("Sucursal");
        grid.addColumn(a -> a.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllAlmacenes());
    }

    private void openWarehouseDialog() {
        TailwindModal modal = new TailwindModal("Nuevo Almacén");
        
        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full", "max-w-lg");
        
        TextField nombre = new TextField("Nombre");
        nombre.addClassName("w-full");
        TextField codigo = new TextField("Código");
        codigo.addClassName("w-full");
        TextField tipoAlmacen = new TextField("Tipo Almacén");
        tipoAlmacen.addClassName("w-full");
        TextField direccion = new TextField("Dirección");
        direccion.addClassName("w-full");
        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(true);

        formLayout.add(nombre, codigo, tipoAlmacen, direccion, activo);
        modal.addContent(formLayout);
        
        Button saveButton = new Button("Guardar", e -> {
            TailwindNotification.show("Nuevo Almacén guardado correctamente", TailwindNotification.Type.SUCCESS);
            modal.close();
        });
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button("Cancelar", e -> {
            TailwindNotification.show("Cambios descartados", TailwindNotification.Type.INFO);
            modal.close();
        });
        cancelButton.addClassNames("bg-gray-200", "text-gray-700", "font-medium", "py-2", "px-4", "rounded-lg");

        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);
        
        add(modal);
        modal.open();
    }
}
