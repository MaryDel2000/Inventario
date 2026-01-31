package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.mariastaff.Inventario.backend.data.entity.GenSucursal;
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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import java.util.Locale;
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

        Button addBtn = new Button("Nuevo Almacén", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg",
                "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openWarehouseDialog(new InvAlmacen()));

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Listado de Almacenes"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "codigo", "tipoAlmacen", "direccion");
        grid.addColumn(a -> a.getSucursal() != null ? a.getSucursal().getNombre() : "Global").setHeader("Sucursal");

        // Active Status Column
        grid.addColumn(new ComponentRenderer<>(almacen -> {
            TailwindToggle toggle = new TailwindToggle("");
            toggle.setValue(Boolean.TRUE.equals(almacen.getActivo()));
            toggle.addValueChangeListener(e -> {
                almacen.setActivo(e.getValue());
                try {
                    service.saveAlmacen(almacen);
                    TailwindNotification.show("Estado actualizado", TailwindNotification.Type.SUCCESS);
                } catch (Exception ex) {
                    toggle.setValue(e.getOldValue());
                    TailwindNotification.show("Error al actualizar estado", TailwindNotification.Type.ERROR);
                }
            });
            return toggle;
        })).setHeader("Activo").setAutoWidth(true);

        // Actions Column
        grid.addComponentColumn(almacen -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            editBtn.addClickListener(e -> openWarehouseDialog(almacen));

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            deleteBtn.addClickListener(e -> deleteAlmacen(almacen));

            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllAlmacenes());
    }

    private void deleteAlmacen(InvAlmacen almacen) {
        try {
            service.deleteAlmacen(almacen);
            updateList();
            TailwindNotification.show("Almacén eliminado", TailwindNotification.Type.SUCCESS);
        } catch (Exception e) {
            TailwindNotification.show("No se puede eliminar (posiblemente en uso)", TailwindNotification.Type.ERROR);
        }
    }

    private void openWarehouseDialog(InvAlmacen almacen) {
        boolean isNew = almacen.getId() == null;
        TailwindModal modal = new TailwindModal(isNew ? "Nuevo Almacén" : "Editar Almacén");

        Binder<InvAlmacen> binder = new Binder<>(InvAlmacen.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full", "max-w-lg");

        TextField nombre = new TextField("Nombre");
        nombre.addClassName("w-full");
        nombre.addValueChangeListener(e -> {
            if (e.getValue() != null && !e.getValue().equals(e.getValue().toUpperCase(Locale.ROOT))) {
                nombre.setValue(e.getValue().toUpperCase(Locale.ROOT));
            }
        });

        TextField codigo = new TextField("Código");
        codigo.addClassName("w-full");
        codigo.addValueChangeListener(e -> {
            if (e.getValue() != null && !e.getValue().equals(e.getValue().toUpperCase(Locale.ROOT))) {
                codigo.setValue(e.getValue().toUpperCase(Locale.ROOT));
            }
        });

        Button generateCodeBtn = new Button(VaadinIcon.MAGIC.create());
        generateCodeBtn.setTooltipText("Generar código automáticamente");
        generateCodeBtn.addClickListener(e -> {
            String genCode = "ALM-" + (int) (Math.random() * 10000);
            codigo.setValue(genCode);
        });
        generateCodeBtn.addClassNames("mt-8");

        HorizontalLayout codeLayout = new HorizontalLayout(codigo, generateCodeBtn);
        codeLayout.setVerticalComponentAlignment(Alignment.END, generateCodeBtn);
        codeLayout.addClassName("w-full");

        ComboBox<String> tipoAlmacen = new ComboBox<>("Tipo Almacén");
        tipoAlmacen.setItems("PRINCIPAL", "SUCURSAL", "TRANSITO", "DEPOSITO", "MERMA", "PROVEEDOR");
        tipoAlmacen.addClassName("w-full");

        ComboBox<GenSucursal> sucursal = new ComboBox<>("Sucursal");
        sucursal.setItems(service.findAllSucursales());
        sucursal.setItemLabelGenerator(GenSucursal::getNombre);
        sucursal.addClassName("w-full");

        TextField direccion = new TextField("Dirección");
        direccion.addClassName("w-full");

        TailwindToggle activo = new TailwindToggle("Activo");

        // Binding
        binder.forField(nombre).asRequired("Requerido").bind(InvAlmacen::getNombre, InvAlmacen::setNombre);
        binder.forField(codigo).bind(InvAlmacen::getCodigo, InvAlmacen::setCodigo);
        binder.forField(tipoAlmacen).bind(InvAlmacen::getTipoAlmacen, InvAlmacen::setTipoAlmacen);
        binder.forField(sucursal).bind(InvAlmacen::getSucursal, InvAlmacen::setSucursal);
        binder.forField(direccion).bind(InvAlmacen::getDireccion, InvAlmacen::setDireccion);
        binder.forField(activo).bind(InvAlmacen::getActivo, InvAlmacen::setActivo);

        binder.readBean(almacen);
        if (isNew)
            activo.setValue(true);

        formLayout.add(nombre, codeLayout, tipoAlmacen, sucursal, direccion, activo);
        modal.addContent(formLayout);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(almacen);
                service.saveAlmacen(almacen);
                updateList();
                TailwindNotification.show("Almacén guardado correctamente", TailwindNotification.Type.SUCCESS);
                modal.close();
            } catch (ValidationException ex) {
                TailwindNotification.show("Revise los datos", TailwindNotification.Type.ERROR);
            } catch (Exception ex) {
                TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
            }
        });
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button("Cancelar", e -> modal.close());
        cancelButton.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium",
                "py-2", "px-4", "rounded-lg");

        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);

        add(modal);
        modal.open();
    }
}
