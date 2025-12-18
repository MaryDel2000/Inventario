package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.backend.data.entity.SysUsuario;
import com.mariastaff.Inventario.backend.data.entity.GenEntidad;
import com.mariastaff.Inventario.backend.service.UserService;
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

@PageTitle("Usuarios | Configuración")
@Route(value = "settings/users", layout = MainLayout.class)
@PermitAll
public class UsersView extends VerticalLayout {

    private final UserService service;
    private final Grid<SysUsuario> grid = new Grid<>(SysUsuario.class);

    public UsersView(UserService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        Button addBtn = new Button("Nuevo Usuario", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Listado de Usuarios"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("username", "authentikUuid");
        grid.addColumn(u -> u.getEntidad() != null ? u.getEntidad().getNombreCompleto() : "-").setHeader("Nombre");
        grid.addColumn(u -> u.getActivo() ? "Sí" : "No").setHeader("Activo");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAll());
    }

    private void openDialog() {
        TailwindModal modal = new TailwindModal("Nuevo Usuario");
        
        SysUsuario item = new SysUsuario();
        if (item.getEntidad() == null) {
            item.setEntidad(new GenEntidad());
            item.getEntidad().setTipoEntidad("PERSONA");
        }

        Binder<SysUsuario> binder = new Binder<>(SysUsuario.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                                      new FormLayout.ResponsiveStep("600px", 2));
        
        TextField nombre = new TextField("Nombre Completo");
        nombre.addClassName("w-full");
        
        EmailField email = new EmailField("Email");
        email.addClassName("w-full");
        
        TextField username = new TextField("Username");
        username.addClassName("w-full");
        
        TextField authentikUuid = new TextField("Authentik UUID");
        authentikUuid.addClassName("w-full");

        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(true);

        formLayout.add(nombre, email, username, authentikUuid, activo);
        formLayout.setColspan(nombre, 2);
        
        modal.addContent(formLayout);

        // Binding
        binder.forField(nombre)
            .asRequired("Nombre obligatorio")
            .bind(u -> u.getEntidad().getNombreCompleto(), 
                  (u, v) -> u.getEntidad().setNombreCompleto(v));
        
        binder.forField(email)
            .bind(u -> u.getEntidad().getEmail(), 
                  (u, v) -> u.getEntidad().setEmail(v));
            
        binder.forField(username).asRequired("Username obligatorio").bind(SysUsuario::getUsername, SysUsuario::setUsername);
        binder.forField(authentikUuid).bind(SysUsuario::getAuthentikUuid, SysUsuario::setAuthentikUuid);
        binder.forField(activo).bind(SysUsuario::getActivo, SysUsuario::setActivo);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(item);
                TailwindNotification.show("Usuario guardado (Simulación)", TailwindNotification.Type.SUCCESS);
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
