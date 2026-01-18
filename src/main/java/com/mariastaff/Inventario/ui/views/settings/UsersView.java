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
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Usuarios | Configuración")
@Route(value = "settings/users", layout = MainLayout.class)
@PermitAll
public class UsersView extends VerticalLayout {

    private final UserService service;
    private final AuthentikService authentikService;
    private final Grid<SysUsuario> grid = new Grid<>(SysUsuario.class);

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

        Button rolesBtn = new Button("Gestionar Roles", VaadinIcon.GROUP.create());
        rolesBtn.addClassNames("bg-green-600", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all", "ml-2");
        rolesBtn.addClickListener(e -> openRolesDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Listado de Usuarios"), addBtn, syncBtn, rolesBtn);
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
                    newUser.setAuthentikUuid(String.valueOf(u.get("pk"))); // PK is likely UUID string now. Safely convert.
                    newUser.setActivo((Boolean) u.get("is_active"));
                    
                    GenEntidad entidad = new GenEntidad();
                    entidad.setNombreCompleto((String) u.get("name"));
                    entidad.setEmail((String) u.get("email"));
                    entidad.setTipoEntidad("PERSONA");
                    newUser.setEntidad(entidad);
                    
                    service.save(newUser);
                    createdLocal++;
                } else if (local.getAuthentikUuid() == null) {
                    // Link existing local to Authentik if name matches
                    local.setAuthentikUuid(String.valueOf(u.get("pk")));
                    service.save(local);
                }
            }

            // 2. Local -> Authentik
            List<SysUsuario> localUsers = service.findAll();
            for (SysUsuario local : localUsers) {
                if (local.getAuthentikUuid() == null) {
                    // Check if it exists in Authentik by username (already covered by step 1 loop usually, but user might be missing in Auth list if pagination issues or just created locally)
                    // If we are here, it wasn't in the Auth list or username didn't match.
                    // Try to create in Authentik
                    try {
                        Map<String, Object> result = authentikService.createUser(
                            local.getUsername(),
                            local.getEntidad().getNombreCompleto(),
                            local.getEntidad().getEmail()
                        );
                        // In v3, 'pk' usually IS the UUID string. 'uid' might also exist.
                        // We'll prioritize 'pk' as the identifier for API calls.
                        String pk = String.valueOf(result.get("pk"));
                        local.setAuthentikUuid(pk);
                        
                        // Set default password or try to reuse? We can't reuse hash. 
                        // Set a default temp password
                        authentikService.setPassword(pk, "Cambiar123!");
                        
                        service.save(local);
                        createdAuth++;
                    } catch (Exception e) {
                        // Ignore if user already exists but mismatch, or log
                        System.err.println("Failed to sync local user to Authentik: " + local.getUsername());
                    }
                }
            }

            // 3. Sync Standard Roles (Ensure they exist)
            String[] standardRoles = {"ADMIN", "USER", "CAJERO", "INVENTARIO"};
            for (String role : standardRoles) {
                try {
                     authentikService.createGroup(role, null);
                } catch (Exception e) {}
            }

