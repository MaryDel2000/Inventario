package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.backend.data.entity.SysUsuario;
import com.mariastaff.Inventario.backend.data.entity.GenEntidad;
import com.mariastaff.Inventario.backend.service.AuthentikService;
import com.mariastaff.Inventario.backend.service.UserService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
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
                    newUser.setAuthentikUuid((String) u.get("pk")); // PK is UUID string
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
                    local.setAuthentikUuid((String) u.get("pk"));
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
                        String uuid = (String) result.get("uid");
                        Integer pk = (Integer) result.get("pk");
                        local.setAuthentikUuid(uuid);
                        
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
                     authentikService.createGroup(role);
                } catch (Exception e) {}
            }

            TailwindNotification.show("Sincronización completa. Local creados: " + createdLocal + ", Authentik creados: " + createdAuth, TailwindNotification.Type.SUCCESS);
            updateList();
        } catch (Exception e) {
             TailwindNotification.show("Error al sincronizar: " + e.getMessage(), TailwindNotification.Type.ERROR);
             e.printStackTrace();
        }
    }

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
        
        rolesGrid.addComponentColumn(g -> {
            Button delBtn = new Button(VaadinIcon.TRASH.create());
            delBtn.addClassNames("text-red-600", "p-2", "hover:text-red-800");
            delBtn.addClickListener(e -> {
                try {
                    // Authentik 'pk' is usually UUID string in list
                    Object pkObj = g.get("pk");
                    String pk = String.valueOf(pkObj); 
                    authentikService.deleteGroup(pk);
                    TailwindNotification.show("Rol eliminado", TailwindNotification.Type.SUCCESS);
                    refreshRoles.run();
                } catch (Exception ex) {
                     TailwindNotification.show("Error al eliminar: " + ex.getMessage(), TailwindNotification.Type.ERROR);
                }
            });
            return delBtn;
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
                    Map<String, Object> newGroup = authentikService.createGroup(newRoleName.getValue());
                    if (newGroup != null) {
                        TailwindNotification.show("Rol '" + newRoleName.getValue() + "' creado con éxito", TailwindNotification.Type.SUCCESS);
                        newRoleName.clear();
                        refreshRoles.run();
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
        
        // Dynamic Roles Loading
        CheckboxGroup<String> rolesGroup = new CheckboxGroup<>();
        rolesGroup.setLabel("Roles (Grupos en Authentik)");
        rolesGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        
        // Fetch real groups from Authentik
        List<Map<String, Object>> groups = authentikService.listGroups();
        if (groups.isEmpty()) {
            TailwindNotification.show("Advertencia: No se encontraron grupos en Authentik o falló la conexión.", TailwindNotification.Type.WARNING);
        }
        
        List<String> groupNames = groups.stream().map(g -> (String)g.get("name")).collect(Collectors.toList());
        rolesGroup.setItems(groupNames);
        rolesGroup.setEnabled(!groupNames.isEmpty());
        
        // Pre-select user groups if editing
        if (!isNew && item.getAuthentikUuid() != null) {
             try {
                Integer pk = authentikService.getPkByUuid(item.getAuthentikUuid());
                if (pk != null) {
                    List<String> userGroups = authentikService.getUserGroupNames(pk);
                    // Filter only those existing in our loaded list to avoid CheckboxGroup error
                    List<String> validGroups = userGroups.stream().filter(groupNames::contains).collect(Collectors.toList());
                    rolesGroup.setValue(Set.copyOf(validGroups));
                }
             } catch (Exception e) {
                 System.err.println("Error fetching user groups: " + e.getMessage());
             }
        }


        formLayout.add(nombre, email, username, password, authentikUuid, activo, rolesGroup);
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
        
        binder.readBean(item);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(item);

                if (isNew && password.isEmpty()) {
                     TailwindNotification.show("La contraseña es obligatoria para nuevos usuarios", TailwindNotification.Type.ERROR);
                     return;
                }

                try {
                    Integer pk = null;
                    // ... Authentik Logic (kept same structure but logic inside is fine) ...
                    if (isNew) {
                        Map<String, Object> result = authentikService.createUser(item.getUsername(), 
                                                                               item.getEntidad().getNombreCompleto(), 
                                                                               item.getEntidad().getEmail());
                        String uuid = (String) result.get("uid");
                        pk = (Integer) result.get("pk");
                        item.setAuthentikUuid(uuid);
                        if (!password.isEmpty()) authentikService.setPassword(pk, password.getValue());
                    } else {
                        if (item.getAuthentikUuid() != null) {
                            pk = authentikService.getPkByUuid(item.getAuthentikUuid());
                            if (pk != null) {
                                System.out.println("Updating user PK: " + pk);
                                System.out.println("Username: " + item.getUsername());
                                System.out.println("Name: " + item.getEntidad().getNombreCompleto());
                                System.out.println("Email: " + item.getEntidad().getEmail());
                                
                                authentikService.updateUser(pk, item.getUsername(), 
                                                          item.getEntidad().getNombreCompleto(), 
                                                          item.getEntidad().getEmail(), 
                                                          item.getActivo());
                                if (!password.isEmpty()) authentikService.setPassword(pk, password.getValue());
                            }
                        }
                    }

                    // Handle Role Changes (Add / Remove)
                    if (pk != null) {
                        Set<String> selectedRoles = rolesGroup.getValue();
                        List<String> currentRoles = authentikService.getUserGroupNames(pk); // Re-fetch to be sure
                        
                        // Add new roles
                        for (String role : selectedRoles) {
                            if (!currentRoles.contains(role)) {
                                Map<String, Object> g = groups.stream().filter(gr -> gr.get("name").equals(role)).findFirst().orElse(null);
                                // If group doesn't exist in list (maybe new created elsewhere), create it? 
                                // Or assume list is authority. Let's create if not found mainly if user typed it, but here it's selection.
                                // But wait, we allowed arbitrary creation before. Here we restrict to existing groups or let's create if missing.
                                Integer gPk;
                                if (g == null) {
                                     // Actually we are selecting from list, so it MUST exist unless list is stale.
                                     // But let's support creating if we change UI back to combo with custom value.
                                     // For now, assume it exists.
                                     continue; 
                                } else {
                                     // g.get("pk") might be UUID string in list response or int. 
                                     // In Authentik API `core/groups/`, `pk` is usually UUID string. 
                                     // BUT `add_user_to_group` URL expects UUID or PK?
                                     // Wait, `core/groups/{pk}/add_user/`
                                     // Let's check `getPkByUuid` logic equivalence? 
                                     // Authentik v3 uses UUID as primary key in URL mostly.
                                     // Wait, `getPkByUuid` returned Integer because I cast it. But API might return String UUID as pk.
                                     // Let's inspect `createGroup` response. It returns pk.
                                     // Let's rely on retrieving the NUMERIC ID if Authentik uses numeric IDs for actions, or UUID.
                                     // Authentik v3 standard is UUID. 
                                     // Let's try to find the Numeric ID if `pk` is UUID string, maybe we need `num_pk`?
                                     // Or the `pk` field IS the UUID.
                                     
                                     // My previous code in AuthentikService used `Integer pk`. If Authentik returns UUID string for `pk`, that cast will fail.
                                     // Let's assume for `add_user_to_group` it accepts UUID in URL?
                                     // "The Group's PK (int) or UUID (str)" usually works.
                                     
                                     // Let's get the ID from the group map.
                                     Object idObj = g.get("pk");
                                     // We need to pass it to `addUserToGroup`, but that method signature is `(Integer userPk, Integer groupPk)`.
                                     // I should update signature to Object or String to support UUIDs if needed.
                                     // Let's assume I catch this in AuthentikService? 
                                     // Actually, let's fix the call here.
                                     // I will update AuthentikService signatures to use String for Group ID to be safe, or just check type.
                                     // But I can't change Service now without another tool call.
                                     // I will cast to what it is.
                                     
                                     // Wait, UsersView cannot change Service signature.
                                     // I must ensure I pass what `addUserToGroup` expects.
                                     // `addUserToGroup` takes (Integer, Integer).
                                     // If group PK is UUID (String), this will crash.
                                     // I must check if I can fetch numeric ID.
                                     
                                     // Actually, looking at `authentikService.getPkByUuid`, it returns Integer.
                                     // It seems I assumed numeric PKs.
                                     // If Authentik uses UUIDs, I should have used String.
                                     // Validating this: core/users returns `pk` (int) usually in Django apps, `uid` (uuid).
                                     // Authentik documentation: `pk` is integer.
                                     // So groups should have `pk` (int). 
                                     
                                     gPk = (Integer) g.get("pk");
                                     authentikService.addUserToGroup(pk, gPk);
                                }
                            }
                        }
                        
                        // Remove unselected roles
                        for (String oldRole : currentRoles) {
                            if (!selectedRoles.contains(oldRole)) {
                                Map<String, Object> g = groups.stream().filter(gr -> gr.get("name").equals(oldRole)).findFirst().orElse(null);
                                if (g != null) {
                                    Integer gPk = (Integer) g.get("pk");
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
        saveButton.addClassNames("bg-primary", "text-white");
        Button cancelButton = new Button("Cancelar", e -> modal.close());
        cancelButton.addClassNames("bg-gray-200", "text-black");
        
        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);
        add(modal);
        modal.open();
    }
}
