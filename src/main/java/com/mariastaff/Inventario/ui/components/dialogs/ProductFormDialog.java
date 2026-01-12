package com.mariastaff.Inventario.ui.components.dialogs;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.entity.InvCategoria;
import com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.backend.service.CatalogoService;
import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.components.base.TailwindToggle;
import com.mariastaff.Inventario.ui.components.base.TailwindDatePicker;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class ProductFormDialog extends TailwindModal {

    private final ProductoService service;
    private final CatalogoService catalogoService;
    private final AlmacenService almacenService;
    
    private final Binder<InvProducto> binder = new Binder<>(InvProducto.class);
    private InvProducto currentProduct;
    private Runnable onSaveCallback;

    // UI Fields
    private TextField nombre;
    private TextField codigo;
    private TextField descripcion;
    private ComboBox<InvCategoria> categoria;
    private ComboBox<InvUnidadMedida> unidadMedida;
    private TailwindToggle activo;

    // New Product Fields
    private ComboBox<InvUbicacion> ubicacion;
    private com.vaadin.flow.component.textfield.BigDecimalField cantidadInicial;
    private TextField lote;
    private TailwindDatePicker fechaCaducidad;
    private TextArea observacionesLote;

    // Edit Product Fields
    private Grid<InvLote> lotesGrid;

    public ProductFormDialog(ProductoService service, CatalogoService catalogoService, AlmacenService almacenService) {
        super(com.vaadin.flow.component.UI.getCurrent().getTranslation("field.product"));
        this.service = service;
        this.catalogoService = catalogoService;
        this.almacenService = almacenService;
        
        initFields();
    }

    public void setOnSave(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    private void initFields() {
        nombre = new TextField(getTranslation("view.products.grid.name"));
        nombre.addClassName("w-full");
        
        codigo = new TextField(getTranslation("view.products.grid.code"));
        codigo.addClassName("w-full");
        
        descripcion = new TextField(getTranslation("field.description", "Descripción"));
        descripcion.addClassName("w-full");
        
        categoria = new ComboBox<>(getTranslation("view.products.grid.category"));
        categoria.setItems(catalogoService.findCategoriasActivas());
        categoria.setItemLabelGenerator(InvCategoria::getNombre);
        categoria.addClassName("w-full");
        categoria.setAllowCustomValue(true);
        categoria.addCustomValueSetListener(e -> {
             InvCategoria newCat = new InvCategoria();
             newCat.setNombre(e.getDetail());
             newCat.setActivo(true);
             newCat.setDescripcion("Creada desde Productos");
             try {
                InvCategoria saved = catalogoService.saveCategoria(newCat);
                categoria.setItems(catalogoService.findAllCategorias());
                categoria.setValue(saved);
                TailwindNotification.show(getTranslation("msg.category.added", "Nueva categoría añadida"), TailwindNotification.Type.SUCCESS);
             } catch (Exception ex) {
                TailwindNotification.show(getTranslation("msg.error.save"), TailwindNotification.Type.ERROR);
             }
        });

        unidadMedida = new ComboBox<>(getTranslation("view.products.grid.uom"));
        unidadMedida.setItems(catalogoService.findAllUnidadesMedida());
        unidadMedida.setItemLabelGenerator(InvUnidadMedida::getNombre);
        unidadMedida.addClassName("w-full");
        unidadMedida.setAllowCustomValue(true);
        unidadMedida.addCustomValueSetListener(e -> {
             String val = e.getDetail();
             InvUnidadMedida newUom = new InvUnidadMedida();
             newUom.setNombre(val);
             newUom.setAbreviatura(val.length() > 5 ? val.substring(0, 5).toUpperCase() : val.toUpperCase());
             newUom.setActivo(true);
             try {
                 InvUnidadMedida saved = catalogoService.saveUnidadMedida(newUom);
                 unidadMedida.setItems(catalogoService.findAllUnidadesMedida());
                 unidadMedida.setValue(saved);
                 TailwindNotification.show(getTranslation("msg.uom.added", "Nueva unidad añadida"), TailwindNotification.Type.SUCCESS);
             } catch (Exception ex) {
                 TailwindNotification.show(getTranslation("msg.error.save"), TailwindNotification.Type.ERROR);
             }
        });

        activo = new TailwindToggle(getTranslation("field.active"));
        
        ubicacion = new ComboBox<>(getTranslation("field.location.initial", "Ubicación (Inicial)"));
        ubicacion.setItems(almacenService.findAllUbicaciones());
        ubicacion.setItemLabelGenerator(u -> u.getCodigo() + " - " + u.getDescripcion());
        ubicacion.addClassName("w-full");
        
        cantidadInicial = new com.vaadin.flow.component.textfield.BigDecimalField(getTranslation("field.stock.initial", "Cantidad Inicial"));
        cantidadInicial.addClassName("w-full");

        lote = new TextField(getTranslation("field.batch.initial", "Lote (Inicial)"));
        lote.addClassName("w-full");

        fechaCaducidad = new TailwindDatePicker(getTranslation("field.batch.expiry"));
        fechaCaducidad.addClassName("w-full");

        observacionesLote = new TextArea(getTranslation("field.batch.observations"));
        observacionesLote.addClassName("w-full");

        lotesGrid = new Grid<>(InvLote.class, false);
        lotesGrid.addClassNames("bg-bg-surface", "rounded-lg", "shadow", "border", "border-gray-200");
        lotesGrid.setHeight("200px");
        lotesGrid.addColumn(l -> l.getProductoVariante() != null ? l.getProductoVariante().getNombreVariante() : "-")
                 .setHeader(getTranslation("field.variant")).setAutoWidth(true);
        lotesGrid.addColumn(InvLote::getCodigoLote).setHeader(getTranslation("field.batch.code")).setAutoWidth(true);
        lotesGrid.addColumn(l -> l.getFechaCaducidad() != null ? l.getFechaCaducidad().toLocalDate().toString() : "-")
                 .setHeader(getTranslation("field.batch.expiry")).setAutoWidth(true);
        lotesGrid.addComponentColumn(this::createEditLoteButton).setHeader(getTranslation("action.edit", "Editar")).setAutoWidth(true);

        binder.forField(nombre).asRequired(getTranslation("msg.required.name", "El nombre es obligatorio")).bind(InvProducto::getNombre, InvProducto::setNombre);
        binder.forField(codigo).bind(InvProducto::getCodigoInterno, InvProducto::setCodigoInterno);
        binder.forField(categoria).asRequired(getTranslation("msg.required.category", "La categoría es obligatoria")).bind(InvProducto::getCategoria, InvProducto::setCategoria);
        binder.forField(unidadMedida).asRequired(getTranslation("msg.required.uom", "La unidad es obligatoria")).bind(InvProducto::getUnidadMedida, InvProducto::setUnidadMedida);
        binder.forField(descripcion).bind(InvProducto::getDescripcion, InvProducto::setDescripcion);
        binder.forField(activo).bind(InvProducto::getActivo, InvProducto::setActivo);
    }
    
    private Button createEditLoteButton(InvLote loteItem) {
        Button editLoteBtn = new Button(VaadinIcon.EDIT.create());
        editLoteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editLoteBtn.addClassNames("text-primary", "hover:text-blue-700");
        editLoteBtn.addClickListener(e -> openEditLoteDialog(loteItem));
        return editLoteBtn;
    }

    private void openEditLoteDialog(InvLote loteItem) {
        TailwindModal editLoteModal = new TailwindModal(getTranslation("action.edit.batch"));
        editLoteModal.setDialogMaxWidth("max-w-2xl");

        ComboBox<InvProductoVariante> variantField = new ComboBox<>(getTranslation("field.product_variant"));
        variantField.setItems(service.findVariantesByProducto(currentProduct));
        variantField.setItemLabelGenerator(InvProductoVariante::getNombreVariante);
        variantField.setValue(loteItem.getProductoVariante());
        variantField.addClassName("w-full");

        TextField codeField = new TextField(getTranslation("field.batch.code"));
        codeField.addClassName("w-full");
        codeField.setValue(loteItem.getCodigoLote() != null ? loteItem.getCodigoLote() : "");

        TailwindDatePicker expiryField = new TailwindDatePicker(getTranslation("field.batch.expiry"));
        expiryField.addClassName("w-full");
        if (loteItem.getFechaCaducidad() != null) {
            expiryField.setValue(loteItem.getFechaCaducidad().toLocalDate());
        }

        TextArea obsField = new TextArea(getTranslation("field.batch.observations"));
        obsField.addClassName("w-full");
        if (loteItem.getObservaciones() != null) obsField.setValue(loteItem.getObservaciones());

        FormLayout layout = new FormLayout(variantField, codeField, expiryField, obsField);
        layout.addClassName("w-full");
        layout.setColspan(obsField, 2);
        editLoteModal.addContent(layout);

        Button saveBtn = new Button(getTranslation("action.save"), event -> {
            if (variantField.getValue() == null) {
                TailwindNotification.show(getTranslation("msg.variant.required"), TailwindNotification.Type.ERROR);
                return;
            }
            if (codeField.isEmpty()) {
                TailwindNotification.show(getTranslation("msg.code.required"), TailwindNotification.Type.ERROR);
                return;
            }
            loteItem.setProductoVariante(variantField.getValue());
            loteItem.setCodigoLote(codeField.getValue());
            loteItem.setFechaCaducidad(expiryField.getValue() != null ? expiryField.getValue().atStartOfDay() : null);
            loteItem.setObservaciones(obsField.getValue());

            try {
                almacenService.saveLote(loteItem);
                refreshLotesGrid();
                TailwindNotification.show(getTranslation("msg.batch.updated"), TailwindNotification.Type.SUCCESS);
                editLoteModal.close();
            } catch (Exception ex) {
                TailwindNotification.show(getTranslation("msg.error.save"), TailwindNotification.Type.ERROR);
            }
        });
        saveBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        
        Button cancelBtn = new Button(getTranslation("action.cancel"), event -> editLoteModal.close());
        cancelBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
        
        editLoteModal.addFooterButton(cancelBtn);
        editLoteModal.addFooterButton(saveBtn);
        
        this.add(editLoteModal); 
        editLoteModal.open();
    }

    private void refreshLotesGrid() {
        if (currentProduct != null) {
            List<InvLote> productBatches = new ArrayList<>();
            List<InvProductoVariante> variantes = service.findVariantesByProducto(currentProduct);
            for (InvProductoVariante var : variantes) {
                productBatches.addAll(almacenService.findLotesByVariante(var));
            }
            lotesGrid.setItems(productBatches);
        }
    }

    public void setProduct(InvProducto product) {
        this.currentProduct = product;
        boolean isNew = product.getId() == null;
        
        binder.readBean(product);
        
        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                                      new FormLayout.ResponsiveStep("600px", 2));
                                      
        if (isNew) {
            categoria.setItems(catalogoService.findCategoriasActivas());
            activo.setValue(true);
            formLayout.add(nombre, codigo, categoria, unidadMedida, ubicacion, cantidadInicial, lote, fechaCaducidad, descripcion, observacionesLote, activo);
            formLayout.setColspan(descripcion, 2);
            formLayout.setColspan(observacionesLote, 2);
        } else {
            categoria.setItems(catalogoService.findAllCategorias());
            refreshLotesGrid();
            formLayout.add(nombre, codigo, categoria, unidadMedida, descripcion, lotesGrid, activo);
            formLayout.setColspan(descripcion, 2);
            formLayout.setColspan(lotesGrid, 2);
        }
        formLayout.setColspan(nombre, 2);
        
        this.addContent(formLayout);

        Button saveButton = new Button(getTranslation("action.save"), e -> save(isNew));
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button(getTranslation("action.cancel"), e -> this.close());
        cancelButton.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
        
        this.addFooterButton(cancelButton);
        this.addFooterButton(saveButton);
    }

    private void save(boolean isNew) {
        try {
            binder.writeBean(currentProduct);
            
            if (isNew) {
                service.createProductWithInitialBatch(
                    currentProduct,
                    ubicacion.getValue(),
                    cantidadInicial.getValue(),
                    lote.getValue(),
                    fechaCaducidad.getValue() != null ? fechaCaducidad.getValue().atStartOfDay() : null,
                    observacionesLote.getValue()
                );
            } else {
                service.save(currentProduct);
            }
            
            TailwindNotification.show(isNew ? getTranslation("msg.product.created", "Producto creado") : getTranslation("msg.product.updated", "Producto actualizado"), TailwindNotification.Type.SUCCESS);
            if (onSaveCallback != null) onSaveCallback.run();
            this.close();
            
        } catch (ValidationException ex) {
            TailwindNotification.show(getTranslation("msg.error.validation", "Revise los campos requeridos"), TailwindNotification.Type.ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            TailwindNotification.show(getTranslation("msg.error.internal", "Error interno"), TailwindNotification.Type.ERROR);
        }
    }
}