            TailwindNotification.show("Sincronización completa. Local creados: " + createdLocal + ", Authentik creados: " + createdAuth, TailwindNotification.Type.SUCCESS);
            updateList();
        } catch (Exception e) {
             TailwindNotification.show("Error al sincronizar: " + e.getMessage(), TailwindNotification.Type.ERROR);
             e.printStackTrace();
        }
    }

    private static final Map<String, String> PERMISSIONS_MAP = Map.of(
        "MODULE_INVENTORY", "Inventario y Compras",
        "MODULE_SALES", "Punto de Venta y Ventas",
        "MODULE_ACCOUNTING", "Contabilidad",
        "MODULE_REPORTS", "Reportes",
        "MODULE_SETTINGS", "Configuración del Sistema"
    );

    private void openRolesDialog() {
        TailwindModal modal = new TailwindModal("Gestión de Roles (Grupos)");
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        
        Grid<Map<String, Object>> rolesGrid = new Grid<>();
        rolesGrid.addColumn(g -> g.get("name")).setHeader("Nombre");
        rolesGrid.addColumn(g -> (g.get("users") instanceof List) ? ((List<?>)g.get("users")).size() : 0).setHeader("Usuarios"); 
        
        Runnable refreshRoles = () -> {
            try {
                List<Map<String, Object>> groups = authentikService.listGroups();
                rolesGrid.setItems(groups);
                if (groups.isEmpty()) {
                   // Optional warning
                }
            } catch (Exception e) {
                TailwindNotification.show("Error al cargar roles: " + e.getMessage(), TailwindNotification.Type.ERROR);
            }
        };
        
        rolesGrid.addColumn(g -> {
            Map<String, Object> attrs = (Map<String, Object>) g.get("attributes");
            if (attrs != null && attrs.containsKey("app_permissions")) {
                List<String> perms = (List<String>) attrs.get("app_permissions");
                return perms.size() + " Permisos";
            }
            return getRolePermissions((String) g.get("name")); // Fallback to hardcoded
        }).setHeader("Permisos del Sistema").setAutoWidth(true);

        rolesGrid.addComponentColumn(g -> {
            HorizontalLayout actions = new HorizontalLayout();
            
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> {
                 modal.close();
                 openRoleEditor(g, refreshRoles);
            });
            
            Button delBtn = new Button(VaadinIcon.TRASH.create());
            delBtn.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR);
            delBtn.addClickListener(e -> {
                if (isSystemRole((String) g.get("name"))) {
                     TailwindNotification.show("No puede eliminar roles del sistema", TailwindNotification.Type.WARNING);
                     return;
                }
                try {
                    String pk = String.valueOf(g.get("pk")); 
                    authentikService.deleteGroup(pk);
                    TailwindNotification.show("Rol eliminado", TailwindNotification.Type.SUCCESS);
                    refreshRoles.run();
                } catch (Exception ex) {
                     TailwindNotification.show("Error al eliminar: " + ex.getMessage(), TailwindNotification.Type.ERROR);
                }
            });
            actions.add(editBtn, delBtn);
            return actions;
        }).setHeader("Acciones");

        // Initial Load
        refreshRoles.run();

        // Add New Role UI
        HorizontalLayout addLayout = new HorizontalLayout();
        addLayout.addClassNames("w-full", "items-end", "gap-2");
        
        TextField newRoleName = new TextField("Nuevo Rol");
        newRoleName.setPlaceholder("Nombre del rol...");
        newRoleName.addClassName("flex-grow");
        
        Button createBtn = new Button("Crear Rol", VaadinIcon.PLUS.create());
        createBtn.addClassNames("bg-green-600", "text-white", "font-semibold");
        createBtn.addClickListener(e -> {
            if (!newRoleName.isEmpty()) {
                try {
                    // Create basic group first
                    Map<String, Object> newGroup = authentikService.createGroup(newRoleName.getValue(), null);
                    if (newGroup != null) {
                        TailwindNotification.show("Rol '" + newRoleName.getValue() + "' creado. Ahora configure los permisos.", TailwindNotification.Type.SUCCESS);
                        modal.close();
                        openRoleEditor(newGroup, refreshRoles);
                    } else {
                         TailwindNotification.show("No se pudo crear el rol (respuesta vacía)", TailwindNotification.Type.ERROR);
                    }
                } catch (Exception ex) {
                    TailwindNotification.show("Error: " + ex.getMessage(), TailwindNotification.Type.ERROR);
                    ex.printStackTrace();
                }
            } else {
                TailwindNotification.show("Escriba un nombre para el rol", TailwindNotification.Type.WARNING);
            }
        });
        
        addLayout.add(newRoleName, createBtn);
        addLayout.setFlexGrow(1, newRoleName);
        
        layout.add(addLayout, rolesGrid);
        modal.addContent(layout);
        
        Button closeBtn = new Button("Cerrar", e -> modal.close());
        closeBtn.addClassNames("bg-gray-200");
        modal.addFooterButton(closeBtn);
        
        add(modal);
        modal.open();
    }

    // Nested Custom Checkbox to ensure Tailwind styling works


    private void openRoleEditor(Map<String, Object> group, Runnable feedbackCallback) {
        String name = (String) group.get("name");
        String pk = String.valueOf(group.get("pk"));
        
        TailwindModal modal = new TailwindModal("Editar Rol: " + name);
        VerticalLayout layout = new VerticalLayout();
        
        TextField roleName = new TextField("Nombre del Rol");
        roleName.setValue(name);
        roleName.setWidthFull();
        if (isSystemRole(name)) roleName.setReadOnly(true);
        
        layout.add(roleName);

        // Permissions Section
        com.vaadin.flow.component.html.H5 permLabel = new com.vaadin.flow.component.html.H5("Permisos de Acceso");
        permLabel.addClassName("mt-4");
        permLabel.addClassNames("text-lg", "font-semibold", "text-text-primary");
        layout.add(permLabel);

        // Container for checkboxes
        VerticalLayout checksLayout = new VerticalLayout();
        checksLayout.setPadding(false);
        checksLayout.setSpacing(true);
        
        java.util.Map<String, TailwindCheckbox> checkboxMap = new java.util.HashMap<>();
        
        // Load current permissions
        java.util.Set<String> currentPerms = new java.util.HashSet<>();
        Map<String, Object> attrs = (Map<String, Object>) group.get("attributes");
        if (attrs != null && attrs.containsKey("app_permissions")) {
            List<String> perms = (List<String>) attrs.get("app_permissions");
            currentPerms.addAll(perms);
        } else {
            // Fallback defaults
             if ("ADMIN".equals(name) || "ADMINS".equals(name)) currentPerms.addAll(PERMISSIONS_MAP.keySet());
             else if ("CAJERO".equals(name)) currentPerms.add("MODULE_SALES");
             else if ("INVENTARIO".equals(name)) currentPerms.add("MODULE_INVENTORY");
             else if ("CONTADOR".equals(name)) { currentPerms.add("MODULE_ACCOUNTING"); currentPerms.add("MODULE_REPORTS"); }
        }

        // Create Checkboxes in specific order
        java.util.List<String> orderedKeys = java.util.List.of(
            "MODULE_INVENTORY", "MODULE_SALES", "MODULE_ACCOUNTING", "MODULE_REPORTS", "MODULE_SETTINGS"
        );

        for (String key : orderedKeys) {
            String label = PERMISSIONS_MAP.getOrDefault(key, key);
            TailwindCheckbox cb = new TailwindCheckbox(label);
            cb.setValue(currentPerms.contains(key));
            checkboxMap.put(key, cb);
            checksLayout.add(cb);
        }
        
        layout.add(checksLayout);
        modal.addContent(layout);
        
        Button saveBtn = new Button("Guardar Cambios", e -> {
            try {
                Map<String, Object> newAttrs = new java.util.HashMap<>();
                if (attrs != null) newAttrs.putAll(attrs);
                
                // Collect values
                java.util.Set<String> selected = new java.util.HashSet<>();
                checkboxMap.forEach((k, v) -> {
                    if (v.getValue()) selected.add(k);
                });
                newAttrs.put("app_permissions", selected);
                
                authentikService.updateGroup(pk, roleName.getValue(), newAttrs);
                TailwindNotification.show("Rol actualizado correctamente", TailwindNotification.Type.SUCCESS);
                modal.close();
                // Re-open parent dialog
                openRolesDialog(); 
            } catch (Exception ex) {
                 TailwindNotification.show("Error al actualizar: " + ex.getMessage(), TailwindNotification.Type.ERROR);
                 ex.printStackTrace();
            }
        });
        saveBtn.addClassNames("bg-primary", "text-white", "font-semibold");
        
        Button cancelBtn = new Button("Cancelar", e -> { modal.close(); openRolesDialog(); });
        cancelBtn.addClassNames("text-text-secondary", "hover:bg-gray-100");
        
        modal.addFooterButton(cancelBtn);
        modal.addFooterButton(saveBtn);
        
        add(modal);
        modal.open();
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
        
        // Dynamic Roles Loading using Styled Checkbox List
        com.vaadin.flow.component.html.Span rolesLabel = new com.vaadin.flow.component.html.Span("Roles (Grupos en Authentik)");
        rolesLabel.addClassNames("text-sm", "font-medium", "text-gray-700", "mt-2"); // Matching label style
        
        com.vaadin.flow.component.html.Div rolesContainer = new com.vaadin.flow.component.html.Div();
        rolesContainer.addClassNames("border", "border-gray-300", "rounded-md", "bg-white", "p-3", "flex", "flex-col", "gap-2", "max-h-40", "overflow-y-auto", "w-full", "shadow-sm");
        
        java.util.Map<String, TailwindCheckbox> roleCheckboxMap = new java.util.HashMap<>();
        
        // Fetch real groups from Authentik
        List<Map<String, Object>> groups = authentikService.listGroups();
        if (groups.isEmpty()) {
            TailwindNotification.show("Advertencia: No se encontraron grupos en Authentik o falló la conexión.", TailwindNotification.Type.WARNING);
        }
        
        List<String> groupNames = groups.stream().map(g -> (String)g.get("name")).collect(Collectors.toList());
        
        for (String role : groupNames) {
            TailwindCheckbox cb = new TailwindCheckbox(role);
            roleCheckboxMap.put(role, cb);
            rolesContainer.add(cb);
        }
        
        // Pre-select user groups if editing
        if (!isNew && item.getAuthentikUuid() != null) {
             try {
                String pk = authentikService.getPkByUuid(item.getAuthentikUuid());
                if (pk != null) {
                    List<String> userGroups = authentikService.getUserGroupNames(pk);
                    for (String userRole : userGroups) {
                        if (roleCheckboxMap.containsKey(userRole)) {
                            roleCheckboxMap.get(userRole).setValue(true);
                        }
                    }
                }
             } catch (Exception e) {
                 System.err.println("Error fetching user groups: " + e.getMessage());
             }
        }
        
        formLayout.add(nombre, email, username, password, authentikUuid, activo, rolesLabel, rolesContainer);
        formLayout.setColspan(nombre, 2);
        formLayout.setColspan(rolesContainer, 2); // Full width for roles
        
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
                            pk = item.getAuthentikUuid(); // Trust the stored UUID as PK
                            
                            authentikService.updateUser(pk, item.getUsername(), 
                                                      item.getEntidad().getNombreCompleto(), 
                                                      item.getEntidad().getEmail(), 
                                                      item.getActivo());
                            if (!password.isEmpty()) authentikService.setPassword(pk, password.getValue());
                        }
                    }

                    // Handle Role Changes (Add / Remove)
                    if (pk != null) {
                        // Collect selected roles from checkboxes
                        Set<String> selectedRoles = new java.util.HashSet<>();
                        roleCheckboxMap.forEach((k, v) -> {
                            if (v.getValue()) selectedRoles.add(k);
                        });
                        
                        List<String> currentRoles = authentikService.getUserGroupNames(pk);
                        
                        // Add new roles
                        for (String role : selectedRoles) {
                            if (!currentRoles.contains(role)) {
                                Map<String, Object> g = groups.stream().filter(gr -> gr.get("name").equals(role)).findFirst().orElse(null);
                                if (g != null) {
                                     String gPk = String.valueOf(g.get("pk"));
                                     authentikService.addUserToGroup(pk, gPk);
                                }
                            }
                        }
                        
                        // Remove unselected roles
                        for (String oldRole : currentRoles) {
                            if (!selectedRoles.contains(oldRole)) {
                                Map<String, Object> g = groups.stream().filter(gr -> gr.get("name").equals(oldRole)).findFirst().orElse(null);
                                if (g != null) {
                                    String gPk = String.valueOf(g.get("pk"));
                                    authentikService.removeUserFromGroup(pk, gPk);
                                }
                            }
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

    private String getRolePermissions(String roleName) {
        if (roleName == null) return "-";
        switch (roleName.toUpperCase()) {
            case "ADMINS":
            case "ADMIN": return "Acceso Total (Admin)";
            case "CAJEROS":
            case "CAJERO": return "PDV, Ventas, Clientes";
            case "CONTADORES":
            case "CONTADOR": return "Contabilidad, Reportes";
            case "INVENTARIO": return "Productos, Stock, Compras";
            case "USER": return "Acceso Básico";
            default: return "Sin Acceso Definido (Rol Personalizado)";
        }
    }

    private boolean isSystemRole(String roleName) {
        if (roleName == null) return false;
        String upper = roleName.toUpperCase();
        return upper.equals("ADMINS") || upper.equals("ADMIN"); 
    }
}
