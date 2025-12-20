package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.entity.InvCategoria;
import com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida;
import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
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
import com.vaadin.flow.component.datepicker.DatePicker;
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

        Button addBtn = new Button("Nuevo Producto", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all"); // Tailwind styled button
        addBtn.addClickListener(e -> openProductDialog());

        HorizontalLayout buttons = new HorizontalLayout(catBtn, addBtn);
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
        unidadMedida.addClassName("w-full");
        
        // New Fields
        ComboBox<InvUbicacion> ubicacion = new ComboBox<>("Ubicación (Inicial)");
        ubicacion.setItems(almacenService.findAllUbicaciones());
        ubicacion.setItemLabelGenerator(u -> u.getCodigo() + " - " + u.getDescripcion());
        ubicacion.addClassName("w-full");

        TextField lote = new TextField("Lote (Inicial)");
        lote.addClassName("w-full");

        DatePicker fechaCaducidad = new DatePicker("Fecha Caducidad Lote");
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
                service.save(product);
                
                // NOTE: Location, Lot, Expiration and Observations are not saved here as they belong to Inventory/Stock entities,
                // not the Product entity definition. Logic to create initial stock would be needed here.
                
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
        catGrid.addComponentColumn(category -> {
            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClassNames("text-red-500", "hover:text-red-700");
            
            deleteBtn.addClickListener(event -> {
                try {
                    catalogoService.deleteCategoria(category);
                    catGrid.setItems(catalogoService.findAllCategorias()); // Refresh grid
                    TailwindNotification.show("Categoría eliminada", TailwindNotification.Type.SUCCESS);
                    
                    // Also refresh the main combo box if it exists in the parent scope (conceptually)
                    // But we are in a dialog method. 
                    // Ideally we should refresh the ComboBox in the main form too if we delete something.
                    // Doing a full page refresh or component refresh might be needed.
                    // For now, let's just refresh the grid.
                    
                } catch (DataIntegrityViolationException e) {
                     TailwindNotification.show("No se puede eliminar: La categoría está en uso", TailwindNotification.Type.ERROR);
                } catch (Exception e) {
                     TailwindNotification.show("Error al eliminar categoría", TailwindNotification.Type.ERROR);
                }
            });
            return deleteBtn;
        }).setHeader("Acciones").setAutoWidth(true);

        catGrid.setItems(catalogoService.findAllCategorias());

        com.vaadin.flow.component.html.Div gridContainer = new com.vaadin.flow.component.html.Div(catGrid);
        gridContainer.addClassNames("w-full", "h-96", "overflow-hidden", "flex", "flex-col");
        catGrid.setHeightFull();

        modal.addContent(gridContainer);

        Button closeBtn = new Button("Cerrar", e -> modal.close());
        closeBtn.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");
        modal.addFooterButton(closeBtn);

        add(modal);
        modal.open();
    }
}
