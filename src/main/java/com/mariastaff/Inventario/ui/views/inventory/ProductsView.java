package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.entity.InvCategoria;
import com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.backend.service.CatalogoService;
import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.components.base.TailwindToggle;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.mariastaff.Inventario.ui.components.base.TailwindDatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.button.ButtonVariant;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.annotation.security.PermitAll;

@PageTitle("Productos | Inventario")
@Route(value = "inventory/products", layout = MainLayout.class)
@PermitAll
public class ProductsView extends VerticalLayout {

    private final ProductoService service;
    private final CatalogoService catalogoService;
    private final AlmacenService almacenService;
    private final Grid<InvProducto> grid = new Grid<>(InvProducto.class);

    public ProductsView(ProductoService service, CatalogoService catalogoService, AlmacenService almacenService) {
        this.service = service;
        this.catalogoService = catalogoService;
        this.almacenService = almacenService;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        Button catBtn = new Button("Ver Categorías", VaadinIcon.TAGS.create());
        catBtn.addClassNames("bg-white", "text-primary", "border", "border-gray-200", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow-sm", "hover:shadow-md", "hover:bg-gray-50", "transition-all", "mr-2");
        catBtn.addClickListener(e -> openCategoriesDialog());

        Button uomBtn = new Button("Ver Unidades", VaadinIcon.SLIDERS.create());
        uomBtn.addClassNames("bg-white", "text-primary", "border", "border-gray-200", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow-sm", "hover:shadow-md", "hover:bg-gray-50", "transition-all", "mr-2");
        uomBtn.addClickListener(e -> openUOMDialog());

        Button varBtn = new Button("Ver Variantes", VaadinIcon.QRCODE.create());
        varBtn.addClassNames("bg-white", "text-primary", "border", "border-gray-200", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow-sm", "hover:shadow-md", "hover:bg-gray-50", "transition-all", "mr-2");
        varBtn.addClickListener(e -> openVariantsDialog());

        Button lotesBtn = new Button("Ver Lotes", VaadinIcon.BARCODE.create());
        lotesBtn.addClassNames("bg-white", "text-primary", "border", "border-gray-200", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow-sm", "hover:shadow-md", "hover:bg-gray-50", "transition-all", "mr-2");
        lotesBtn.addClickListener(e -> openBatchesDialog());

        Button addBtn = new Button("Nuevo Producto", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all"); // Tailwind styled button
        addBtn.addClickListener(e -> openProductDialog());

        HorizontalLayout buttons = new HorizontalLayout(catBtn, uomBtn, varBtn, lotesBtn, addBtn);
        // buttons.setSpacing(true); // Tailwind handles spacing via margin on catBtn, but container spacing is safer. 
        // Actually HorizontalLayout has spacing by default usually, but let's trust the classes.
        buttons.setAlignItems(Alignment.CENTER);
        
        HorizontalLayout header = new HorizontalLayout(new AppLabel("view.products.title"), buttons);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("nombre", "codigoInterno");
        
        grid.getColumnByKey("nombre").setHeader(getTranslation("view.products.grid.name"));
        grid.getColumnByKey("codigoInterno").setHeader(getTranslation("view.products.grid.code"));
        
        grid.addColumn(p -> p.getCategoria() != null ? p.getCategoria().getNombre() : "-").setHeader(getTranslation("view.products.grid.category"));
        grid.addColumn(p -> p.getUnidadMedida() != null ? p.getUnidadMedida().getNombre() : "-").setHeader(getTranslation("view.products.grid.uom"));
        grid.addColumn(p -> p.getActivo() ? getTranslation("common.yes") : getTranslation("common.no")).setHeader(getTranslation("view.products.grid.active"));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAll());
    }

    private void openProductDialog() {
        TailwindModal modal = new TailwindModal("Nuevo Producto");
        
        InvProducto product = new InvProducto();
        Binder<InvProducto> binder = new Binder<>(InvProducto.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full");
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                                      new FormLayout.ResponsiveStep("600px", 2));
        
        TextField nombre = new TextField("Nombre");
        nombre.addClassName("w-full");
        
        TextField codigo = new TextField("Código Interno");
        codigo.addClassName("w-full");
        
        TextField descripcion = new TextField("Descripción");
        descripcion.addClassName("w-full");
        
        ComboBox<InvCategoria> categoria = new ComboBox<>("Categoría");
        categoria.setItems(catalogoService.findCategoriasActivas());
        categoria.setItemLabelGenerator(InvCategoria::getNombre);
        categoria.setAllowCustomValue(true);
        categoria.addCustomValueSetListener(event -> {
            String customValue = event.getDetail();
            // Look for existing category (optional optimization, but good practice if user types exact existing name)
            // Here we assume if they typed it and it wasn't selected, they intend to create it or it didn't match.
            // Since we don't have a findByName on the service, we proceed to create.
            
            InvCategoria newCategory = new InvCategoria();
            newCategory.setNombre(customValue);
            newCategory.setActivo(true);
            newCategory.setDescripcion("Creada desde Productos"); 
            
            try {
                InvCategoria savedCategory = catalogoService.saveCategoria(newCategory);
                categoria.setItems(catalogoService.findAllCategorias());
                categoria.setValue(savedCategory);
                TailwindNotification.show("Nueva categoría añadida", TailwindNotification.Type.SUCCESS);
            } catch (Exception ex) {
                TailwindNotification.show("Error al guardar categoría", TailwindNotification.Type.ERROR);
            }
        });
        categoria.addClassName("w-full");
        
        ComboBox<InvUnidadMedida> unidadMedida = new ComboBox<>("Unidad de Medida");
        unidadMedida.setItems(catalogoService.findAllUnidadesMedida());
        unidadMedida.setItemLabelGenerator(InvUnidadMedida::getNombre);
        unidadMedida.setAllowCustomValue(true);
        unidadMedida.addCustomValueSetListener(event -> {
            String customValue = event.getDetail();
            
            InvUnidadMedida newUom = new InvUnidadMedida();
            newUom.setNombre(customValue);
            // Simple heuristic: use name as abbreviation initially
            newUom.setAbreviatura(customValue.length() > 5 ? customValue.substring(0, 5).toUpperCase() : customValue.toUpperCase());
            newUom.setActivo(true);
            
            try {
                InvUnidadMedida saved = catalogoService.saveUnidadMedida(newUom);
                unidadMedida.setItems(catalogoService.findAllUnidadesMedida());
                unidadMedida.setValue(saved);
                TailwindNotification.show("Nueva unidad añadida", TailwindNotification.Type.SUCCESS);
            } catch (Exception ex) {
                TailwindNotification.show("Error al guardar unidad", TailwindNotification.Type.ERROR);
            }
        });
        unidadMedida.addClassName("w-full");
        
        // New Fields
        ComboBox<InvUbicacion> ubicacion = new ComboBox<>("Ubicación (Inicial)");
        ubicacion.setItems(almacenService.findAllUbicaciones());
        ubicacion.setItemLabelGenerator(u -> u.getCodigo() + " - " + u.getDescripcion());
        ubicacion.addClassName("w-full");

        TextField lote = new TextField("Lote (Inicial)");
        lote.addClassName("w-full");

        TailwindDatePicker fechaCaducidad = new TailwindDatePicker("Fecha Caducidad Lote");
        fechaCaducidad.addClassName("w-full");

        TextArea observacionesLote = new TextArea("Observaciones Lote");
        observacionesLote.addClassName("w-full");
        formLayout.setColspan(observacionesLote, 2);

        TailwindToggle activo = new TailwindToggle("Activo");
        activo.setValue(true);

        // Add fields to layout
        formLayout.add(nombre, codigo, categoria, unidadMedida, ubicacion, lote, fechaCaducidad, descripcion, observacionesLote, activo);
        
        // Layout tweak: description full width
        formLayout.setColspan(descripcion, 2);
        formLayout.setColspan(nombre, 2);
        
        modal.addContent(formLayout);

        // Binding
        binder.forField(nombre).asRequired("El nombre es obligatorio").bind(InvProducto::getNombre, InvProducto::setNombre);
        binder.forField(codigo).bind(InvProducto::getCodigoInterno, InvProducto::setCodigoInterno);
        binder.forField(categoria).asRequired("La categoría es obligatoria").bind(InvProducto::getCategoria, InvProducto::setCategoria);
        binder.forField(unidadMedida).asRequired("La unidad es obligatoria").bind(InvProducto::getUnidadMedida, InvProducto::setUnidadMedida);
        binder.forField(descripcion).bind(InvProducto::getDescripcion, InvProducto::setDescripcion);
        binder.forField(activo).bind(InvProducto::getActivo, InvProducto::setActivo);

        Button saveButton = new Button("Guardar", e -> {
            try {
                binder.writeBean(product);
                
                // Save Product
                InvProducto savedProduct = service.save(product);
                
                // Logic to create initial Batch (Lote) if provided
                if (lote.getValue() != null && !lote.getValue().trim().isEmpty()) {
                    // Create a default variant to attach the batch to
                    InvProductoVariante defaultVariant = new InvProductoVariante();
                    defaultVariant.setProducto(savedProduct);
                    defaultVariant.setNombreVariante(savedProduct.getNombre()); 
                    defaultVariant.setCodigoInternoVariante(savedProduct.getCodigoInterno());
                    defaultVariant.setActivo(true);
                    
                    // Save Variant
                    InvProductoVariante savedVariant = service.saveVariante(defaultVariant);
                    
                    // Create Batch
                    InvLote newLote = new InvLote();
                    newLote.setProductoVariante(savedVariant);
                    newLote.setCodigoLote(lote.getValue());
                    if (fechaCaducidad.getValue() != null) {
                        newLote.setFechaCaducidad(fechaCaducidad.getValue().atStartOfDay());
                    }
                    newLote.setObservaciones(observacionesLote.getValue());
                    
                    // Save Batch
                    almacenService.saveLote(newLote);
                }
                
                TailwindNotification.show("Producto guardado correctamente", TailwindNotification.Type.SUCCESS);
                updateList();
                modal.close();
            } catch (ValidationException ex) {
                TailwindNotification.show("Por favor revise los campos requeridos", TailwindNotification.Type.ERROR);
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

    private void openCategoriesDialog() {
        TailwindModal modal = new TailwindModal("Listado de Categorías");
        modal.setDialogMaxWidth("max-w-5xl"); // Wider for grid

        Grid<InvCategoria> catGrid = new Grid<>(InvCategoria.class, false); // Disable auto columns
        catGrid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        
        catGrid.addColumn(InvCategoria::getNombre).setHeader("Nombre").setAutoWidth(true);
        catGrid.addColumn(InvCategoria::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        
        // Editable Active Toggle Column
        catGrid.addColumn(new ComponentRenderer<>(category -> {
            TailwindToggle toggle = new TailwindToggle("");
            toggle.getStyle().set("margin-top", "0");
            // Assuming TailwindToggle has setValue/getValue or similar if it extends Checkbox or CustomField
            // If it's a custom component wrapper, we might need to access inner.
            // Based on previous code: toggle.setValue(true); implies it works like Field.
            toggle.setValue(Boolean.TRUE.equals(category.getActivo()));
            
            toggle.addValueChangeListener(event -> {
                category.setActivo(event.getValue());
                try {
                    catalogoService.saveCategoria(category);
                    TailwindNotification.show("Estado actualizado", TailwindNotification.Type.SUCCESS);
                } catch (Exception e) {
                    toggle.setValue(event.getOldValue()); // Revert on error
                    TailwindNotification.show("Error al actualizar", TailwindNotification.Type.ERROR);
                }
            });
            return toggle;
        })).setHeader("Activo").setAutoWidth(true);

        // Delete Column
        // Actions Column
        catGrid.addComponentColumn(category -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            
            editBtn.addClickListener(e -> {
                 TailwindModal editModal = new TailwindModal("Editar Categoría");
                 TextField nameField = new TextField("Nombre");
                 nameField.addClassName("w-full");
                 nameField.setValue(category.getNombre());
                 
                 TextField descField = new TextField("Descripción");
                 descField.addClassName("w-full");
                 if (category.getDescripcion() != null) descField.setValue(category.getDescripcion());
                 
                 FormLayout layout = new FormLayout(nameField, descField);
                 layout.addClassName("w-full");
                 editModal.addContent(layout);
                 
                 Button saveEditBtn = new Button("Guardar", event -> {
                     category.setNombre(nameField.getValue());
                     category.setDescripcion(descField.getValue());
                     try {
                         catalogoService.saveCategoria(category);
                         catGrid.setItems(catalogoService.findAllCategorias());
                         TailwindNotification.show("Categoría actualizada", TailwindNotification.Type.SUCCESS);
                         editModal.close();
                     } catch (Exception ex) {
                         TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
                     }
                 });
                 saveEditBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
                 
                 Button cancelEditBtn = new Button("Cancelar", event -> editModal.close());
                 cancelEditBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
                 
                 editModal.addFooterButton(cancelEditBtn);
                 editModal.addFooterButton(saveEditBtn);
                 add(editModal);
                 editModal.open();
            });

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            
            deleteBtn.addClickListener(event -> {
                try {
                    catalogoService.deleteCategoria(category);
                    catGrid.setItems(catalogoService.findAllCategorias()); // Refresh grid
                    TailwindNotification.show("Categoría eliminada", TailwindNotification.Type.SUCCESS);
                } catch (DataIntegrityViolationException e) {
                     TailwindNotification.show("No se puede eliminar: La categoría está en uso", TailwindNotification.Type.ERROR);
                } catch (Exception e) {
                     TailwindNotification.show("Error al eliminar categoría", TailwindNotification.Type.ERROR);
                }
            });
            
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);

        catGrid.setItems(catalogoService.findAllCategorias());

        com.vaadin.flow.component.html.Div gridContainer = new com.vaadin.flow.component.html.Div(catGrid);
        gridContainer.addClassNames("w-full", "h-96", "overflow-hidden", "flex", "flex-col");
        catGrid.setHeightFull();

        // Add Button
        Button addBtn = new Button("Nueva Categoría", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "mb-4", "w-fit");
        addBtn.addClickListener(e -> {
             TailwindModal addModal = new TailwindModal("Nueva Categoría");
             TextField nameField = new TextField("Nombre");
             nameField.addClassName("w-full");
             
             TextField descField = new TextField("Descripción");
             descField.addClassName("w-full");
             
             FormLayout layout = new FormLayout(nameField, descField);
             layout.addClassName("w-full");
             addModal.addContent(layout);
             
             Button saveAddBtn = new Button("Guardar", event -> {
                 if (nameField.isEmpty()) {
                     TailwindNotification.show("El nombre es obligatorio", TailwindNotification.Type.ERROR);
                     return;
                 }
                 InvCategoria newCategory = new InvCategoria();
                 newCategory.setNombre(nameField.getValue());
                 newCategory.setDescripcion(descField.getValue());
                 newCategory.setActivo(true);
                 
                 try {
                     catalogoService.saveCategoria(newCategory);
                     catGrid.setItems(catalogoService.findAllCategorias());
                     TailwindNotification.show("Categoría creada", TailwindNotification.Type.SUCCESS);
                     addModal.close();
                 } catch (Exception ex) {
                     TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
                 }
             });
             saveAddBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
             
             Button cancelAddBtn = new Button("Cancelar", event -> addModal.close());
             cancelAddBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
             
             addModal.addFooterButton(cancelAddBtn);
             addModal.addFooterButton(saveAddBtn);
             add(addModal);
             addModal.open();
        });

        // Add layout with button and grid
        VerticalLayout content = new VerticalLayout(addBtn, gridContainer);
        content.setPadding(false);
        content.setSpacing(true);
        content.addClassNames("w-full", "h-full");

        modal.addContent(content);

        Button closeBtn = new Button("Cerrar", e -> modal.close());
        closeBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        modal.addFooterButton(closeBtn);

        add(modal);
        modal.open();
    }

    private void openUOMDialog() {
        TailwindModal modal = new TailwindModal("Listado de Unidades de Medida");
        modal.setDialogMaxWidth("max-w-5xl");

        Grid<InvUnidadMedida> uomGrid = new Grid<>(InvUnidadMedida.class, false);
        uomGrid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        
        uomGrid.addColumn(InvUnidadMedida::getNombre).setHeader("Nombre").setAutoWidth(true);
        uomGrid.addColumn(InvUnidadMedida::getAbreviatura).setHeader("Abreviatura").setAutoWidth(true);
        
        uomGrid.addColumn(new ComponentRenderer<>(uom -> {
            TailwindToggle toggle = new TailwindToggle("");
            toggle.getStyle().set("margin-top", "0");
            toggle.setValue(Boolean.TRUE.equals(uom.getActivo()));
            
            toggle.addValueChangeListener(event -> {
                uom.setActivo(event.getValue());
                try {
                    catalogoService.saveUnidadMedida(uom);
                    TailwindNotification.show("Estado actualizado", TailwindNotification.Type.SUCCESS);
                } catch (Exception e) {
                    toggle.setValue(event.getOldValue());
                    TailwindNotification.show("Error al actualizar", TailwindNotification.Type.ERROR);
                }
            });
            return toggle;
        })).setHeader("Activo").setAutoWidth(true);

        uomGrid.addComponentColumn(uom -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            
            editBtn.addClickListener(e -> {
                 TailwindModal editModal = new TailwindModal("Editar Unidad de Medida");
                 TextField nameField = new TextField("Nombre");
                 nameField.addClassName("w-full");
                 nameField.setValue(uom.getNombre());
                 
                 TextField abbrField = new TextField("Abreviatura");
                 abbrField.addClassName("w-full");
                 if (uom.getAbreviatura() != null) abbrField.setValue(uom.getAbreviatura());
                 
                 FormLayout layout = new FormLayout(nameField, abbrField);
                 layout.addClassName("w-full");
                 editModal.addContent(layout);
                 
                 Button saveEditBtn = new Button("Guardar", event -> {
                     uom.setNombre(nameField.getValue());
                     uom.setAbreviatura(abbrField.getValue());
                     try {
                         catalogoService.saveUnidadMedida(uom);
                         uomGrid.setItems(catalogoService.findAllUnidadesMedida());
                         TailwindNotification.show("Unidad actualizada", TailwindNotification.Type.SUCCESS);
                         editModal.close();
                     } catch (Exception ex) {
                         TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
                     }
                 });
                 saveEditBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
                 
                 Button cancelEditBtn = new Button("Cancelar", event -> editModal.close());
                 cancelEditBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
                 
                 editModal.addFooterButton(cancelEditBtn);
                 editModal.addFooterButton(saveEditBtn);
                 add(editModal);
                 editModal.open();
            });

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            
            deleteBtn.addClickListener(event -> {
                try {
                    catalogoService.deleteUnidadMedida(uom);
                    uomGrid.setItems(catalogoService.findAllUnidadesMedida());
                    TailwindNotification.show("Unidad eliminada", TailwindNotification.Type.SUCCESS);
                } catch (DataIntegrityViolationException e) {
                     TailwindNotification.show("No se puede eliminar: La unidad está en uso", TailwindNotification.Type.ERROR);
                } catch (Exception e) {
                     TailwindNotification.show("Error al eliminar unidad", TailwindNotification.Type.ERROR);
                }
            });
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);

        uomGrid.setItems(catalogoService.findAllUnidadesMedida());

        com.vaadin.flow.component.html.Div gridContainer = new com.vaadin.flow.component.html.Div(uomGrid);
        gridContainer.addClassNames("w-full", "h-96", "overflow-hidden", "flex", "flex-col");
        uomGrid.setHeightFull();

         // Add Button
        Button addBtn = new Button("Nueva Unidad", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "mb-4", "w-fit");
        addBtn.addClickListener(e -> {
             TailwindModal addModal = new TailwindModal("Nueva Unidad de Medida");
             TextField nameField = new TextField("Nombre");
             nameField.addClassName("w-full");
             
             TextField abbrField = new TextField("Abreviatura");
             abbrField.addClassName("w-full");
             
             FormLayout layout = new FormLayout(nameField, abbrField);
             layout.addClassName("w-full");
             addModal.addContent(layout);
             
             Button saveAddBtn = new Button("Guardar", event -> {
                 if (nameField.isEmpty()) {
                     TailwindNotification.show("El nombre es obligatorio", TailwindNotification.Type.ERROR);
                     return;
                 }
                 InvUnidadMedida newUom = new InvUnidadMedida();
                 newUom.setNombre(nameField.getValue());
                 newUom.setAbreviatura(abbrField.getValue());
                 if (newUom.getAbreviatura() == null || newUom.getAbreviatura().isEmpty()) {
                     newUom.setAbreviatura(nameField.getValue().length() > 5 ? nameField.getValue().substring(0, 5).toUpperCase() : nameField.getValue().toUpperCase());
                 }
                 newUom.setActivo(true);
                 
                 try {
                     catalogoService.saveUnidadMedida(newUom);
                     uomGrid.setItems(catalogoService.findAllUnidadesMedida());
                     TailwindNotification.show("Unidad creada", TailwindNotification.Type.SUCCESS);
                     addModal.close();
                 } catch (Exception ex) {
                     TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
                 }
             });
             saveAddBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
             
             Button cancelAddBtn = new Button("Cancelar", event -> addModal.close());
             cancelAddBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
             
             addModal.addFooterButton(cancelAddBtn);
             addModal.addFooterButton(saveAddBtn);
             add(addModal);
             addModal.open();
        });

        // Add layout with button and grid
        VerticalLayout content = new VerticalLayout(addBtn, gridContainer);
        content.setPadding(false);
        content.setSpacing(true);
        content.addClassNames("w-full", "h-full");

        modal.addContent(content);

        Button closeBtn = new Button("Cerrar", e -> modal.close());
        closeBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        modal.addFooterButton(closeBtn);

        add(modal);
        modal.open();
    }

    private void openVariantsDialog() {
        TailwindModal modal = new TailwindModal("Listado de Variantes de Producto");
        modal.setDialogMaxWidth("max-w-6xl");

        Grid<InvProductoVariante> varGrid = new Grid<>(InvProductoVariante.class, false);
        varGrid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        
        varGrid.addColumn(v -> v.getProducto() != null ? v.getProducto().getNombre() : "-").setHeader("Producto").setAutoWidth(true);
        varGrid.addColumn(InvProductoVariante::getNombreVariante).setHeader("Variante").setAutoWidth(true);
        varGrid.addColumn(InvProductoVariante::getCodigoBarras).setHeader("Cód. Barras").setAutoWidth(true);
        varGrid.addColumn(InvProductoVariante::getCodigoInternoVariante).setHeader("Cód. Interno").setAutoWidth(true);
        
        varGrid.addColumn(new ComponentRenderer<>(v -> {
            TailwindToggle toggle = new TailwindToggle("");
            toggle.getStyle().set("margin-top", "0");
            toggle.setValue(Boolean.TRUE.equals(v.getActivo()));
            
            toggle.addValueChangeListener(event -> {
                v.setActivo(event.getValue());
                try {
                    service.saveVariante(v);
                    TailwindNotification.show("Estado actualizado", TailwindNotification.Type.SUCCESS);
                } catch (Exception e) {
                    toggle.setValue(event.getOldValue());
                    TailwindNotification.show("Error al actualizar", TailwindNotification.Type.ERROR);
                }
            });
            return toggle;
        })).setHeader("Activo").setAutoWidth(true);

        varGrid.addComponentColumn(v -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            
            editBtn.addClickListener(e -> {
                 TailwindModal editModal = new TailwindModal("Editar Variante");
                 
                 ComboBox<InvProducto> productField = new ComboBox<>("Producto");
                 productField.setItems(service.findAll());
                 productField.setItemLabelGenerator(InvProducto::getNombre);
                 productField.setValue(v.getProducto());
                 productField.addClassName("w-full");

                 TextField nameField = new TextField("Nombre Variante");
                 nameField.addClassName("w-full");
                 nameField.setValue(v.getNombreVariante() != null ? v.getNombreVariante() : "");
                 
                 TextField barcodeField = new TextField("Código Barras");
                 barcodeField.addClassName("w-full");
                 if (v.getCodigoBarras() != null) barcodeField.setValue(v.getCodigoBarras());

                 TextField internalCodeField = new TextField("Código Interno");
                 internalCodeField.addClassName("w-full");
                 if (v.getCodigoInternoVariante() != null) internalCodeField.setValue(v.getCodigoInternoVariante());
                 
                 FormLayout layout = new FormLayout(productField, nameField, barcodeField, internalCodeField);
                 layout.addClassName("w-full");
                 editModal.addContent(layout);
                 
                 Button saveEditBtn = new Button("Guardar", event -> {
                     if (productField.getValue() == null) {
                         TailwindNotification.show("El producto es obligatorio", TailwindNotification.Type.ERROR);
                         return;
                     }
                     v.setProducto(productField.getValue());
                     v.setNombreVariante(nameField.getValue());
                     v.setCodigoBarras(barcodeField.getValue());
                     v.setCodigoInternoVariante(internalCodeField.getValue());
                     try {
                         service.saveVariante(v);
                         varGrid.setItems(service.findAllVariantes());
                         TailwindNotification.show("Variante actualizada", TailwindNotification.Type.SUCCESS);
                         editModal.close();
                     } catch (Exception ex) {
                         TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
                     }
                 });
                 saveEditBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
                 
                 Button cancelEditBtn = new Button("Cancelar", event -> editModal.close());
                 cancelEditBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
                 
                 editModal.addFooterButton(cancelEditBtn);
                 editModal.addFooterButton(saveEditBtn);
                 add(editModal);
                 editModal.open();
            });

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            
            deleteBtn.addClickListener(event -> {
                try {
                    service.deleteVariante(v);
                    varGrid.setItems(service.findAllVariantes());
                    TailwindNotification.show("Variante eliminada", TailwindNotification.Type.SUCCESS);
                } catch (DataIntegrityViolationException e) {
                     TailwindNotification.show("No se puede eliminar: La variante está en uso", TailwindNotification.Type.ERROR);
                } catch (Exception e) {
                     TailwindNotification.show("Error al eliminar variante", TailwindNotification.Type.ERROR);
                }
            });
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);

        varGrid.setItems(service.findAllVariantes());

        com.vaadin.flow.component.html.Div gridContainer = new com.vaadin.flow.component.html.Div(varGrid);
        gridContainer.addClassNames("w-full", "h-96", "overflow-hidden", "flex", "flex-col");
        varGrid.setHeightFull();

         // Add Button
        Button addBtn = new Button("Nueva Variante", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "mb-4", "w-fit");
        addBtn.addClickListener(e -> {
             TailwindModal addModal = new TailwindModal("Nueva Variante");
             
             ComboBox<InvProducto> productField = new ComboBox<>("Producto");
             productField.setItems(service.findAll());
             productField.setItemLabelGenerator(InvProducto::getNombre);
             productField.addClassName("w-full");

             TextField nameField = new TextField("Nombre Variante");
             nameField.addClassName("w-full");
             
             TextField barcodeField = new TextField("Código Barras");
             barcodeField.addClassName("w-full");
             
             TextField internalCodeField = new TextField("Código Interno");
             internalCodeField.addClassName("w-full");
             
             FormLayout layout = new FormLayout(productField, nameField, barcodeField, internalCodeField);
             layout.addClassName("w-full");
             addModal.addContent(layout);
             
             Button saveAddBtn = new Button("Guardar", event -> {
                 if (productField.getValue() == null) {
                     TailwindNotification.show("El producto es obligatorio", TailwindNotification.Type.ERROR);
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
                     varGrid.setItems(service.findAllVariantes());
                     TailwindNotification.show("Variante creada", TailwindNotification.Type.SUCCESS);
                     addModal.close();
                 } catch (Exception ex) {
                     TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
                 }
             });
             saveAddBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
             
             Button cancelAddBtn = new Button("Cancelar", event -> addModal.close());
             cancelAddBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
             
             addModal.addFooterButton(cancelAddBtn);
             addModal.addFooterButton(saveAddBtn);
             add(addModal);
             addModal.open();
        });

        // Add layout with button and grid
        VerticalLayout content = new VerticalLayout(addBtn, gridContainer);
        content.setPadding(false);
        content.setSpacing(true);
        content.addClassNames("w-full", "h-full");

        modal.addContent(content);

        Button closeBtn = new Button("Cerrar", e -> modal.close());
        closeBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        modal.addFooterButton(closeBtn);

        add(modal);
        modal.open();
    }

    private void openBatchesDialog() {
        TailwindModal modal = new TailwindModal("Listado de Lotes");
        modal.setDialogMaxWidth("max-w-6xl");

        Grid<InvLote> grid = new Grid<>(InvLote.class, false);
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        
        grid.addColumn(l -> l.getProductoVariante() != null && l.getProductoVariante().getProducto() != null ? 
            l.getProductoVariante().getProducto().getNombre() + " - " + l.getProductoVariante().getNombreVariante() : "-").setHeader("Producto / Variante").setAutoWidth(true);
        grid.addColumn(InvLote::getCodigoLote).setHeader("Cód. Lote").setAutoWidth(true);
        grid.addColumn(l -> result(l.getFechaCaducidad())).setHeader("Caducidad").setAutoWidth(true);
        grid.addColumn(InvLote::getObservaciones).setHeader("Observaciones").setAutoWidth(true);

        grid.addComponentColumn(lote -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            
            editBtn.addClickListener(e -> {
                 TailwindModal editModal = new TailwindModal("Editar Lote");
                 
                 ComboBox<InvProductoVariante> variantField = new ComboBox<>("Variante de Producto");
                 variantField.setItems(service.findAllVariantes());
                 variantField.setItemLabelGenerator(v -> v.getProducto().getNombre() + " - " + v.getNombreVariante());
                 variantField.setValue(lote.getProductoVariante());
                 variantField.addClassName("w-full");

                 TextField codeField = new TextField("Código Lote");
                 codeField.addClassName("w-full");
                 codeField.setValue(lote.getCodigoLote() != null ? lote.getCodigoLote() : "");
                 
                 TailwindDatePicker expiryField = new TailwindDatePicker("Fecha Caducidad");
                 expiryField.addClassName("w-full");
                 if (lote.getFechaCaducidad() != null) {
                     expiryField.setValue(lote.getFechaCaducidad().toLocalDate());
                 }

                 TextArea obsField = new TextArea("Observaciones");
                 obsField.addClassName("w-full");
                 if (lote.getObservaciones() != null) obsField.setValue(lote.getObservaciones());
                 
                 FormLayout layout = new FormLayout(variantField, codeField, expiryField, obsField);
                 layout.addClassName("w-full");
                 layout.setColspan(obsField, 2);
                 editModal.addContent(layout);
                 
                 Button saveEditBtn = new Button("Guardar", event -> {
                     if (variantField.getValue() == null) {
                         TailwindNotification.show("La variante es obligatoria", TailwindNotification.Type.ERROR);
                         return;
                     }
                     if (codeField.isEmpty()) {
                         TailwindNotification.show("El código es obligatorio", TailwindNotification.Type.ERROR);
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
                         grid.setItems(almacenService.findAllLotes());
                         TailwindNotification.show("Lote actualizado", TailwindNotification.Type.SUCCESS);
                         editModal.close();
                     } catch (Exception ex) {
                         TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
                     }
                 });
                 saveEditBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
                 
                 Button cancelEditBtn = new Button("Cancelar", event -> editModal.close());
                 cancelEditBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
                 
                 editModal.addFooterButton(cancelEditBtn);
                 editModal.addFooterButton(saveEditBtn);
                 add(editModal);
                 editModal.open();
            });

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            
            deleteBtn.addClickListener(event -> {
                try {
                    almacenService.deleteLote(lote);
                    grid.setItems(almacenService.findAllLotes());
                    TailwindNotification.show("Lote eliminado", TailwindNotification.Type.SUCCESS);
                } catch (DataIntegrityViolationException e) {
                     TailwindNotification.show("No se puede eliminar: El lote está en uso", TailwindNotification.Type.ERROR);
                } catch (Exception e) {
                     TailwindNotification.show("Error al eliminar lote", TailwindNotification.Type.ERROR);
                }
            });
            
            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acciones").setAutoWidth(true);

        grid.setItems(almacenService.findAllLotes());

        com.vaadin.flow.component.html.Div gridContainer = new com.vaadin.flow.component.html.Div(grid);
        gridContainer.addClassNames("w-full", "h-96", "overflow-hidden", "flex", "flex-col");
        grid.setHeightFull();

         // Add Button
        Button addBtn = new Button("Nuevo Lote", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "mb-4", "w-fit");
        addBtn.addClickListener(e -> {
             TailwindModal addModal = new TailwindModal("Nuevo Lote");
             
             ComboBox<InvProductoVariante> variantField = new ComboBox<>("Variante de Producto");
             variantField.setItems(service.findAllVariantes());
             variantField.setItemLabelGenerator(v -> v.getProducto().getNombre() + " - " + v.getNombreVariante());
             variantField.addClassName("w-full");

             TextField codeField = new TextField("Código Lote");
             codeField.addClassName("w-full");
             
             TailwindDatePicker expiryField = new TailwindDatePicker("Fecha Caducidad");
             expiryField.addClassName("w-full");

             TextArea obsField = new TextArea("Observaciones");
             obsField.addClassName("w-full");
             
             FormLayout layout = new FormLayout(variantField, codeField, expiryField, obsField);
             layout.addClassName("w-full");
             layout.setColspan(obsField, 2);
             addModal.addContent(layout);
             
             Button saveAddBtn = new Button("Guardar", event -> {
                 if (variantField.getValue() == null) {
                     TailwindNotification.show("La variante es obligatoria", TailwindNotification.Type.ERROR);
                     return;
                 }
                 if (codeField.isEmpty()) {
                     TailwindNotification.show("El código es obligatorio", TailwindNotification.Type.ERROR);
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
                     grid.setItems(almacenService.findAllLotes());
                     TailwindNotification.show("Lote creado", TailwindNotification.Type.SUCCESS);
                     addModal.close();
                 } catch (Exception ex) {
                     TailwindNotification.show("Error al guardar", TailwindNotification.Type.ERROR);
                 }
             });
             saveAddBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
             
             Button cancelAddBtn = new Button("Cancelar", event -> addModal.close());
             cancelAddBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");
             
             addModal.addFooterButton(cancelAddBtn);
             addModal.addFooterButton(saveAddBtn);
             add(addModal);
             addModal.open();
        });

        // Add layout with button and grid
        VerticalLayout content = new VerticalLayout(addBtn, gridContainer);
        content.setPadding(false);
        content.setSpacing(true);
        content.addClassNames("w-full", "h-full");

        modal.addContent(content);

        Button closeBtn = new Button("Cerrar", e -> modal.close());
        closeBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        modal.addFooterButton(closeBtn);

        add(modal);
        modal.open();
    }
    
    private String result(java.time.LocalDateTime date) {
        return date != null ? date.toLocalDate().toString() : "-";
    }
}
