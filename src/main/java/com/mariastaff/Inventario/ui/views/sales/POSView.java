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
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
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
    private final com.mariastaff.Inventario.backend.data.repository.GenMonedaTasaRepository monedaTasaRepository;
    private final com.mariastaff.Inventario.backend.data.repository.GenEntidadRepository genEntidadRepository;
    private final UserService userService;

    private final ComboBox<PosCliente> clienteSelect = new ComboBox<>();
    private final ComboBox<InvAlmacen> almacenSelect = new ComboBox<>();
    private final ComboBox<InvProducto> productoSelect = new ComboBox<>();
    private final Grid<PosVentaDetalle> cartGrid = new Grid<>();
    private final List<PosVentaDetalle> cartItems = new ArrayList<>();
    private final Span totalSpan = new Span();
    private final Span totalNioSpan = new Span();

    public POSView(PosService posService, ProductoService productoService,
            com.mariastaff.Inventario.backend.data.repository.InvAlmacenRepository almacenRepository,
            com.mariastaff.Inventario.backend.data.repository.GenMonedaTasaRepository monedaTasaRepository,
            com.mariastaff.Inventario.backend.data.repository.GenEntidadRepository genEntidadRepository,
            UserService userService) {
        this.posService = posService;
        this.productoService = productoService;
        this.almacenRepository = almacenRepository;
        this.monedaTasaRepository = monedaTasaRepository;
        this.genEntidadRepository = genEntidadRepository;
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
        if (!almacenes.isEmpty())
            almacenSelect.setValue(almacenes.get(0));

        leftPanel.add(topControls, cartGrid);

        // Right Panel: Totals & Actions
        VerticalLayout rightPanel = new VerticalLayout();
        rightPanel.addClassNames("bg-bg-surface", "rounded-lg", "shadow", "p-6", "border", "border-border");
        rightPanel.setWidth("350px");
        rightPanel.setSpacing(true);

        totalSpan.addClassNames("text-4xl", "font-bold", "text-primary", "block", "text-right");
        totalNioSpan.addClassNames("text-2xl", "font-semibold", "text-secondary", "block", "mb-4", "text-right"); // Secondary
                                                                                                                  // color,
                                                                                                                  // smaller

        Button payButton = new Button(getTranslation("view.pos.action.pay"), e -> openPaymentDialog());
        payButton.addClassNames("bg-green-600", "text-white", "rounded-lg", "shadow", "hover:bg-green-700", "font-bold",
                "text-2xl", "py-6");
        payButton.setWidthFull();
        payButton.setHeight("100px");

        Button clearButton = new Button(getTranslation("view.pos.action.clear"), e -> clearCart());
        clearButton.addClassNames("bg-red-100", "text-red-600", "rounded-lg", "hover:bg-red-200", "font-medium");
        clearButton.setWidthFull();

        rightPanel.add(new Span(getTranslation("view.pos.total_pay")), totalSpan, totalNioSpan, payButton, clearButton);

        mainLayout.add(leftPanel, rightPanel);
        mainLayout.setFlexGrow(1, leftPanel);
        add(mainLayout);
    }

    private void configureControls() {
        clienteSelect.setLabel(getTranslation("view.pos.client"));
        clienteSelect.setItems(posService.findAllClientes());
        clienteSelect.setItemLabelGenerator(
                c -> c.getEntidad() != null ? c.getEntidad().getNombreCompleto() : "Cliente " + c.getId());
        clienteSelect.setAllowCustomValue(true);
        clienteSelect.addCustomValueSetListener(e -> {
            String name = e.getDetail();
            if (name == null || name.trim().isEmpty())
                return;

            try {
                com.mariastaff.Inventario.backend.data.entity.GenEntidad entidad = new com.mariastaff.Inventario.backend.data.entity.GenEntidad();
                entidad.setNombreCompleto(name);
                entidad.setTipoEntidad("PERSONA");
                entidad = genEntidadRepository.save(entidad);

                PosCliente cliente = new PosCliente();
                cliente.setEntidad(entidad);
                cliente = posService.saveCliente(cliente);

                clienteSelect.setItems(posService.findAllClientes());
                clienteSelect.setValue(cliente);

                TailwindNotification.show(getTranslation("view.pos.msg.client.registered"),
                        TailwindNotification.Type.SUCCESS);
            } catch (Exception ex) {
                TailwindNotification.show(getTranslation("view.pos.msg.client.error") + ex.getMessage(),
                        TailwindNotification.Type.ERROR);
            }
        });

        almacenSelect.setLabel(getTranslation("view.pos.warehouse.select"));
        almacenSelect.setItems(almacenRepository.findAll());
        almacenSelect.setItemLabelGenerator(InvAlmacen::getNombre);

        productoSelect.setLabel(getTranslation("view.pos.search_product"));
        productoSelect.setItems(productoService.search(null, null, true));
        productoSelect.setItemLabelGenerator(InvProducto::getNombre);
        productoSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                checkAndAddToCart(e.getValue());
                productoSelect.clear();
            }
        });

        totalSpan.setText("$0.00");
        totalNioSpan.setText("C$0.00");
    }

    private void configureGrid() {
        cartGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        cartGrid.addColumn(d -> {
            if (d.getProductoVariante() != null) {
                return d.getProductoVariante().getProducto().getNombre() +
                        (d.getProductoVariante().getNombreVariante()
                                .equals(d.getProductoVariante().getProducto().getNombre()) ? ""
                                        : " - " + d.getProductoVariante().getNombreVariante());
            }
            return getTranslation("view.pos.grid.product");
        }).setHeader(getTranslation("view.pos.grid.product")).setFlexGrow(2);

        cartGrid.addColumn(createQuantityFieldRenderer())
                .setHeader(getTranslation("view.pos.grid.qty")).setWidth("100px").setFlexGrow(0);

        cartGrid.addColumn(d -> d.getPrecioUnitario() != null ? "$" + d.getPrecioUnitario() : "$0.00")
                .setHeader(getTranslation("view.pos.grid.price"))
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END);

        cartGrid.addColumn(d -> d.getSubtotal() != null ? "$" + d.getSubtotal() : "$0.00")
                .setHeader(getTranslation("view.pos.grid.subtotal")).setKey("subtotal")
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END);

        cartGrid.addComponentColumn(item -> {
            Button remove = new Button(VaadinIcon.TRASH.create(), e -> removeFromCart(item));
            remove.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            remove.addClassName("bg-white"); // Optional contrast if needed, but native error variant is cleaner
            return remove;
        }).setWidth("60px").setFlexGrow(0).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
    }

    private void checkAndAddToCart(InvProducto product) {
        List<InvProductoVariante> variants = productoService.findVariantesByProducto(product);
        if (variants.isEmpty()) {
            TailwindNotification.show(getTranslation("msg.error.no_variants"), TailwindNotification.Type.ERROR);
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
        dialog.setHeaderTitle(getTranslation("dialog.select_variant.title"));

        Grid<InvProductoVariante> grid = new Grid<>();
        grid.setItems(variants);
        grid.addColumn(InvProductoVariante::getNombreVariante).setHeader(getTranslation("grid.header.variant"));
        grid.addColumn(v -> productoService.getStockTotal(v)).setHeader(getTranslation("grid.header.stock"));
        grid.addColumn(v -> "$" + productoService.getPrecioVentaActual(v, "USD"))
                .setHeader(getTranslation("grid.header.price"));

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
        BigDecimal price = productoService.getPrecioVentaActual(variant, "USD");

        // Stock Validation
        // Stock Validation
        if (stock.compareTo(BigDecimal.ZERO) <= 0) {
            TailwindNotification.show(getTranslation("view.pos.msg.stock_insufficient") + stock,
                    TailwindNotification.Type.ERROR);
            return;
        }

        // Find if already in cart
        Optional<PosVentaDetalle> existing = cartItems.stream()
                .filter(d -> d.getProductoVariante().getId().equals(variant.getId()))
                .findFirst();

        if (existing.isPresent()) {
            PosVentaDetalle item = existing.get();
            if (item.getCantidad().add(BigDecimal.ONE).compareTo(stock) > 0) {
                TailwindNotification.show(getTranslation("view.pos.msg.stock_limit"),
                        TailwindNotification.Type.WARNING);
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
        BigDecimal totalUSD = cartItems.stream()
                .map(PosVentaDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalSpan.setText("$" + totalUSD.toString());

        // Calculate Cordobas
        BigDecimal tasa = getTasaCambioUSDtoNIO();
        BigDecimal totalNIO = totalUSD.multiply(tasa).setScale(2, java.math.RoundingMode.HALF_UP);
        totalNioSpan.setText("C$" + totalNIO.toString());
    }

    private BigDecimal getTasaCambioUSDtoNIO() {
        return monedaTasaRepository
                .findTopByMonedaOrigenCodigoAndMonedaDestinoCodigoOrderByFechaActualizacionDesc("USD", "NIO")
                .map(com.mariastaff.Inventario.backend.data.entity.GenMonedaTasa::getTasaConversion)
                .orElse(new BigDecimal("36.62")); // Fallback default rate if not set in DB
    }

    private ComponentRenderer<NumberField, PosVentaDetalle> createQuantityFieldRenderer() {
        return new ComponentRenderer<>(item -> {
            NumberField quantityField = new NumberField();
            quantityField.setValueChangeMode(ValueChangeMode.EAGER);
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
        });
    }

    private void openPaymentDialog() {
        if (cartItems.isEmpty()) {
            TailwindNotification.show(getTranslation("view.pos.msg.empty_cart"), TailwindNotification.Type.WARNING);
            return;
        }
        if (almacenSelect.getValue() == null) {
            TailwindNotification.show(getTranslation("view.pos.msg.warehouse_select"),
                    TailwindNotification.Type.WARNING);
            return;
        }

        TailwindModal modal = new TailwindModal(getTranslation("view.pos.pay.dialog.title"));
        modal.setWidth("400px");

        BigDecimal total = cartItems.stream().map(PosVentaDetalle::getSubtotal).reduce(BigDecimal.ZERO,
                BigDecimal::add);

        Span amount = new Span("$" + total);
        amount.addClassNames("text-4xl", "font-bold", "text-center", "block", "mb-6");

        ComboBox<String> paymentMethod = new ComboBox<>(getTranslation("view.pos.pay.method"));
        paymentMethod.setItems("EFECTIVO", "TARJETA", "TRANSFERENCIA");
        paymentMethod.setItemLabelGenerator(item -> switch (item) {
            case "EFECTIVO" -> getTranslation("view.pos.pay.method.cash");
            case "TARJETA" -> getTranslation("view.pos.pay.method.card");
            case "TRANSFERENCIA" -> getTranslation("view.pos.pay.method.transfer");
            default -> item;
        });
        paymentMethod.setValue("EFECTIVO");
        paymentMethod.setWidthFull();

        // Card Details Section (Hidden by default)
        VerticalLayout cardDetails = new VerticalLayout();
        cardDetails.setPadding(false);
        cardDetails.setSpacing(true);
        cardDetails.setVisible(false);

        com.vaadin.flow.component.textfield.TextField cardNumber = new com.vaadin.flow.component.textfield.TextField(
                getTranslation("view.pos.pay.card.number"));
        cardNumber.setWidthFull();
        cardNumber.setPlaceholder("0000 0000 0000 0000");

        HorizontalLayout cardExtra = new HorizontalLayout();
        cardExtra.setWidthFull();

        com.vaadin.flow.component.textfield.TextField cardHolder = new com.vaadin.flow.component.textfield.TextField(
                getTranslation("view.pos.pay.card.holder"));
        cardHolder.setWidthFull();

        com.vaadin.flow.component.textfield.TextField cardExpiry = new com.vaadin.flow.component.textfield.TextField(
                getTranslation("view.pos.pay.card.expiry"));
        cardExpiry.setWidth("150px");

        cardExtra.add(cardHolder, cardExpiry);
        cardDetails.add(cardNumber, cardExtra);

        // Transfer Details Section (Hidden by default)
        VerticalLayout transferDetails = new VerticalLayout();
        transferDetails.setPadding(false);
        transferDetails.setSpacing(true);
        transferDetails.setVisible(false);

        com.vaadin.flow.component.textfield.TextField transferReference = new com.vaadin.flow.component.textfield.TextField(
                getTranslation("view.pos.pay.transfer.ref"));
        transferReference.setWidthFull();

        com.vaadin.flow.component.textfield.TextField transferBank = new com.vaadin.flow.component.textfield.TextField(
                getTranslation("view.pos.pay.transfer.bank"));
        transferBank.setWidthFull();

        transferDetails.add(transferReference, transferBank);

        paymentMethod.addValueChangeListener(e -> {
            cardDetails.setVisible("TARJETA".equals(e.getValue()));
            transferDetails.setVisible("TRANSFERENCIA".equals(e.getValue()));
        });

        Button confirmBtn = new Button(getTranslation("view.pos.pay.confirm"), e -> {
            String methodKey = paymentMethod.getValue();
            String detailedMethod = methodKey;

            if ("TARJETA".equals(methodKey)) {
                if (cardNumber.isEmpty() || cardHolder.isEmpty()) {
                    TailwindNotification.show(getTranslation("view.pos.msg.card_data"),
                            TailwindNotification.Type.WARNING);
                    return;
                }
                String safeCard = cardNumber.getValue().length() > 4
                        ? cardNumber.getValue().substring(cardNumber.getValue().length() - 4)
                        : cardNumber.getValue();
                detailedMethod += " (Titular: " + cardHolder.getValue() + ", **** " + safeCard + ")";
            } else if ("TRANSFERENCIA".equals(methodKey)) {
                if (transferReference.isEmpty()) {
                    TailwindNotification.show(getTranslation("view.pos.msg.transfer_ref"),
                            TailwindNotification.Type.WARNING);
                    return;
                }
                String bankInfo = transferBank.isEmpty() ? "" : ", Banco: " + transferBank.getValue();
                detailedMethod += " (Ref: " + transferReference.getValue() + bankInfo + ")";
            }
            processSale(detailedMethod);
            modal.close();
        });
        confirmBtn.addClassNames("bg-green-600", "text-white", "flex-1", "py-4", "text-xl", "font-bold", "rounded-lg");

        Button cancelBtn = new Button(getTranslation("view.pos.pay.cancel"), e -> modal.close());
        cancelBtn.addClassNames("bg-gray-300", "text-gray-800", "flex-1", "py-4", "text-xl", "font-bold", "rounded-lg",
                "hover:bg-gray-400");

        HorizontalLayout buttonsLayout = new HorizontalLayout(cancelBtn, confirmBtn);
        buttonsLayout.setWidthFull();
        buttonsLayout.setSpacing(true);

        modal.addContent(new VerticalLayout(amount, paymentMethod, cardDetails, transferDetails, buttonsLayout));
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
            venta.setTurno(posService.findOpenShiftForUser(currentUser)); // Attempt to link current shift

            BigDecimal total = cartItems.stream().map(PosVentaDetalle::getSubtotal).reduce(BigDecimal.ZERO,
                    BigDecimal::add);
            venta.setTotalBruto(total);
            venta.setTotalNeto(total);
            venta.setImpuestosTotal(BigDecimal.ZERO);
            venta.setDescuentoTotal(BigDecimal.ZERO);
            venta.setEstado("CERRADO");
            venta.setEstadoPago("PAGADO");
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

            venta = posService.saveVenta(venta);

            // Create Payment
            com.mariastaff.Inventario.backend.data.entity.PosPago pago = new com.mariastaff.Inventario.backend.data.entity.PosPago();
            pago.setVenta(venta);
            pago.setMontoTotal(total);
            pago.setMontoRecibido(total); // Assuming exact payment for now in this simple POS
            pago.setMontoVuelto(BigDecimal.ZERO);
            pago.setFechaPago(java.time.LocalDateTime.now());
            pago.setUsuarioRegistro(currentUser);
            pago.setReferenciaPago(metodoPago);

            // We need a way to save Pago. Usually PosService should handle this cascade or
            // explicit save.
            // Since we don't have savePago exposed yet, we rely on CascadeType.ALL in
            // PosVenta.pagos if we add it there,
            // OR we update PosService to save payments.
            // Based on previous step, I added @OneToMany mappedBy="venta" but NOT cascade
            // ALL on 'pagos' field in PosVenta is dangerous if I don't save manually.
            // I'll add savePago to PosService first or assume I can use a repository.
            // But I cannot easily inject a new repo here without constructor changes.
            // Better: update PosService to include savePago.

            posService.savePago(pago);

            TailwindNotification.show(getTranslation("view.pos.msg.success"), TailwindNotification.Type.SUCCESS);
            clearCart();

        } catch (Exception e) {
            TailwindNotification.show(getTranslation("view.pos.msg.sale.error") + e.getMessage(),
                    TailwindNotification.Type.ERROR);
            e.printStackTrace();
        }
    }
}
