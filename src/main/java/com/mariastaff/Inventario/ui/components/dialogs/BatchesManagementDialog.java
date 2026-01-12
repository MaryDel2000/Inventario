package com.mariastaff.Inventario.ui.components.dialogs;

import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.components.base.TailwindDatePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

public class BatchesManagementDialog extends TailwindModal {

    private final AlmacenService almacenService;
    private final ProductoService service;
    private final Grid<InvLote> grid;

    public BatchesManagementDialog(AlmacenService almacenService, ProductoService service) {
        super(com.vaadin.flow.component.UI.getCurrent().getTranslation("dialog.batches.title"));
        this.almacenService = almacenService;
        this.service = service;
        this.setDialogMaxWidth("max-w-6xl");

        this.grid = new Grid<>(InvLote.class, false);
        configureGrid();

        Button addBtn = new Button(getTranslation("action.new.batch"), VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "mb-4", "w-fit");
        addBtn.addClickListener(e -> openAddDialog());

        Div gridContainer = new Div(grid);
        gridContainer.addClassNames("w-full", "h-96", "overflow-hidden", "flex", "flex-col");
        grid.setHeightFull();

        VerticalLayout content = new VerticalLayout(addBtn, gridContainer);
        content.setPadding(false);
        content.setSpacing(true);
        content.addClassNames("w-full", "h-full");

        this.addContent(content);

        Button closeBtn = new Button(getTranslation("action.close"), e -> this.close());
        closeBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        this.addFooterButton(closeBtn);
        
        refreshGrid();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        
        grid.addColumn(l -> l.getProductoVariante() != null && l.getProductoVariante().getProducto() != null ? 
            l.getProductoVariante().getProducto().getNombre() + " - " + l.getProductoVariante().getNombreVariante() : "-").setHeader(getTranslation("field.product_variant")).setAutoWidth(true);
        grid.addColumn(InvLote::getCodigoLote).setHeader(getTranslation("field.batch.code")).setAutoWidth(true);
        grid.addColumn(l -> result(l.getFechaCaducidad())).setHeader(getTranslation("field.batch.expiry")).setAutoWidth(true);
        grid.addColumn(InvLote::getObservaciones).setHeader(getTranslation("field.batch.observations")).setAutoWidth(true);

        grid.addComponentColumn(lote -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            editBtn.addClickListener(e -> openEditDialog(lote));

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            deleteBtn.addClickListener(event -> deleteBatch(lote));
            
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);
    }
    
    private String result(LocalDateTime date) {
        return date != null ? date.toLocalDate().toString() : "-";
    }

    private void refreshGrid() {
        grid.setItems(almacenService.findAllLotes());
    }

    private void openAddDialog() {
        TailwindModal addModal = new TailwindModal(getTranslation("action.new.batch"));
        
        ComboBox<InvProductoVariante> variantField = new ComboBox<>(getTranslation("field.product_variant"));
        variantField.setItems(service.findAllVariantes());
        variantField.setItemLabelGenerator(v -> v.getProducto().getNombre() + " - " + v.getNombreVariante());
        variantField.addClassName("w-full");

        TextField codeField = new TextField(getTranslation("field.batch.code"));
        codeField.addClassName("w-full");
        
        TailwindDatePicker expiryField = new TailwindDatePicker(getTranslation("field.batch.expiry"));
        expiryField.addClassName("w-full");

        TextArea obsField = new TextArea(getTranslation("field.batch.observations"));
        obsField.addClassName("w-full");
        
        FormLayout layout = new FormLayout(variantField, codeField, expiryField, obsField);
        layout.addClassName("w-full");
        layout.setColspan(obsField, 2);
        addModal.addContent(layout);
        
        Button saveAddBtn = new Button(getTranslation("action.save"), event -> {
            if (variantField.getValue() == null) {
                TailwindNotification.show(getTranslation("msg.variant.required"), TailwindNotification.Type.ERROR);
                return;
            }
            if (codeField.isEmpty()) {
                TailwindNotification.show(getTranslation("msg.code.required"), TailwindNotification.Type.ERROR);
                return;
            }
            
            InvLote newLote = new InvLote();
            newLote.setProductoVariante(variantField.getValue());
            newLote.setCodigoLote(codeField.getValue());
            if (expiryField.getValue() != null) {
               newLote.setFechaCaducidad(expiryField.getValue().atStartOfDay());
            }
            newLote.setObservaciones(obsField.getValue());
            
            try {
                almacenService.saveLote(newLote);
                refreshGrid();
                TailwindNotification.show(getTranslation("msg.batch.created"), TailwindNotification.Type.SUCCESS);
                addModal.close();
            } catch (Exception ex) {
                TailwindNotification.show(getTranslation("msg.error.save"), TailwindNotification.Type.ERROR);
            }
        });
        saveAddBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        
        Button cancelAddBtn = new Button(getTranslation("action.cancel"), event -> addModal.close());
        cancelAddBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
        
        addModal.addFooterButton(cancelAddBtn);
        addModal.addFooterButton(saveAddBtn);
        this.add(addModal);
        addModal.open();
    }

