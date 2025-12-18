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
        
        Button addBtn = new Button("Nuevo Producto", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all"); // Tailwind styled button
        addBtn.addClickListener(e -> openProductDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("view.products.title"), addBtn);
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
        categoria.setItems(catalogoService.findAllCategorias());
        categoria.setItemLabelGenerator(InvCategoria::getNombre);
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
}
