package com.mariastaff.Inventario.ui.views.purchases;

import com.mariastaff.Inventario.backend.data.entity.InvProveedor;
import com.mariastaff.Inventario.backend.data.entity.GenEntidad;
import com.mariastaff.Inventario.backend.service.CompraService;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Proveedores | Compras")
@Route(value = "purchases/providers", layout = MainLayout.class)
@PermitAll
public class ProvidersView extends VerticalLayout {

    private final CompraService service;
    private final Grid<InvProveedor> grid = new Grid<>(InvProveedor.class);

    public ProvidersView(CompraService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        Button addBtn = new Button("Nuevo Proveedor", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Listado de Proveedores"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");
        
        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns();
        grid.addColumn(p -> p.getEntidad() != null ? p.getEntidad().getNombreCompleto() : "-").setHeader("Nombre");
        grid.addColumn(p -> p.getEntidad() != null ? p.getEntidad().getIdentificacion() : "-").setHeader("Identificación");
        grid.addColumn(p -> p.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllProveedores());
    }

    private void openDialog() {
        TailwindModal modal = new TailwindModal("Nuevo Proveedor");
        
        InvProveedor item = new InvProveedor();
        if (item.getEntidad() == null) {
            item.setEntidad(new GenEntidad());
            item.getEntidad().setTipoEntidad("EMPRESA");
        }

        Binder<InvProveedor> binder = new Binder<>(InvProveedor.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                                      new FormLayout.ResponsiveStep("600px", 2));
        
        TextField nombre = new TextField("Nombre Completo / Razón Social");
        nombre.addClassName("w-full");
        
        TextField identificacion = new TextField("Identificación / RUC");
        identificacion.addClassName("w-full");
        
        TextField telefono = new TextField("Teléfono");
        telefono.addClassName("w-full");

        EmailField email = new EmailField("Email");
        email.addClassName("w-full");
        
        TextField direccion = new TextField("Dirección");
        direccion.addClassName("w-full");

        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(true);

        formLayout.add(nombre, identificacion, telefono, email, direccion, activo);
        formLayout.setColspan(nombre, 2);
        formLayout.setColspan(direccion, 2);
        
        modal.addContent(formLayout);

        binder.forField(nombre)
            .asRequired("El nombre es obligatorio")
            .bind(p -> p.getEntidad().getNombreCompleto(), 
                  (p, v) -> p.getEntidad().setNombreCompleto(v));
            
        binder.forField(identificacion)
            .bind(p -> p.getEntidad().getIdentificacion(), 
                  (p, v) -> p.getEntidad().setIdentificacion(v));
            
        binder.forField(telefono)
            .bind(p -> p.getEntidad().getTelefono(), 
                  (p, v) -> p.getEntidad().setTelefono(v));
            
        binder.forField(email)
            .bind(p -> p.getEntidad().getEmail(), 
                  (p, v) -> p.getEntidad().setEmail(v));
            
        binder.forField(direccion)
            .bind(p -> p.getEntidad().getDireccion(), 
                  (p, v) -> p.getEntidad().setDireccion(v));
            
        binder.forField(activo).bind(InvProveedor::getActivo, InvProveedor::setActivo);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(item);
                // service.save(item);
                TailwindNotification.show("Proveedor guardado (Simulación)", TailwindNotification.Type.SUCCESS);
                updateList();
                modal.close();
            } catch (ValidationException ex) {
                TailwindNotification.show("Revise los campos requeridos", TailwindNotification.Type.ERROR);
            }
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
