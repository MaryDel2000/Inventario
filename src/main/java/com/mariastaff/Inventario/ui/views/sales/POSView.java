package com.mariastaff.Inventario.ui.views.sales;

import com.mariastaff.Inventario.backend.data.entity.InvAlmacen;
import com.mariastaff.Inventario.backend.data.entity.InvProducto;
import com.mariastaff.Inventario.backend.data.entity.InvProductoVariante;
import com.mariastaff.Inventario.backend.data.entity.PosCliente;
import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import com.mariastaff.Inventario.backend.data.entity.PosVentaDetalle;
import com.mariastaff.Inventario.backend.data.entity.SysUsuario;
import com.mariastaff.Inventario.backend.service.PosService;
import com.mariastaff.Inventario.backend.service.ProductoService;
import com.mariastaff.Inventario.backend.service.UserService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Punto de Venta | Ventas")
@Route(value = "sales/pos", layout = MainLayout.class)
@PermitAll
public class POSView extends VerticalLayout {

    private final PosService posService;
    private final ProductoService productoService;
    private final com.mariastaff.Inventario.backend.data.repository.InvAlmacenRepository almacenRepository;
    private final UserService userService;
    
    private final ComboBox<PosCliente> clienteSelect = new ComboBox<>();
    private final ComboBox<InvAlmacen> almacenSelect = new ComboBox<>();
    private final ComboBox<InvProducto> productoSelect = new ComboBox<>();
    private final Grid<PosVentaDetalle> cartGrid = new Grid<>();
    private final List<PosVentaDetalle> cartItems = new ArrayList<>();
    private final Span totalSpan = new Span();
    
    public POSView(PosService posService, ProductoService productoService,
                   com.mariastaff.Inventario.backend.data.repository.InvAlmacenRepository almacenRepository,
                   UserService userService) {
        this.posService = posService;
        this.productoService = productoService;
        this.almacenRepository = almacenRepository;
        this.userService = userService;
        
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        setSizeFull();
        
        add(new AppLabel("Punto de Venta"));
        
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        mainLayout.addClassName("gap-4");
        
        // Left Panel: Product Selection & Cart
        VerticalLayout leftPanel = new VerticalLayout();
        leftPanel.addClassNames("bg-bg-surface", "rounded-lg", "shadow", "p-4");
        leftPanel.setSizeFull();
        
        configureControls();
        configureGrid();
        
        HorizontalLayout topControls = new HorizontalLayout(clienteSelect, almacenSelect, productoSelect);
        topControls.setWidthFull();
        topControls.setFlexGrow(1, productoSelect);
        topControls.setAlignItems(Alignment.BASELINE);
        
        // Defaults
        List<InvAlmacen> almacenes = almacenRepository.findAll();
        if (!almacenes.isEmpty()) almacenSelect.setValue(almacenes.get(0));
        
        leftPanel.add(topControls, cartGrid);
        
        // Right Panel: Totals & Actions
        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.addClassNames("bg-bg-surface", "rounded-lg", "shadow", "p-6", "border", "border-border");
        rightPanel.setWidth("350px");
        rightPanel.setSpacing(true);
        
        totalSpan.addClassNames("text-4xl", "font-bold", "text-primary", "block", "mb-4", "text-right");
        
        Button payButton = new Button("COBRAR", e -> openPaymentDialog());
        payButton.addClassNames("bg-green-600", "text-white", "rounded-lg", "shadow", "hover:bg-green-700", "font-bold", "text-2xl", "py-6");
        payButton.setWidthFull();
        payButton.setHeight("100px");
        
        Button clearButton = new Button("Limpiar", e -> clearCart());
        clearButton.addClassNames("bg-red-100", "text-red-600", "rounded-lg", "hover:bg-red-200", "font-medium");
        clearButton.setWidthFull();

        rightPanel.add(new Span("Total a Pagar:"), totalSpan, payButton, clearButton);
        
        mainLayout.add(leftPanel, rightPanel);
        mainLayout.setFlexGrow(1, leftPanel);
        add(mainLayout);
    }