    private void openEditDialog(InvLote lote) {
         TailwindModal editModal = new TailwindModal(getTranslation("action.edit.batch"));
         
         ComboBox<InvProductoVariante> variantField = new ComboBox<>(getTranslation("field.product_variant"));
         variantField.setItems(service.findAllVariantes());
         variantField.setItemLabelGenerator(v -> v.getProducto().getNombre() + " - " + v.getNombreVariante());
         variantField.setValue(lote.getProductoVariante());
         variantField.addClassName("w-full");

         TextField codeField = new TextField(getTranslation("field.batch.code"));
         codeField.addClassName("w-full");
         codeField.setValue(lote.getCodigoLote() != null ? lote.getCodigoLote() : "");
         
         TailwindDatePicker expiryField = new TailwindDatePicker(getTranslation("field.batch.expiry"));
         expiryField.addClassName("w-full");
         if (lote.getFechaCaducidad() != null) {
             expiryField.setValue(lote.getFechaCaducidad().toLocalDate());
         }

         TextArea obsField = new TextArea(getTranslation("field.batch.observations"));
         obsField.addClassName("w-full");
         if (lote.getObservaciones() != null) obsField.setValue(lote.getObservaciones());
         
         FormLayout layout = new FormLayout(variantField, codeField, expiryField, obsField);
         layout.addClassName("w-full");
         layout.setColspan(obsField, 2);
         editModal.addContent(layout);
         
         Button saveEditBtn = new Button(getTranslation("action.save"), event -> {
             if (variantField.getValue() == null) {
                 TailwindNotification.show(getTranslation("msg.variant.required"), TailwindNotification.Type.ERROR);
                 return;
             }
             if (codeField.isEmpty()) {
                 TailwindNotification.show(getTranslation("msg.code.required"), TailwindNotification.Type.ERROR);
                 return;
             }

             lote.setProductoVariante(variantField.getValue());
             lote.setCodigoLote(codeField.getValue());
             if (expiryField.getValue() != null) {
                lote.setFechaCaducidad(expiryField.getValue().atStartOfDay());
             } else {
                lote.setFechaCaducidad(null);
             }
             lote.setObservaciones(obsField.getValue());

             try {
                 almacenService.saveLote(lote);
                 refreshGrid();
                 TailwindNotification.show(getTranslation("msg.batch.updated"), TailwindNotification.Type.SUCCESS);
                 editModal.close();
             } catch (Exception ex) {
                 TailwindNotification.show(getTranslation("msg.error.save"), TailwindNotification.Type.ERROR);
             }
         });
         saveEditBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
         
         Button cancelEditBtn = new Button(getTranslation("action.cancel"), event -> editModal.close());
         cancelEditBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
         
         editModal.addFooterButton(cancelEditBtn);
         editModal.addFooterButton(saveEditBtn);
         this.add(editModal);
         editModal.open();
    }

    private void deleteBatch(InvLote lote) {
        try {
            almacenService.deleteLote(lote);
            refreshGrid();
            TailwindNotification.show(getTranslation("msg.batch.deleted"), TailwindNotification.Type.SUCCESS);
        } catch (DataIntegrityViolationException e) {
             TailwindNotification.show(getTranslation("msg.error.in_use"), TailwindNotification.Type.ERROR);
        } catch (Exception e) {
             TailwindNotification.show(getTranslation("msg.error.delete"), TailwindNotification.Type.ERROR);
        }
    }
}
