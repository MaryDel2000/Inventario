package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.util.Arrays;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;

@PageTitle("Migraciones DB | Inventario")
@Route(value = "flyway-migration", layout = MainLayout.class)
@PermitAll
public class FlywayMigrationView extends VerticalLayout {

    private final Flyway flyway;
    private final Grid<MigrationInfo> grid;

    public FlywayMigrationView(Flyway flyway) {
        this.flyway = flyway;
        
        addClassNames("p-6", "bg-bg-secondary", "h-full");
        
        add(new H2("Gestión de Migraciones de Base de Datos"));
        
        HorizontalLayout toolbar = new HorizontalLayout();
        Button migrateBtn = new Button("Ejecutar Migraciones", new Icon("lumo", "play"));
        migrateBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        migrateBtn.addClickListener(e -> runMigration());
        
        Button repairBtn = new Button("Reparar (Repair)", new Icon("lumo", "tools"));
        repairBtn.addClickListener(e -> runRepair());

        Button refreshBtn = new Button("Refrescar", new Icon("lumo", "reload"));
        refreshBtn.addClickListener(e -> refreshGrid());
        
        toolbar.add(migrateBtn, repairBtn, refreshBtn);
        add(toolbar);
        
        grid = new Grid<>();
        grid.addColumn(MigrationInfo::getVersion).setHeader("Versión").setAutoWidth(true);
        grid.addColumn(MigrationInfo::getDescription).setHeader("Descripción").setAutoWidth(true);
        grid.addColumn(MigrationInfo::getType).setHeader("Tipo");
        grid.addColumn(MigrationInfo::getInstalledOn).setHeader("Instalado En");
        grid.addColumn(MigrationInfo::getState).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(MigrationInfo::getExecutionTime).setHeader("Tiempo (ms)");
        
        add(grid);
        
        refreshGrid();
    }
    
    private void refreshGrid() {
        MigrationInfo[] info = flyway.info().all();
        grid.setItems(Arrays.asList(info));
    }
    
    private void runMigration() {
        try {
            flyway.migrate();
            Notification.show("Migración completada exitosamente.");
            refreshGrid();
        } catch (Exception e) {
            Notification.show("Error en migración: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }
    
    private void runRepair() {
        try {
            flyway.repair();
            Notification.show("Reparación completada.");
            refreshGrid();
        } catch (Exception e) {
            Notification.show("Error en reparación: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }
}