    private void configureControls() {
        clienteSelect.setLabel(getTranslation("view.pos.client"));
        clienteSelect.setItems(posService.findAllClientes());
        clienteSelect.setItemLabelGenerator(c -> c.getEntidad() != null ? c.getEntidad().getNombreCompleto() : "Cliente " + c.getId());

        almacenSelect.setLabel("Almacén de Salida");
        almacenSelect.setItems(almacenRepository.findAll());
        almacenSelect.setItemLabelGenerator(InvAlmacen::getNombre);

        productoSelect.setLabel(getTranslation("view.pos.search_product"));
        productoSelect.setItems(productoService.findAll());
        productoSelect.setItemLabelGenerator(InvProducto::getNombre);
        productoSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                checkAndAddToCart(e.getValue());
                productoSelect.clear();
            }
        });
        
        totalSpan.setText(getTranslation("view.pos.total") + " $0.00");
    }

    private void configureGrid() {
        cartGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        
        cartGrid.addColumn(d -> {
            if (d.getProductoVariante() != null) {
               return d.getProductoVariante().getProducto().getNombre() + 
                      (d.getProductoVariante().getNombreVariante().equals(d.getProductoVariante().getProducto().getNombre()) ? "" : " - " + d.getProductoVariante().getNombreVariante());
            }
            return "Producto";
        }).setHeader("Producto").setFlexGrow(2); 
        
        cartGrid.addColumn(new ComponentRenderer<>(item -> {
            NumberField quantityField = new NumberField();
            quantityField.setValue(item.getCantidad() != null ? item.getCantidad().doubleValue() : 1.0);
            quantityField.setMin(1);
            quantityField.setStep(1);
            quantityField.setWidth("80px");
            quantityField.addValueChangeListener(e -> {
                if (e.getValue() != null && e.getValue() > 0) {
                     item.setCantidad(BigDecimal.valueOf(e.getValue()));
                     updateItemSubtotal(item);
                     cartGrid.getDataProvider().refreshItem(item); 
                     refreshTotals();
                }
            });
            return quantityField;
        })).setHeader("Cant").setWidth("100px").setFlexGrow(0);

        cartGrid.addColumn(d -> d.getPrecioUnitario() != null ? "$" + d.getPrecioUnitario() : "$0.00")
                .setHeader("Precio").setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END);
        
        cartGrid.addColumn(d -> d.getSubtotal() != null ? "$" + d.getSubtotal() : "$0.00")
                .setHeader("Subtotal").setKey("subtotal").setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END);
        
        cartGrid.addComponentColumn(item -> {
            Button remove = new Button(VaadinIcon.CLOSE_SMALL.create(), e -> removeFromCart(item));
            remove.addClassNames("bg-red-500", "text-white", "rounded", "px-2", "py-1", "hover:bg-red-600", "text-xs");
            return remove;
        }).setWidth("60px").setFlexGrow(0);
    }

    private void checkAndAddToCart(InvProducto product) {
        List<InvProductoVariante> variants = productoService.findVariantesByProducto(product);
        if (variants.isEmpty()) {
            TailwindNotification.show("Error: Producto sin variantes.", TailwindNotification.Type.ERROR);
            return;
        }

        if (variants.size() == 1) {
            addVariantToCart(variants.get(0));
        } else {
            showVariantSelectionDialog(variants);
        }
    }
    
    private void showVariantSelectionDialog(List<InvProductoVariante> variants) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Seleccionar Variante");
        
        Grid<InvProductoVariante> grid = new Grid<>();
        grid.setItems(variants);
        grid.addColumn(InvProductoVariante::getNombreVariante).setHeader("Variante");
        grid.addColumn(v -> productoService.getStockTotal(v)).setHeader("Stock");
        grid.addColumn(v -> "$" + productoService.getPrecioVentaActual(v)).setHeader("Precio");
        
        grid.addItemClickListener(e -> {
            addVariantToCart(e.getItem());
            dialog.close();
        });
        
        dialog.add(grid);
        dialog.setWidth("600px");
        dialog.open();
    }

    private void addVariantToCart(InvProductoVariante variant) {
        BigDecimal stock = productoService.getStockTotal(variant);
        BigDecimal price = productoService.getPrecioVentaActual(variant);
        
        // Stock Validation
        if (stock.compareTo(BigDecimal.ZERO) <= 0) {
            TailwindNotification.show("¡Stock insuficiente! Disponible: " + stock, TailwindNotification.Type.ERROR);
            return; 
        }
        
        // Find if already in cart
        Optional<PosVentaDetalle> existing = cartItems.stream()
                .filter(d -> d.getProductoVariante().getId().equals(variant.getId()))
                .findFirst();
        
        if (existing.isPresent()) {
            PosVentaDetalle item = existing.get();
            if (item.getCantidad().add(BigDecimal.ONE).compareTo(stock) > 0) {
                 TailwindNotification.show("No hay más stock disponible", TailwindNotification.Type.WARNING);
                 return;
            }
            item.setCantidad(item.getCantidad().add(BigDecimal.ONE));
            updateItemSubtotal(item);
        } else {
            PosVentaDetalle item = new PosVentaDetalle();
            item.setProductoVariante(variant);
            item.setCantidad(BigDecimal.ONE);
            item.setPrecioUnitario(price);
            updateItemSubtotal(item);
            cartItems.add(item);
        }
        
        refreshGrid();
    }
    
    private void updateItemSubtotal(PosVentaDetalle item) {
        BigDecimal qty = item.getCantidad();
        BigDecimal price = item.getPrecioUnitario();
        item.setSubtotal(price.multiply(qty));
        item.setImpuestosMonto(BigDecimal.ZERO); // Standard 0 for now. Calculation logic would be here.
        item.setDescuentoMonto(BigDecimal.ZERO);
    }
    
    private void removeFromCart(PosVentaDetalle item) {
        cartItems.remove(item);
        refreshGrid();
    }
    
    private void clearCart() {
        cartItems.clear();
        refreshGrid();
        clienteSelect.clear();
    }
    
    private void refreshGrid() {
        cartGrid.setItems(cartItems);
        refreshTotals();
    }
    
    private void refreshTotals() {
        BigDecimal total = cartItems.stream()
                .map(PosVentaDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalSpan.setText("$" + total.toString());
    }

    private void openPaymentDialog() {
        if (cartItems.isEmpty()) {
            TailwindNotification.show("El carrito está vacío", TailwindNotification.Type.WARNING);
            return;
        }
        if (almacenSelect.getValue() == null) {
            TailwindNotification.show("Seleccione un almacén de salida", TailwindNotification.Type.WARNING);
            return;
        }

        TailwindModal modal = new TailwindModal("Procesar Pago");
        modal.setWidth("400px");
        
        BigDecimal total = cartItems.stream().map(PosVentaDetalle::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Span amount = new Span("$" + total);
        amount.addClassNames("text-4xl", "font-bold", "text-center", "block", "mb-6");
        
        ComboBox<String> paymentMethod = new ComboBox<>("Método de Pago");
        paymentMethod.setItems("EFECTIVO", "TARJETA", "TRANSFERENCIA");
        paymentMethod.setValue("EFECTIVO");
        paymentMethod.setWidthFull();
        
        Button confirmBtn = new Button("CONFIRMAR PAGO", e -> {
            processSale(paymentMethod.getValue());
            modal.close();
        });
        confirmBtn.addClassNames("bg-green-600", "text-white", "w-full", "py-4", "text-xl", "font-bold", "mt-4", "rounded-lg");
        
        modal.addContent(new VerticalLayout(amount, paymentMethod, confirmBtn));
        modal.open();
    }

    private void processSale(String metodoPago) {
        try {
            PosVenta venta = new PosVenta();
            venta.setCliente(clienteSelect.getValue());
            
            // User Assignment
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            SysUsuario currentUser = userService.findByUsername(username);
            venta.setUsuarioVendedor(currentUser);

            venta.setTotalBruto(cartItems.stream().map(PosVentaDetalle::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
            venta.setTotalNeto(venta.getTotalBruto());
            venta.setImpuestosTotal(BigDecimal.ZERO);
            venta.setDescuentoTotal(BigDecimal.ZERO);
            venta.setEstado("CERRADO");
            venta.setEstadoPago("PAGADO - " + metodoPago); // Saving Payment Method here
            venta.setFechaHora(java.time.LocalDateTime.now());
            venta.setAlmacenSalida(almacenSelect.getValue());
            
            // Populate Details
            for (PosVentaDetalle item : cartItems) {
                PosVentaDetalle detail = new PosVentaDetalle();
                detail.setProductoVariante(item.getProductoVariante());
                detail.setCantidad(item.getCantidad());
                detail.setPrecioUnitario(item.getPrecioUnitario());
                detail.setSubtotal(item.getSubtotal());
                detail.setImpuestosMonto(item.getImpuestosMonto());
                venta.addDetalle(detail);
            }
            
            posService.saveVenta(venta);
            TailwindNotification.show("Venta procesada correctamente", TailwindNotification.Type.SUCCESS);
            clearCart();
            
        } catch (Exception e) {
            TailwindNotification.show("Error al guardar venta: " + e.getMessage(), TailwindNotification.Type.ERROR);
            e.printStackTrace();
        }
    }
}
