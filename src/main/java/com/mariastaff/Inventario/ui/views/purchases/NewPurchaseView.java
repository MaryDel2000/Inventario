package com.mariastaff.Inventario.ui.views.purchases;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.mariastaff.Inventario.backend.data.entity.InvCompra;
import com.mariastaff.Inventario.backend.data.entity.InvCompraDetalle;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.data.entity.InvProveedor;
import com.mariastaff.Inventario.backend.data.entity.InvUbicacion;
import com.mariastaff.Inventario.backend.service.AlmacenService;
import com.mariastaff.Inventario.backend.service.CompraService;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.button.ButtonVariant;
import com.mariastaff.Inventario.ui.components.base.TailwindDatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Nueva Compra | Compras")
@Route(value = "purchases/new", layout = MainLayout.class)
@PermitAll
public class NewPurchaseView extends VerticalLayout {

    private final CompraService compraService;
    private final ProductoService productoService;
    private final AlmacenService almacenService;
    private final com.mariastaff.Inventario.backend.service.CatalogoService catalogoService;
    
    private final Binder<InvCompra> binder = new Binder<>(InvCompra.class);
    private final List<InvCompraDetalle> detalles = new ArrayList<>();
    private final Grid<InvCompraDetalle> grid = new Grid<>();
    private final Span totalSpan = new Span();

    // Form fields
    private final ComboBox<InvProveedor> proveedor = new ComboBox<>("Proveedor");
    private final ComboBox<InvAlmacen> almacenDestino = new ComboBox<>("Almacén Destino");
    private final TailwindDatePicker fechaCompra = new TailwindDatePicker("Fecha Compra");
    private final TextField tipoDocumento = new TextField("Tipo Doc.");
    private final TextField numeroDocumento = new TextField("Nº Documento");

    public NewPurchaseView(CompraService compraService, 
                           ProductoService productoService, 
                           AlmacenService almacenService,
                           com.mariastaff.Inventario.backend.service.CatalogoService catalogoService) {
        this.compraService = compraService;
        this.productoService = productoService;
        this.almacenService = almacenService;
        this.catalogoService = catalogoService;

        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        createHeader();
        createForm();
        createGrid();
        createFooter();
        
        initializeBinder();
    }
    
    private void createHeader() {
        AppLabel title = new AppLabel("Registrar Nueva Compra");
        add(title);
    }
    
