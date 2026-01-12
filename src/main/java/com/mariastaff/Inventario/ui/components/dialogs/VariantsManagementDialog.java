package com.mariastaff.Inventario.ui.components.dialogs;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.components.base.TailwindToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.springframework.dao.DataIntegrityViolationException;

public class VariantsManagementDialog extends TailwindModal {

    private final ProductoService service;
    private final Grid<InvProductoVariante> grid;
    private final Runnable onUpdateCallback;

    public VariantsManagementDialog(ProductoService service, Runnable onUpdateCallback) {
        super(com.vaadin.flow.component.UI.getCurrent().getTranslation("dialog.variants.title"));
        this.service = service;
        this.onUpdateCallback = onUpdateCallback;
        this.setDialogMaxWidth("max-w-6xl");

        this.grid = new Grid<>(InvProductoVariante.class, false);
        configureGrid();

        Button addBtn = new Button(getTranslation("action.new.variant"), VaadinIcon.PLUS.create());
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
        
        grid.addColumn(v -> v.getProducto() != null ? v.getProducto().getNombre() : "-").setHeader(getTranslation("field.product")).setAutoWidth(true);
        grid.addColumn(InvProductoVariante::getNombreVariante).setHeader(getTranslation("field.variant")).setAutoWidth(true);
        grid.addColumn(InvProductoVariante::getCodigoBarras).setHeader(getTranslation("field.barcode")).setAutoWidth(true);
        grid.addColumn(InvProductoVariante::getCodigoInternoVariante).setHeader(getTranslation("field.internal_code")).setAutoWidth(true);
        
        grid.addColumn(new ComponentRenderer<>(v -> {
            TailwindToggle toggle = new TailwindToggle("");
            toggle.getStyle().set("margin-top", "0");
            toggle.setValue(Boolean.TRUE.equals(v.getActivo()));
            
            toggle.addValueChangeListener(event -> {
                v.setActivo(event.getValue());
                try {
                    if (Boolean.TRUE.equals(event.getValue())) {
                         InvProducto parent = v.getProducto();
                         if (parent != null && !Boolean.TRUE.equals(parent.getActivo())) {
                             parent.setActivo(true);
                             service.save(parent);
                             TailwindNotification.show(getTranslation("msg.activated.auto"), TailwindNotification.Type.SUCCESS);
                             if (onUpdateCallback != null) onUpdateCallback.run();
                         }
                    }
                    service.saveVariante(v);
                    TailwindNotification.show(getTranslation("msg.variant.updated"), TailwindNotification.Type.SUCCESS);
                } catch (Exception e) {
                    toggle.setValue(event.getOldValue());
                    TailwindNotification.show(getTranslation("msg.error.save"), TailwindNotification.Type.ERROR);
                }
            });
            return toggle;
        })).setHeader(getTranslation("field.active")).setAutoWidth(true);

        grid.addComponentColumn(v -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            editBtn.addClickListener(e -> openEditDialog(v));

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            deleteBtn.addClickListener(event -> deleteVariant(v));
            
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);
    }

    private void refreshGrid() {
        grid.setItems(service.findAllVariantes());
    }

    private void openAddDialog() {
        TailwindModal addModal = new TailwindModal(getTranslation("action.new.variant"));
        
        ComboBox<InvProducto> productField = new ComboBox<>(getTranslation("field.product"));
        productField.setItems(service.findAll());
        productField.setItemLabelGenerator(InvProducto::getNombre);
        productField.addClassName("w-full");

        TextField nameField = new TextField(getTranslation("field.variant"));
        nameField.addClassName("w-full");
        
        TextField barcodeField = new TextField(getTranslation("field.barcode"));
        barcodeField.addClassName("w-full");
        
        TextField internalCodeField = new TextField(getTranslation("field.internal_code"));
        internalCodeField.addClassName("w-full");
        
        FormLayout layout = new FormLayout(productField, nameField, barcodeField, internalCodeField);
        layout.addClassName("w-full");
        addModal.addContent(layout);
        
        Button saveAddBtn = new Button(getTranslation("action.save"), event -> {
            if (productField.getValue() == null) {
                TailwindNotification.show(getTranslation("msg.product.required"), TailwindNotification.Type.ERROR);
                return;
            }
            InvProductoVariante newVar = new InvProductoVariante();
            newVar.setProducto(productField.getValue());
            newVar.setNombreVariante(nameField.getValue());
            newVar.setCodigoBarras(barcodeField.getValue());
            newVar.setCodigoInternoVariante(internalCodeField.getValue());
            newVar.setActivo(true);
            
            try {
                service.saveVariante(newVar);
                refreshGrid();
                TailwindNotification.show(getTranslation("msg.variant.created"), TailwindNotification.Type.SUCCESS);
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

    private void openEditDialog(InvProductoVariante v) {
         TailwindModal editModal = new TailwindModal(getTranslation("action.edit.variant"));
         
         ComboBox<InvProducto> productField = new ComboBox<>(getTranslation("field.product"));
         productField.setItems(service.findAll());
         productField.setItemLabelGenerator(InvProducto::getNombre);
         productField.setValue(v.getProducto());
         productField.addClassName("w-full");

         TextField nameField = new TextField(getTranslation("field.variant"));
         nameField.addClassName("w-full");
         nameField.setValue(v.getNombreVariante() != null ? v.getNombreVariante() : "");
         
         TextField barcodeField = new TextField(getTranslation("field.barcode"));
         barcodeField.addClassName("w-full");
         if (v.getCodigoBarras() != null) barcodeField.setValue(v.getCodigoBarras());

         TextField internalCodeField = new TextField(getTranslation("field.internal_code"));
         internalCodeField.addClassName("w-full");
         if (v.getCodigoInternoVariante() != null) internalCodeField.setValue(v.getCodigoInternoVariante());
         
         FormLayout layout = new FormLayout(productField, nameField, barcodeField, internalCodeField);
         layout.addClassName("w-full");
         editModal.addContent(layout);
         
         Button saveEditBtn = new Button(getTranslation("action.save"), event -> {
             if (productField.getValue() == null) {
                 TailwindNotification.show(getTranslation("msg.product.required"), TailwindNotification.Type.ERROR);
                 return;
             }
             v.setProducto(productField.getValue());
             v.setNombreVariante(nameField.getValue());
             v.setCodigoBarras(barcodeField.getValue());
             v.setCodigoInternoVariante(internalCodeField.getValue());
             try {
                 service.saveVariante(v);
                 refreshGrid();
                 TailwindNotification.show(getTranslation("msg.variant.updated"), TailwindNotification.Type.SUCCESS);
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

    private void deleteVariant(InvProductoVariante v) {
        try {
            service.deleteVariante(v);
            refreshGrid();
            TailwindNotification.show(getTranslation("msg.variant.deleted"), TailwindNotification.Type.SUCCESS);
        } catch (DataIntegrityViolationException e) {
             TailwindNotification.show(getTranslation("msg.error.in_use"), TailwindNotification.Type.ERROR);
        } catch (Exception e) {
             TailwindNotification.show(getTranslation("msg.error.delete"), TailwindNotification.Type.ERROR);
        }
    }
}
