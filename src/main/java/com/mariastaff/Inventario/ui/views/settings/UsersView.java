package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.backend.data.entity.SysUsuario;
import com.mariastaff.Inventario.backend.data.entity.GenEntidad;
import com.mariastaff.Inventario.backend.service.AuthentikService;
import com.mariastaff.Inventario.backend.service.UserService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.mariastaff.Inventario.ui.components.base.TailwindCheckbox;
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
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;
import java.util.Map;
import java.util.Set;

@PageTitle("Usuarios | Configuración")
@Route(value = "settings/users", layout = MainLayout.class)
@PermitAll
public class UsersView extends VerticalLayout {

    private final UserService service;
    private final AuthentikService authentikService;
    private final Grid<SysUsuario> grid = new Grid<>(SysUsuario.class);

    // Permisos de la aplicación (Entitlements)
    private static final Map<String, String> PERMISSIONS_MAP = Map.of(
        "MODULE_INVENTORY", "Inventario y Compras",
        "MODULE_SALES", "Punto de Venta y Ventas",
        "MODULE_ACCOUNTING", "Contabilidad",
        "MODULE_REPORTS", "Reportes",
        "MODULE_SETTINGS", "Configuración del Sistema"
    );

    public UsersView(UserService service, AuthentikService authentikService) {
        this.service = service;
        this.authentikService = authentikService;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        Button addBtn = new Button("Nuevo Usuario", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openDialog(new SysUsuario()));

        Button syncBtn = new Button("Sincronizar Authentik", VaadinIcon.REFRESH.create());
        syncBtn.addClassNames("bg-blue-600", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all", "ml-2");
        syncBtn.addClickListener(e -> syncUsers());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Listado de Usuarios"), addBtn, syncBtn);
        header.addClassNames("w-full", "items-center", "gap-2");
        header.setFlexGrow(1, new AppLabel("Listado de Usuarios"));

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("username", "authentikUuid");
        grid.addColumn(u -> u.getEntidad() != null ? u.getEntidad().getNombreCompleto() : "-").setHeader("Nombre");
        grid.addColumn(u -> u.getActivo() ? "Sí" : "No").setHeader("Activo");
        
        grid.addComponentColumn(user -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addClassNames("text-text-secondary", "hover:text-primary", "p-2");
            editBtn.addClickListener(e -> openDialog(user));
            return editBtn;
        }).setHeader("Acciones");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAll());
    }
    
    // Sync Logic: Bidirectional
    private void syncUsers() {
        try {
            int createdLocal = 0;
            int createdAuth = 0;
            
            // 1. Authentik -> Local
            List<Map<String, Object>> authUsers = authentikService.listUsers();
            for (Map<String, Object> u : authUsers) {
                String username = (String) u.get("username");
                if (username == null || username.equals("authentik admin")) continue; 
                
                SysUsuario local = service.findByUsername(username);
                if (local == null) {
                    SysUsuario newUser = new SysUsuario();
                    newUser.setUsername(username);
                    newUser.setAuthentikUuid(String.valueOf(u.get("pk")));
                    newUser.setActivo((Boolean) u.get("is_active"));
                    
                    GenEntidad entidad = new GenEntidad();
                    entidad.setNombreCompleto((String) u.get("name"));
                    entidad.setEmail((String) u.get("email"));
                    entidad.setTipoEntidad("PERSONA");
                    newUser.setEntidad(entidad);
                    
                    service.save(newUser);
                    createdLocal++;
                } else if (local.getAuthentikUuid() == null) {
                    local.setAuthentikUuid(String.valueOf(u.get("pk")));
                    service.save(local);
                }
            }

            // 2. Local -> Authentik
            List<SysUsuario> localUsers = service.findAll();
            for (SysUsuario local : localUsers) {
                if (local.getAuthentikUuid() == null) {
                    try {
                        Map<String, Object> result = authentikService.createUser(
                            local.getUsername(),
                            local.getEntidad().getNombreCompleto(),
                            local.getEntidad().getEmail()
                        );
                        String pk = String.valueOf(result.get("pk"));
                        local.setAuthentikUuid(pk);
                        
                        authentikService.setPassword(pk, "Cambiar123!");
                        
                        service.save(local);
                        createdAuth++;
                    } catch (Exception e) {
                        System.err.println("Failed to sync local user to Authentik: " + local.getUsername());
                    }
                }
            }

            // 3. Crear entitlements estándar si no existen
            try {
                List<Map<String, Object>> existingEntitlements = authentikService.listEntitlements();
                Set<String> existingNames = new java.util.HashSet<>();
                for (Map<String, Object> ent : existingEntitlements) {
                    existingNames.add((String) ent.get("name"));
                }
                
                for (String permission : PERMISSIONS_MAP.keySet()) {
                    if (!existingNames.contains(permission)) {
                        Map<String, Object> attributes = new java.util.HashMap<>();
                        attributes.put("description", PERMISSIONS_MAP.get(permission));
                        authentikService.createEntitlement(permission, attributes);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error creando entitlements: " + e.getMessage());
            }

            TailwindNotification.show("Sincronización completa. Local creados: " + createdLocal + ", Authentik creados: " + createdAuth, TailwindNotification.Type.SUCCESS);
            updateList();
        } catch (Exception e) {
             TailwindNotification.show("Error al sincronizar: " + e.getMessage(), TailwindNotification.Type.ERROR);
             e.printStackTrace();
        }
    }

    private void openDialog(SysUsuario userToEdit) {
        boolean isNew = userToEdit.getId() == null;
        TailwindModal modal = new TailwindModal(isNew ? "Nuevo Usuario" : "Editar Usuario");
        
        SysUsuario item = userToEdit;
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
        if (!isNew) {
            username.setReadOnly(true);
        }

        PasswordField password = new PasswordField("Contraseña");
        password.addClassName("w-full");
        password.setPlaceholder(isNew ? "Obligatorio" : "Dejar en blanco para no cambiar");
        if (isNew) {
            password.setRequired(true);
        }

        TextField authentikUuid = new TextField("Authentik UUID");
        authentikUuid.addClassName("w-full");
        authentikUuid.setReadOnly(true);

        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(item.getActivo() != null ? item.getActivo() : true);
        
        // Permisos (Entitlements)
        com.vaadin.flow.component.html.Span permissionsLabel = new com.vaadin.flow.component.html.Span("Permisos de la Aplicación");
        permissionsLabel.addClassNames("text-sm", "font-medium", "text-gray-700", "mt-2");
        
        com.vaadin.flow.component.html.Div permissionsContainer = new com.vaadin.flow.component.html.Div();
        permissionsContainer.addClassNames("border", "border-gray-300", "rounded-md", "bg-white", "p-3", "flex", "flex-col", "gap-2", "max-h-40", "overflow-y-auto", "w-full", "shadow-sm");
        
        java.util.Map<String, TailwindCheckbox> permissionCheckboxMap = new java.util.HashMap<>();
        
        // Obtener entitlements disponibles
        try {
            List<Map<String, Object>> allEntitlements = authentikService.listEntitlements();
            
            for (Map<String, Object> ent : allEntitlements) {
                String entName = (String) ent.get("name");
                String displayName = PERMISSIONS_MAP.getOrDefault(entName, entName);
                
                TailwindCheckbox cb = new TailwindCheckbox(displayName);
                permissionCheckboxMap.put(entName, cb);
                permissionsContainer.add(cb);
            }
            
            // Pre-seleccionar permisos del usuario si está editando
            if (!isNew && item.getAuthentikUuid() != null) {
                try {
                    List<String> userEntitlements = authentikService.getUserEntitlements(item.getAuthentikUuid());
                    for (String entName : userEntitlements) {
                        if (permissionCheckboxMap.containsKey(entName)) {
                            permissionCheckboxMap.get(entName).setValue(true);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching user entitlements: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            TailwindNotification.show("Error cargando permisos: " + e.getMessage(), TailwindNotification.Type.ERROR);
        }
        
        formLayout.add(nombre, email, username, password, authentikUuid, activo, permissionsLabel, permissionsContainer);
        formLayout.setColspan(nombre, 2);
        formLayout.setColspan(permissionsContainer, 2);
        
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
        
        binder.readBean(item);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(item);

                if (isNew && password.isEmpty()) {
                     TailwindNotification.show("La contraseña es obligatoria para nuevos usuarios", TailwindNotification.Type.ERROR);
                     return;
                }

                try {
                    String pk = null;
                    if (isNew) {
                        Map<String, Object> result = authentikService.createUser(item.getUsername(), 
                                                                               item.getEntidad().getNombreCompleto(), 
                                                                               item.getEntidad().getEmail());
                        pk = String.valueOf(result.get("pk"));
                        item.setAuthentikUuid(pk);
                        
                        if (!password.isEmpty()) authentikService.setPassword(pk, password.getValue());
                    } else {
                        if (item.getAuthentikUuid() != null) {
                            pk = item.getAuthentikUuid();
                            
                            authentikService.updateUser(pk, item.getUsername(), 
                                                      item.getEntidad().getNombreCompleto(), 
                                                      item.getEntidad().getEmail(), 
                                                      item.getActivo());
                            if (!password.isEmpty()) authentikService.setPassword(pk, password.getValue());
                        }
                    }

                    // Manejar asignación de entitlements
                    if (pk != null) {
                        // Obtener entitlements seleccionados
                        Set<String> selectedPermissions = new java.util.HashSet<>();
                        permissionCheckboxMap.forEach((k, v) -> {
                            if (v.getValue()) selectedPermissions.add(k);
                        });
                        
                        // TODO: Actualizar entitlements del usuario
                        // Nota: Esto requiere llamar a assignEntitlementToUser/removeEntitlementFromUser
                        // por cada entitlement que cambió
                        
                        List<Map<String, Object>> allEntitlements = authentikService.listEntitlements();
                        for (Map<String, Object> ent : allEntitlements) {
                            String entName = (String) ent.get("name");
                            String entPk = String.valueOf(ent.get("pk"));
                            
                            if (selectedPermissions.contains(entName)) {
                                // Asignar este entitlement (el método manejará si ya existe)
                                authentikService.assignEntitlementToUser(entPk, pk);
                            }
                            // TODO: Implementar removeEntitlementFromUser cuando esté disponible
                        }
                    }

                } catch (Exception apiEx) {
                    TailwindNotification.show("Error en Authentik: " + apiEx.getMessage(), TailwindNotification.Type.ERROR);
                    apiEx.printStackTrace();
                    return; 
                }

                service.save(item);
                TailwindNotification.show("Usuario guardado", TailwindNotification.Type.SUCCESS);
                updateList();
                modal.close();
            } catch (ValidationException ex) {
                TailwindNotification.show("Valide los campos", TailwindNotification.Type.ERROR);
            }
        });
        saveButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);
        
        Button cancelButton = new Button("Cancelar", e -> modal.close());
        cancelButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_CONTRAST);
        
        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);
        add(modal);
        modal.open();
    }
}
