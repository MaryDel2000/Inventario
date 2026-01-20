package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.backend.service.BackupService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

@PageTitle("Respaldos | Configuración")
@Route(value = "settings/backups", layout = MainLayout.class)
@RolesAllowed("ROLE_ADMIN")
public class BackupView extends VerticalLayout {

    private final BackupService backupService;
    private final Grid<File> grid = new Grid<>();

    public BackupView(BackupService backupService) {
        this.backupService = backupService;

        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        add(new AppLabel(getTranslation("view.settings.backups.title")));
        
        configureGrid();
        
        Button createBtn = new Button(getTranslation("action.create_backup"), VaadinIcon.DATABASE.create());
        createBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        createBtn.addClickListener(e -> createBackup());
        
        add(createBtn, grid, createClearDbButton());
        updateList();
    }

    private Button createClearDbButton() {
        Button clearBtn = new Button(getTranslation("action.clear_db"), VaadinIcon.TRASH.create());
        clearBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        clearBtn.addClassNames("mt-6", "bg-red-600", "text-white", "font-bold", "py-2", "px-4", "rounded-lg", "shadow");
        clearBtn.addClickListener(e -> confirmClearDatabase());
        return clearBtn;
    }

    private void confirmClearDatabase() {
        TailwindModal modal = new TailwindModal(getTranslation("msg.clear_db.confirm.title"));
        
        com.vaadin.flow.component.html.Paragraph warning = new com.vaadin.flow.component.html.Paragraph(
            getTranslation("msg.clear_db.confirm.text")
        );
        warning.addClassNames("text-red-600", "font-bold", "text-lg");
        
        modal.addContent(warning);
        
        Button confirmBtn = new Button(getTranslation("action.clear_db.confirm"), e -> {
            try {
                backupService.clearDatabase();
                TailwindNotification.show(getTranslation("msg.clear_db.success"), TailwindNotification.Type.SUCCESS);
                modal.close();
            } catch (Exception ex) {
                TailwindNotification.show(getTranslation("msg.clear_db.error") + ex.getMessage(), TailwindNotification.Type.ERROR);
                ex.printStackTrace();
            }
        });
        confirmBtn.addClassNames("bg-red-700", "text-white", "font-bold");
        
        Button cancelBtn = new Button(getTranslation("action.cancel"), e -> modal.close());
        
        modal.addFooterButton(cancelBtn);
        modal.addFooterButton(confirmBtn);
        
        add(modal);
        modal.open();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        
        grid.addColumn(File::getName).setHeader(getTranslation("grid.header.file"));
        grid.addColumn(f -> new Date(f.lastModified())).setHeader(getTranslation("grid.header.date"));
        grid.addColumn(f -> formatSize(f.length())).setHeader(getTranslation("grid.header.size"));
        
        grid.addComponentColumn(file -> {
            HorizontalLayout actions = new HorizontalLayout();
            
            Button restoreBtn = new Button(getTranslation("action.restore"), VaadinIcon.ROTATE_RIGHT.create());
            restoreBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            restoreBtn.addClassNames("bg-red-100", "text-red-700", "hover:bg-red-200");
            restoreBtn.addClickListener(e -> confirmRestore(file));
            
            actions.add(createDownloadLink(file), restoreBtn);
            return actions;
        }).setHeader(getTranslation("grid.header.actions"));
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }
    
    private Anchor createDownloadLink(File file) {
        StreamResource resource = new StreamResource(file.getName(), () -> {
            try {
                return new FileInputStream(file);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
        
        Anchor anchor = new Anchor(resource, "");
        Button btn = new Button(VaadinIcon.DOWNLOAD.create());
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        anchor.add(btn);
        anchor.getElement().setAttribute("download", true);
        return anchor;
    }

    private void updateList() {
        grid.setItems(backupService.listBackups());
    }

    private void createBackup() {
        try {
            backupService.backupDatabase();
            TailwindNotification.show(getTranslation("msg.backup.success"), TailwindNotification.Type.SUCCESS);
            updateList();
        } catch (Exception e) {
            TailwindNotification.show(getTranslation("msg.backup.error") + e.getMessage(), TailwindNotification.Type.ERROR);
            e.printStackTrace();
        }
    }

    private void confirmRestore(File file) {
        TailwindModal modal = new TailwindModal(getTranslation("msg.restore.confirm.title"));
        
        com.vaadin.flow.component.html.Paragraph warning = new com.vaadin.flow.component.html.Paragraph(
            getTranslation("msg.restore.confirm.text") + " (" + file.getName() + ")"
        );
        warning.addClassName("text-red-600");
        
        modal.addContent(warning);
        
        Button confirmBtn = new Button(getTranslation("action.restore.confirm"), e -> {
            try {
                backupService.restoreDatabase(file);
                TailwindNotification.show(getTranslation("msg.restore.success"), TailwindNotification.Type.SUCCESS);
                modal.close();
            } catch (Exception ex) {
                TailwindNotification.show(getTranslation("msg.restore.error") + ex.getMessage(), TailwindNotification.Type.ERROR);
                ex.printStackTrace();
            }
        });
        confirmBtn.addClassNames("bg-red-600", "text-white", "font-bold");
        
        Button cancelBtn = new Button(getTranslation("action.cancel"), e -> modal.close());
        
        modal.addFooterButton(cancelBtn);
        modal.addFooterButton(confirmBtn);
        
        add(modal);
        modal.open();
    }

    private String formatSize(long size) {
        if (size < 1024) return size + " B";
        int z = (63 - Long.numberOfLeadingZeros(size)) / 10;
        return String.format("%.1f %sB", (double)size / (1L << (z*10)), " KMGTPE".charAt(z));
    }
}