    private void createForm() {
        FormLayout form = new FormLayout();
        form.addClassNames("bg-bg-surface", "p-6", "rounded-lg", "shadow", "mb-4");
        
        proveedor.setItems(compraService.findAllProveedores());
        proveedor.setItemLabelGenerator(p -> p.getEntidad().getNombreCompleto());
        
        almacenDestino.setItems(almacenService.findAllAlmacenes());
        almacenDestino.setItemLabelGenerator(InvAlmacen::getNombre);
        
        fechaCompra.setValue(java.time.LocalDate.now());
        
        form.add(proveedor, almacenDestino, fechaCompra, tipoDocumento, numeroDocumento);
        form.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("600px", 2),
            new FormLayout.ResponsiveStep("1000px", 3)
        );
        add(form);
    }
    
    private void createGrid() {
        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.addClassNames("bg-bg-surface", "p-4", "rounded-lg", "shadow");
        gridLayout.setPadding(false);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.getStyle().set("padding", "var(--lumo-space-m)");

        Button addItemBtn = new Button("Agregar Producto", VaadinIcon.PLUS.create(), e -> openAddItemDialog());
        addItemBtn.addClassNames("bg-primary", "text-white", "font-semibold", "rounded-lg", "shadow-sm");

        toolbar.add(new Span("Detalle de Compra"), addItemBtn);

        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COMPACT);
        grid.addColumn(d -> d.getProductoVariante().getProducto().getNombre() + " - " + d.getProductoVariante().getNombreVariante()).setHeader("Producto").setAutoWidth(true);
        grid.addColumn(InvCompraDetalle::getCantidad).setHeader("Cant.").setAutoWidth(true);
        grid.addColumn(d -> d.getCostoUnitario()).setHeader("Costo U.").setAutoWidth(true);
        grid.addColumn(d -> d.getFechaCaducidad() != null ? d.getFechaCaducidad().toLocalDate().toString() : "-").setHeader("Caducidad").setAutoWidth(true);
        grid.addColumn(InvCompraDetalle::getSubtotal).setHeader("Subtotal").setAutoWidth(true);
        
        grid.addComponentColumn(item -> {
            Button remove = new Button(VaadinIcon.TRASH.create(), e -> {
                detalles.remove(item);
                refreshGrid();
            });
            remove.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR, com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
            return remove;
        });
        
        gridLayout.add(toolbar, grid);
        add(gridLayout);
    }
    
    private void createFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidthFull();
        footer.addClassNames("bg-bg-surface", "p-6", "rounded-lg", "shadow", "mt-4", "items-center", "justify-between");
        
        totalSpan.addClassNames("text-2xl", "font-bold", "text-primary");
        totalSpan.setText("Total: $0.00");
        
        Button saveBtn = new Button("Guardar Compra", e -> save());
        saveBtn.addClassNames("bg-green-600", "text-white", "font-bold", "text-lg", "py-2", "px-8", "rounded-lg", "hover:bg-green-700");
        
        footer.add(totalSpan, saveBtn);
        add(footer);
    }

    private void initializeBinder() {
        binder.forField(proveedor).asRequired("Requerido").bind(InvCompra::getProveedor, InvCompra::setProveedor);
        binder.forField(almacenDestino).asRequired("Requerido").bind(InvCompra::getAlmacenDestino, InvCompra::setAlmacenDestino);
        binder.forField(tipoDocumento).bind(InvCompra::getTipoDocumento, InvCompra::setTipoDocumento);
        binder.forField(numeroDocumento).asRequired("Requerido").bind(InvCompra::getNumeroDocumento, InvCompra::setNumeroDocumento);
        binder.setBean(new InvCompra());
    }

    private void openAddItemDialog() {
        if (almacenDestino.getValue() == null) {
            TailwindNotification.show("Seleccione primero un almacén de destino", TailwindNotification.Type.WARNING);
            return;
        }

        try {
            TailwindModal modal = new TailwindModal("Agregar Producto");
            // width removed to avoid breaking fixed overlay layout
            
            ComboBox<InvProductoVariante> variantSelect = new ComboBox<>("Producto");
            
            Button newProductBtn = new Button(VaadinIcon.PLUS.create());
            newProductBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            newProductBtn.setTooltipText("Crear nuevo producto");
            HorizontalLayout productSelectionRow = new HorizontalLayout(variantSelect, newProductBtn);
            productSelectionRow.setAlignItems(Alignment.END); // Align to bottom to match input field
            productSelectionRow.addClassName("items-end"); // Tailwind backup
            productSelectionRow.setWidthFull();
            // make combo take max width
            productSelectionRow.setFlexGrow(1, variantSelect);
            // Add margin to button to align better
            newProductBtn.addClassNames("mb-1"); // Small margin bottom key tweak usage 

            newProductBtn.addClickListener(e -> {
                 com.mariastaff.Inventario.ui.components.dialogs.ProductFormDialog productDialog = 
                     new com.mariastaff.Inventario.ui.components.dialogs.ProductFormDialog(productoService, catalogoService, almacenService);
                 productDialog.setPurchaseMode(true); // Hide initial stock fields
                 productDialog.setProduct(new com.mariastaff.Inventario.backend.data.entity.InvProducto());
                 productDialog.setOnSave(() -> {
                     try {
                         variantSelect.setItems(productoService.findAllVariantesWithProducto());
                     } catch (Exception ex) {
                         ex.printStackTrace();
                     }
                 });
                 // IMPORTANT: Add to UI hierarchy before opening if it's not self-attached (TailwindModal usually is but let's be safe)
                 // actually TailwindModal extends Dialog, but in Vaadin Flow you often just call open().
                 // However, if the click listener wasn't firing, let's debug.
                 // The issue "no muestra el formulario" implies the event fired but nothing happened OR event didn't fire.
                 // We will explicitly add it.
                 add(productDialog);
                 productDialog.open();
            });
            
            // Optimized loading using join fetch to avoid LazyInitException
            List<InvProductoVariante> allVariants;
            try {
                allVariants = productoService.findAllVariantesWithProducto();
            } catch (Exception e) {
                e.printStackTrace();
                TailwindNotification.show("Error cargando productos: " + e.getMessage(), TailwindNotification.Type.ERROR);
                return;
            }
            
            variantSelect.setItems(allVariants);
            variantSelect.setItemLabelGenerator(v -> {
                try {
                   String pName = (v.getProducto() != null) ? v.getProducto().getNombre() : "Producto Desconocido";
                   String vName = (v.getNombreVariante() != null) ? v.getNombreVariante() : "Variante";
                   return pName + " - " + vName;
                } catch (Exception e) {
                   return "Error: " + v.getId();
                }
            });
            variantSelect.setWidthFull();
            
            BigDecimalField qtyField = new BigDecimalField("Cantidad");
            qtyField.setLocale(java.util.Locale.US);
            
            BigDecimalField costField = new BigDecimalField("Costo Unitario");
            costField.setLocale(java.util.Locale.US);
            
            TailwindDatePicker expiryDate = new TailwindDatePicker("Fecha Caducidad (Si aplica)");
            
            ComboBox<InvUbicacion> locationSelect = new ComboBox<>("Ubicación Destino (Estante/Fila)");
            List<InvUbicacion> locs = almacenService.findUbicacionesByAlmacen(almacenDestino.getValue());

            locationSelect.setItems(locs);
            locationSelect.setItemLabelGenerator(InvUbicacion::getCodigo);
            locationSelect.setWidthFull();

            Button addBtn = new Button("Agregar", e -> {
                try {
                    if (variantSelect.getValue() != null && qtyField.getValue() != null && costField.getValue() != null) {
                         // Warning if location is missing but we proceed (service might handle default)
                         if (locationSelect.getValue() == null && !locs.isEmpty()) {
                              TailwindNotification.show("Seleccione una ubicación", TailwindNotification.Type.WARNING);
                              return;
                         }

                        InvCompraDetalle detalle = new InvCompraDetalle();
                        detalle.setProductoVariante(variantSelect.getValue());
                        detalle.setCantidad(qtyField.getValue());
                        detalle.setCostoUnitario(costField.getValue());
                        detalle.setSubtotal(qtyField.getValue().multiply(costField.getValue()));
                        if (expiryDate.getValue() != null) {
                            detalle.setFechaCaducidad(expiryDate.getValue().atStartOfDay());
                        }
                        
                        // Set the specific target location
                        if (locationSelect.getValue() != null) {
                             detalle.setTargetLocation(locationSelect.getValue());
                        }
                                        
                        detalles.add(detalle);
                        refreshGrid();
                        modal.close();
                    } else {
                        TailwindNotification.show("Complete todos los campos requeridos", TailwindNotification.Type.ERROR);
                    }
                } catch (Exception ex) {
                    TailwindNotification.show("Error al agregar producto: " + ex.getMessage(), TailwindNotification.Type.ERROR);
                    ex.printStackTrace();
                }
            });
            addBtn.addClassNames("bg-primary", "text-white", "w-full", "mt-4");
            
            Button cancelBtn = new Button("Cancelar", e -> modal.close());
            cancelBtn.addClassNames("w-full", "bg-gray-200", "text-gray-800", "mt-2", "rounded-lg", "shadow-sm");

            VerticalLayout layout = new VerticalLayout(productSelectionRow, new HorizontalLayout(qtyField, costField), expiryDate, locationSelect, addBtn, cancelBtn);
            modal.addContent(layout);
            add(modal);
            modal.open();
        } catch (Exception e) {
             TailwindNotification.show("Error al abrir diálogo: " + e.getMessage(), TailwindNotification.Type.ERROR);
             e.printStackTrace();
        }
    }
    
    private void refreshGrid() {
        grid.setItems(detalles);
        BigDecimal total = detailsTotal();
        totalSpan.setText("Total: $" + total);
    }
    
    private BigDecimal detailsTotal() {
        return detalles.stream()
                .map(InvCompraDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private void save() {
        if (binder.validate().isOk() && !detalles.isEmpty()) {
            if (fechaCompra.getValue() == null) {
                TailwindNotification.show("La fecha de compra es requerida", TailwindNotification.Type.WARNING);
                return;
            }

            try {
                InvCompra compra = binder.getBean();
                compra.setFechaCompra(fechaCompra.getValue().atStartOfDay());
                compra.setTotalCompra(detailsTotal());
                compra.setEstado("COMPLETADO");
                
                // Link details
                detalles.forEach(d -> d.setCompra(compra));
            
                compraService.saveCompraWithDetails(compra, detalles); 
                TailwindNotification.show("Compra registrada con éxito", TailwindNotification.Type.SUCCESS);
                
                // Reset form
                binder.setBean(new InvCompra());
                fechaCompra.setValue(java.time.LocalDate.now());
                detalles.clear();
                refreshGrid();
            } catch (Exception e) {
                TailwindNotification.show("Error al guardar: " + e.getMessage(), TailwindNotification.Type.ERROR);
                e.printStackTrace();
            }
        } else {
            TailwindNotification.show("Verifique los campos y agregue productos", TailwindNotification.Type.WARNING);
        }
    }
}
