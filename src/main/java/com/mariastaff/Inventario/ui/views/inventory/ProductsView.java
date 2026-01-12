package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.entity.InvCategoria;
import com.mariastaff.Inventario.backend.data.entity.InvUnidadMedida;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.data.entity.InvLote;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.backend.service.CatalogoService;
import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.mariastaff.Inventario.ui.components.dialogs.CategoryManagementDialog;
import com.mariastaff.Inventario.ui.components.dialogs.UomManagementDialog;
import com.mariastaff.Inventario.ui.components.dialogs.ProductFormDialog;
import com.mariastaff.Inventario.ui.components.dialogs.VariantsManagementDialog;
import com.mariastaff.Inventario.ui.components.dialogs.BatchesManagementDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.ButtonVariant;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;

@PageTitle("Productos | Inventario")
@Route(value = "inventory/products", layout = MainLayout.class)
@PermitAll
public class ProductsView extends VerticalLayout {

    private final ProductoService service;
    private final CatalogoService catalogoService;
    private final AlmacenService almacenService;
    private final Grid<InvProducto> grid = new Grid<>(InvProducto.class);
    
    private ComboBox<InvCategoria> filterCategory;
    private ComboBox<InvUnidadMedida> filterUOM;
    private ComboBox<String> filterActive;

    public ProductsView(ProductoService service, CatalogoService catalogoService, AlmacenService almacenService) {
        this.service = service;
        this.catalogoService = catalogoService;
        this.almacenService = almacenService;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        
        Button catBtn = new Button(getTranslation("action.view.categories"), VaadinIcon.TAGS.create());
        catBtn.addClassNames("bg-white", "text-primary", "border", "border-gray-200", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow-sm", "hover:shadow-md", "hover:bg-gray-50", "transition-all", "mr-2");
        catBtn.addClickListener(e -> openCategoriesDialog());

        Button uomBtn = new Button(getTranslation("action.view.uom"), VaadinIcon.SLIDERS.create());
        uomBtn.addClassNames("bg-white", "text-primary", "border", "border-gray-200", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow-sm", "hover:shadow-md", "hover:bg-gray-50", "transition-all", "mr-2");
        uomBtn.addClickListener(e -> openUOMDialog());

        Button varBtn = new Button(getTranslation("action.view.variants"), VaadinIcon.QRCODE.create());
        varBtn.addClassNames("bg-white", "text-primary", "border", "border-gray-200", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow-sm", "hover:shadow-md", "hover:bg-gray-50", "transition-all", "mr-2");
        varBtn.addClickListener(e -> openVariantsDialog());
        
        Button lotesBtn = new Button(getTranslation("action.view.batches"), VaadinIcon.BARCODE.create());
        lotesBtn.addClassNames("bg-white", "text-primary", "border", "border-gray-200", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow-sm", "hover:shadow-md", "hover:bg-gray-50", "transition-all", "mr-2");
        lotesBtn.addClickListener(e -> openBatchesDialog());

        Button addBtn = new Button(getTranslation("action.new.product"), VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openProductDialog());

        HorizontalLayout buttons = new HorizontalLayout(catBtn, uomBtn, varBtn, lotesBtn, addBtn);
        buttons.setAlignItems(Alignment.CENTER);
        
        HorizontalLayout header = new HorizontalLayout(new AppLabel("view.products.title"), buttons);
        header.addClassNames("w-full", "justify-between", "items-center");

        HorizontalLayout filters = configureFilters();

        add(header, filters, grid);
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
        grid.addColumn(p -> service.findVariantesByProducto(p).stream()
                .map(InvProductoVariante::getNombreVariante)
                .collect(java.util.stream.Collectors.joining(", ")))
                .setHeader(getTranslation("view.products.grid.variants")).setAutoWidth(true);

        grid.addColumn(producto -> {
            java.math.BigDecimal stock = almacenService.getStockTotal(producto.getId());
            return stock != null ? stock.toString() : "0";
        }).setHeader(getTranslation("view.products.grid.stock")).setAutoWidth(true);

        grid.addComponentColumn(producto -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editBtn.addClassNames("text-primary", "hover:text-blue-700", "mr-2");
            editBtn.addClickListener(e -> openEditProductDialog(producto));

            return editBtn;
        }).setHeader(getTranslation("view.products.grid.actions")).setAutoWidth(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private HorizontalLayout configureFilters() {
        filterCategory = new ComboBox<>(getTranslation("filter.category"));
        filterCategory.setItems(catalogoService.findAllCategorias());
        filterCategory.setItemLabelGenerator(InvCategoria::getNombre);
        filterCategory.setClearButtonVisible(true);
        filterCategory.addValueChangeListener(e -> updateList());

        filterUOM = new ComboBox<>(getTranslation("filter.uom"));
        filterUOM.setItems(catalogoService.findAllUnidadesMedida());
        filterUOM.setItemLabelGenerator(InvUnidadMedida::getNombre);
        filterUOM.setClearButtonVisible(true);
        filterUOM.addValueChangeListener(e -> updateList());

        filterActive = new ComboBox<>(getTranslation("filter.status"));
        filterActive.setItems(getTranslation("status.active"), getTranslation("status.inactive"));
        filterActive.setClearButtonVisible(true);
        filterActive.addValueChangeListener(e -> updateList());
        
        HorizontalLayout layout = new HorizontalLayout(filterCategory, filterUOM, filterActive);
        layout.addClassNames("w-full", "items-end", "mb-4");
        return layout;
    }

    private void updateList() {
        Boolean activeStatus = null;
        if (filterActive.getValue() != null) {
            activeStatus = getTranslation("status.active").equals(filterActive.getValue());
        }
        grid.setItems(service.search(filterCategory.getValue(), filterUOM.getValue(), activeStatus));
    }

    private void openProductDialog() {
        ProductFormDialog dialog = new ProductFormDialog(service, catalogoService, almacenService);
        dialog.setProduct(new InvProducto());
        dialog.setOnSave(this::updateList);
        add(dialog);
        dialog.open();
    }

    private void openEditProductDialog(InvProducto originalProduct) {
        // Reload product
        InvProducto product = service.findById(originalProduct.getId()).orElse(originalProduct);
        ProductFormDialog dialog = new ProductFormDialog(service, catalogoService, almacenService);
        dialog.setProduct(product);
        dialog.setOnSave(this::updateList);
        add(dialog);
        dialog.open();
    }

    private void openCategoriesDialog() {
        CategoryManagementDialog dialog = new CategoryManagementDialog(catalogoService);
        dialog.addDetachListener(e -> {
            updateList();
            filterCategory.setItems(catalogoService.findAllCategorias());
        });
        add(dialog);
        dialog.open();
    }
    
    private void openUOMDialog() {
        UomManagementDialog dialog = new UomManagementDialog(catalogoService);
        dialog.addDetachListener(e -> {
            updateList();
            filterUOM.setItems(catalogoService.findAllUnidadesMedida());
        });
        add(dialog);
        dialog.open();
    }


    private void openVariantsDialog() {
        VariantsManagementDialog dialog = new VariantsManagementDialog(service, this::updateList);
        dialog.addDetachListener(e -> updateList());
        add(dialog);
        dialog.open();
    }

    private void openBatchesDialog() {
        BatchesManagementDialog dialog = new BatchesManagementDialog(almacenService, service);
        dialog.addDetachListener(e -> updateList());
        add(dialog);
        dialog.open();
    }
    
    private String result(java.time.LocalDateTime date) {
        return date != null ? date.toLocalDate().toString() : "-";
    }
}
