package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.GenSucursal;
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
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.mariastaff.Inventario.ui.components.base.TailwindConfirmationDialog;

@PageTitle("Sucursales | Inventario")
@Route(value = "inventory/branches", layout = MainLayout.class)
@PermitAll
public class BranchesView extends VerticalLayout {

    private final GeneralService service;
    private final Grid<GenSucursal> grid = new Grid<>(GenSucursal.class);

    public BranchesView(GeneralService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");

        configureGrid();

        Button addBtn = new Button("Nueva Sucursal", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg",
                "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openDialog(new GenSucursal()));

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Sucursales"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "codigo", "direccion", "telefono");
        grid.addColumn(s -> s.getActivo() ? "Sí" : "No").setHeader("Activo");

        grid.addComponentColumn(branch -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addClassNames("text-text-secondary", "hover:text-primary", "p-2", "mr-2");
            editBtn.addClickListener(e -> openDialog(branch));

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700", "p-2");
            deleteBtn.addClickListener(e -> confirmDelete(branch));

            HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
            return actions;
        }).setHeader("Acciones");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllSucursales());
    }

    private void confirmDelete(GenSucursal branch) {
        TailwindConfirmationDialog dialog = new TailwindConfirmationDialog(
                "Eliminar Sucursal",
                "¿Estás seguro de que deseas eliminar la sucursal '" + branch.getNombre()
                        + "'? Esta acción no se puede deshacer.",
                "Eliminar",
                () -> {
                    try {
                        service.deleteSucursal(branch);
                        TailwindNotification.show("Sucursal eliminada exitosamente", TailwindNotification.Type.SUCCESS);
                        updateList();
                    } catch (Exception e) {
                        TailwindNotification.show("No se puede eliminar la sucursal porque está en uso",
                                TailwindNotification.Type.ERROR);
                    }
                });
        dialog.open();
    }

    private void openDialog(GenSucursal branchToEdit) {
        boolean isNew = branchToEdit.getId() == null;
        TailwindModal modal = new TailwindModal(isNew ? "Nueva Sucursal" : "Editar Sucursal");

        GenSucursal item = branchToEdit;
        Binder<GenSucursal> binder = new Binder<>(GenSucursal.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full");

        TextField nombre = new TextField("Nombre");
        nombre.addClassName("w-full");

        TextField codigo = new TextField("Código");
        codigo.addClassName("w-full");

        Button generateCodeBtn = new Button(VaadinIcon.MAGIC.create());
        generateCodeBtn.setTooltipText("Generar código automáticamente");
        generateCodeBtn.addClickListener(e -> {
            String genCode = "SUC-" + (int) (Math.random() * 10000);
            codigo.setValue(genCode);
        });
        generateCodeBtn.addClassNames("mt-8"); // Align with text field input area (skipping label)

        HorizontalLayout codeLayout = new HorizontalLayout(codigo, generateCodeBtn);
        codeLayout.setVerticalComponentAlignment(Alignment.END, generateCodeBtn);
        codeLayout.addClassName("w-full");

        TextField direccion = new TextField("Dirección");
        direccion.addClassName("w-full");

        TextField telefono = new TextField("Teléfono");
        telefono.addClassName("w-full");

        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(item.getActivo() != null ? item.getActivo() : true);

        formLayout.add(nombre, codeLayout, direccion, telefono, activo);

        modal.addContent(formLayout);

        binder.forField(nombre).asRequired("Nombre obligatorio").bind(GenSucursal::getNombre, GenSucursal::setNombre);
        binder.forField(codigo).bind(GenSucursal::getCodigo, GenSucursal::setCodigo);
        binder.forField(direccion).bind(GenSucursal::getDireccion, GenSucursal::setDireccion);
        binder.forField(telefono).bind(GenSucursal::getTelefono, GenSucursal::setTelefono);
        binder.forField(activo).bind(GenSucursal::getActivo, GenSucursal::setActivo);

        binder.readBean(item);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(item);
                service.saveSucursal(item);
                TailwindNotification.show("Sucursal guardada exitosamente", TailwindNotification.Type.SUCCESS);
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
        cancelButton.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium",
                "py-2", "px-4", "rounded-lg");

        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);

        add(modal);
        modal.open();
    }
}
