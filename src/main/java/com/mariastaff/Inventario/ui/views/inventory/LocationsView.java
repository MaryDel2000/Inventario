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
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.components.base.TailwindToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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

        Button addBtn = new Button(getTranslation("view.locations.action.new"), VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg",
                "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openLocationDialog(new InvUbicacion()));

        HorizontalLayout header = new HorizontalLayout(new AppLabel("view.locations.title"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn(InvUbicacion::getCodigo).setHeader(getTranslation("view.locations.grid.code"))
                .setAutoWidth(true);
        grid.addColumn(InvUbicacion::getDescripcion).setHeader(getTranslation("view.locations.grid.description"))
                .setAutoWidth(true);

        grid.addColumn(u -> u.getAlmacen() != null ? u.getAlmacen().getNombre() : "-")
                .setHeader(getTranslation("view.locations.grid.warehouse"));

        // Active Toggle Column
        grid.addColumn(new ComponentRenderer<>(ubicacion -> {
            TailwindToggle toggle = new TailwindToggle("");
            toggle.setValue(Boolean.TRUE.equals(ubicacion.getActivo()));
            toggle.addValueChangeListener(e -> {
                ubicacion.setActivo(e.getValue());
                try {
                    service.saveUbicacion(ubicacion);
                    TailwindNotification.show(getTranslation("msg.status.updated"), TailwindNotification.Type.SUCCESS);
                } catch (Exception ex) {
                    toggle.setValue(e.getOldValue());
                    TailwindNotification.show(getTranslation("msg.status.error"), TailwindNotification.Type.ERROR);
                }
            });
            return toggle;
        })).setHeader(getTranslation("view.products.grid.active")).setAutoWidth(true);

        // Actions Column
        grid.addComponentColumn(ubicacion -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            editBtn.addClickListener(e -> openLocationDialog(ubicacion));

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            deleteBtn.addClickListener(e -> deleteUbicacion(ubicacion));

            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader(getTranslation("view.products.grid.actions")).setAutoWidth(true);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllUbicaciones());
    }

    private void deleteUbicacion(InvUbicacion ubicacion) {
        try {
            service.deleteUbicacion(ubicacion);
            updateList();
            TailwindNotification.show(getTranslation("view.locations.msg.deleted"), TailwindNotification.Type.SUCCESS);
        } catch (Exception e) {
            TailwindNotification.show(getTranslation("msg.error.in_use"), TailwindNotification.Type.ERROR);
        }
    }

    private void openLocationDialog(InvUbicacion ubicacion) {
        boolean isNew = ubicacion.getId() == null;
        TailwindModal modal = new TailwindModal(isNew ? getTranslation("view.locations.editor.title.new")
                : getTranslation("view.locations.editor.title.edit"));

        Binder<InvUbicacion> binder = new Binder<>(InvUbicacion.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full", "max-w-lg");

        TextField codigo = new TextField(getTranslation("view.locations.grid.code"));
        codigo.addClassName("w-full");

        TextField descripcion = new TextField(getTranslation("view.locations.grid.description"));
        descripcion.addClassName("w-full");

        ComboBox<InvAlmacen> almacen = new ComboBox<>(getTranslation("view.locations.grid.warehouse"));
        almacen.setItems(service.findAllAlmacenes());
        almacen.setItemLabelGenerator(InvAlmacen::getNombre);
        almacen.addClassName("w-full");

        TailwindToggle activo = new TailwindToggle(getTranslation("view.products.grid.active"));

        // Binding
        binder.forField(codigo).asRequired(getTranslation("field.required")).bind(InvUbicacion::getCodigo,
                InvUbicacion::setCodigo);
        binder.forField(descripcion).bind(InvUbicacion::getDescripcion, InvUbicacion::setDescripcion);
        binder.forField(almacen).asRequired(getTranslation("field.required")).bind(InvUbicacion::getAlmacen,
                InvUbicacion::setAlmacen);
        binder.forField(activo).bind(InvUbicacion::getActivo, InvUbicacion::setActivo);

        binder.readBean(ubicacion);
        if (isNew)
            activo.setValue(true);

        formLayout.add(codigo, descripcion, almacen, activo);
        modal.addContent(formLayout);

        Button saveButton = new Button(getTranslation("action.save"), e -> {
            try {
                binder.writeBean(ubicacion);
                service.saveUbicacion(ubicacion);
                updateList();
                TailwindNotification.show(getTranslation("view.locations.msg.saved"),
                        TailwindNotification.Type.SUCCESS);
                modal.close();
            } catch (ValidationException ex) {
                TailwindNotification.show(getTranslation("msg.error.validation"), TailwindNotification.Type.ERROR);
            } catch (Exception ex) {
                TailwindNotification.show(getTranslation("msg.error.save"), TailwindNotification.Type.ERROR);
            }
        });
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button(getTranslation("action.cancel"), e -> modal.close());
        cancelButton.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium",
                "py-2", "px-4", "rounded-lg");

        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);

        add(modal);
        modal.open();
    }
}
