package com.mariastaff.Inventario.ui.views.inventory;

import com.mariastaff.Inventario.backend.data.entity.InvMovimiento;
import com.mariastaff.Inventario.backend.service.MovimientoService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Movimientos de Stock | Inventario")
@Route(value = "inventory/movements", layout = MainLayout.class)
@PermitAll
public class MovementsView extends VerticalLayout {

        private final MovimientoService service;
        private final com.mariastaff.Inventario.backend.service.ProductoService productoService;
        private final com.mariastaff.Inventario.backend.service.AlmacenService almacenService;
        private final Grid<InvMovimiento> grid = new Grid<>(InvMovimiento.class);

        public MovementsView(MovimientoService service,
                        com.mariastaff.Inventario.backend.service.ProductoService productoService,
                        com.mariastaff.Inventario.backend.service.AlmacenService almacenService) {
                this.service = service;
                this.productoService = productoService;
                this.almacenService = almacenService;

                addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");

                configureGrid();

                com.vaadin.flow.component.button.Button addBtn = new com.vaadin.flow.component.button.Button(
                                getTranslation("view.movements.action.new"),
                                com.vaadin.flow.component.icon.VaadinIcon.PLUS.create());
                addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4",
                                "rounded-lg",
                                "shadow", "hover:shadow-md", "transition-all");
                addBtn.addClickListener(e -> openMovementDialog());

                com.vaadin.flow.component.orderedlayout.HorizontalLayout header = new com.vaadin.flow.component.orderedlayout.HorizontalLayout(
                                new AppLabel("view.movements.title"), addBtn);
                header.addClassNames("w-full", "justify-between", "items-center");

                add(header, grid);
                updateList();
        }

        private void configureGrid() {
                grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
                grid.setSizeFull();
                grid.removeAllColumns();

                grid.addColumn(InvMovimiento::getTipoMovimiento).setHeader(getTranslation("view.movements.grid.type"))
                                .setAutoWidth(true);
                grid.addColumn(InvMovimiento::getFechaMovimiento).setHeader(getTranslation("view.movements.grid.date"))
                                .setAutoWidth(true);
                grid.addColumn(InvMovimiento::getObservaciones)
                                .setHeader(getTranslation("view.movements.grid.observations"))
                                .setAutoWidth(true);
                grid.addColumn(m -> m.getAlmacenOrigen() != null ? m.getAlmacenOrigen().getNombre() : "-")
                                .setHeader(getTranslation("view.movements.grid.source"));
                grid.addColumn(m -> m.getAlmacenDestino() != null ? m.getAlmacenDestino().getNombre() : "-")
                                .setHeader(getTranslation("view.movements.grid.target"));
                grid.getColumns().forEach(col -> col.setAutoWidth(true));

                grid.setItemDetailsRenderer(new com.vaadin.flow.data.renderer.ComponentRenderer<>(movimiento -> {
                        com.vaadin.flow.component.grid.Grid<com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle> detailsGrid = new com.vaadin.flow.component.grid.Grid<>();
                        detailsGrid
                                        .addColumn(d -> d.getProductoVariante().getProducto().getNombre() + " - "
                                                        + d.getProductoVariante().getNombreVariante())
                                        .setHeader(getTranslation("grid.header.product"));
                        detailsGrid.addColumn(d -> d.getLote() != null ? d.getLote().getCodigoLote() : "-")
                                        .setHeader(getTranslation("grid.header.batch")).setAutoWidth(true);
                        detailsGrid.addColumn(
                                        d -> d.getUbicacionOrigen() != null ? d.getUbicacionOrigen().getCodigo() : "-")
                                        .setHeader(getTranslation("grid.header.location.origin")).setAutoWidth(true);
                        detailsGrid.addColumn(d -> d.getUbicacionDestino() != null ? d.getUbicacionDestino().getCodigo()
                                        : "-")
                                        .setHeader(getTranslation("grid.header.location.destination"))
                                        .setAutoWidth(true);
                        detailsGrid.addColumn(
                                        com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle::getCantidad)
                                        .setHeader(getTranslation("grid.header.quantity")).setAutoWidth(true);

                        // Set Items from Service
                        detailsGrid.setItems(service.findDetallesByMovimiento(movimiento));
                        detailsGrid.addThemeVariants(com.vaadin.flow.component.grid.GridVariant.LUMO_COMPACT,
                                        com.vaadin.flow.component.grid.GridVariant.LUMO_NO_BORDER);

                        // Layout to wrap
                        com.vaadin.flow.component.orderedlayout.VerticalLayout layout = new com.vaadin.flow.component.orderedlayout.VerticalLayout(
                                        detailsGrid);
                        layout.setPadding(true);
                        layout.setSpacing(false);
                        return layout;
                }));
        }

        private void updateList() {
                grid.setItems(service.findAllMovimientos());
        }

        private void openMovementDialog() {
                com.mariastaff.Inventario.ui.components.base.TailwindModal modal = new com.mariastaff.Inventario.ui.components.base.TailwindModal(
                                getTranslation("view.movements.editor.title"));
                modal.setDialogMaxWidth("max-w-4xl");

                InvMovimiento movimiento = new InvMovimiento();
                java.util.List<com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle> detalles = new java.util.ArrayList<>();

                com.vaadin.flow.component.formlayout.FormLayout headerForm = new com.vaadin.flow.component.formlayout.FormLayout();
                headerForm.addClassName("w-full");

                com.vaadin.flow.component.combobox.ComboBox<String> tipo = new com.vaadin.flow.component.combobox.ComboBox<>(
                                getTranslation("view.movements.field.type"));
                tipo.setItems("ENTRADA", "SALIDA", "TRASPASO");
                tipo.addClassName("w-full");

                com.vaadin.flow.component.combobox.ComboBox<com.mariastaff.Inventario.backend.data.entity.InvAlmacen> origen = new com.vaadin.flow.component.combobox.ComboBox<>(
                                getTranslation("view.movements.field.source_wh"));
                origen.setItems(almacenService.findAllAlmacenes());
                origen.setItemLabelGenerator(com.mariastaff.Inventario.backend.data.entity.InvAlmacen::getNombre);
                origen.addClassName("w-full");

                com.vaadin.flow.component.combobox.ComboBox<com.mariastaff.Inventario.backend.data.entity.InvAlmacen> destino = new com.vaadin.flow.component.combobox.ComboBox<>(
                                getTranslation("view.movements.field.target_wh"));
                destino.setItems(almacenService.findAllAlmacenes());
                destino.setItemLabelGenerator(com.mariastaff.Inventario.backend.data.entity.InvAlmacen::getNombre);
                destino.addClassName("w-full");

                com.vaadin.flow.component.textfield.TextArea observaciones = new com.vaadin.flow.component.textfield.TextArea(
                                getTranslation("field.observations"));
                observaciones.addClassName("w-full");

                headerForm.add(tipo, origen, destino, observaciones);

                // Define components FIRST
                com.vaadin.flow.component.combobox.ComboBox<com.mariastaff.Inventario.backend.data.entity.InvLote> batchSelector = new com.vaadin.flow.component.combobox.ComboBox<>(
                                getTranslation("field.batch"));
                batchSelector.setWidth("150px");
                batchSelector.setItemLabelGenerator(
                                com.mariastaff.Inventario.backend.data.entity.InvLote::getCodigoLote);

                com.vaadin.flow.component.combobox.ComboBox<com.mariastaff.Inventario.backend.data.entity.InvUbicacion> sourceLocSelector = new com.vaadin.flow.component.combobox.ComboBox<>(
                                getTranslation("field.location.origin"));
                sourceLocSelector.setWidth("180px");
                sourceLocSelector.setItemLabelGenerator(u -> u.getCodigo());

                com.vaadin.flow.component.combobox.ComboBox<com.mariastaff.Inventario.backend.data.entity.InvUbicacion> targetLocSelector = new com.vaadin.flow.component.combobox.ComboBox<>(
                                getTranslation("field.location.destination"));
                targetLocSelector.setWidth("180px");
                targetLocSelector.setItemLabelGenerator(u -> u.getCodigo());

                com.vaadin.flow.component.combobox.ComboBox<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> productoSelector = new com.vaadin.flow.component.combobox.ComboBox<>(
                                getTranslation("field.product.variant"));
                java.util.List<com.mariastaff.Inventario.backend.data.entity.InvProductoVariante> allVariants = new java.util.ArrayList<>();
                productoService.findAll().forEach(p -> allVariants.addAll(productoService.findVariantesByProducto(p)));
                productoSelector.setItems(allVariants);
                productoSelector.setItemLabelGenerator(
                                v -> v.getProducto().getNombre() + " - " + v.getNombreVariante());
                productoSelector.setWidthFull();

                com.vaadin.flow.component.textfield.BigDecimalField cantidadField = new com.vaadin.flow.component.textfield.BigDecimalField(
                                getTranslation("field.quantity"));
                cantidadField.setWidth("150px");

                // Logic for visibility
                origen.addValueChangeListener(e -> {
                        if (e.getValue() != null) {
                                java.util.List<com.mariastaff.Inventario.backend.data.entity.InvUbicacion> locs = almacenService
                                                .findAllUbicaciones().stream()
                                                .filter(u -> u.getAlmacen() != null
                                                                && u.getAlmacen().getId().equals(e.getValue().getId()))
                                                .collect(java.util.stream.Collectors.toList());
                                sourceLocSelector.setItems(locs);
                        } else {
                                sourceLocSelector.clear();
                                sourceLocSelector.setItems(java.util.Collections.emptyList());
                        }
                });

                destino.addValueChangeListener(e -> {
                        if (e.getValue() != null) {
                                java.util.List<com.mariastaff.Inventario.backend.data.entity.InvUbicacion> locs = almacenService
                                                .findAllUbicaciones().stream()
                                                .filter(u -> u.getAlmacen() != null
                                                                && u.getAlmacen().getId().equals(e.getValue().getId()))
                                                .collect(java.util.stream.Collectors.toList());
                                targetLocSelector.setItems(locs);
                        } else {
                                targetLocSelector.clear();
                                targetLocSelector.setItems(java.util.Collections.emptyList());
                        }
                });

                // Update Batch Selector when Product changes
                productoSelector.addValueChangeListener(e -> {
                        if (e.getValue() != null) {
                                batchSelector.setItems(almacenService.findLotesByVariante(e.getValue()));
                        } else {
                                batchSelector.clear();
                                batchSelector.setItems(java.util.Collections.emptyList());
                        }
                });

                // Visibility Logic for Details
                tipo.addValueChangeListener(e -> {
                        boolean isTraspaso = "TRASPASO".equals(e.getValue());
                        boolean isEntrada = "ENTRADA".equals(e.getValue());
                        boolean isSalida = "SALIDA".equals(e.getValue());

                        origen.setVisible(isTraspaso || isSalida);
                        destino.setVisible(isTraspaso || isEntrada);

                        sourceLocSelector.setVisible(isTraspaso || isSalida);
                        targetLocSelector.setVisible(isTraspaso || isEntrada);
                });

                // Details Grid
                Grid<com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle> detailsGrid = new Grid<>();
                detailsGrid.addClassNames("border", "border-gray-200", "rounded", "mt-4");
                detailsGrid.setHeight("200px");

                detailsGrid.addColumn(d -> d.getProductoVariante().getNombreVariante())
                                .setHeader(getTranslation("grid.header.product")).setAutoWidth(true);
                detailsGrid.addColumn(d -> d.getLote() != null ? d.getLote().getCodigoLote() : "-")
                                .setHeader(getTranslation("grid.header.batch")).setAutoWidth(true);
                detailsGrid.addColumn(d -> d.getUbicacionOrigen() != null ? d.getUbicacionOrigen().getCodigo() : "-")
                                .setHeader(getTranslation("grid.header.location.origin")).setAutoWidth(true);
                detailsGrid.addColumn(d -> d.getUbicacionDestino() != null ? d.getUbicacionDestino().getCodigo() : "-")
                                .setHeader(getTranslation("grid.header.location.destination")).setAutoWidth(true);
                detailsGrid.addColumn(com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle::getCantidad)
                                .setHeader(getTranslation("grid.header.quantity")).setAutoWidth(true);

                detailsGrid.addComponentColumn(d -> {
                        com.vaadin.flow.component.button.Button remove = new com.vaadin.flow.component.button.Button(
                                        com.vaadin.flow.component.icon.VaadinIcon.TRASH.create());
                        remove.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_ERROR,
                                        com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
                        remove.addClickListener(ev -> {
                                detalles.remove(d);
                                detailsGrid.setItems(detalles);
                        });
                        return remove;
                });

                com.vaadin.flow.component.button.Button addDetailBtn = new com.vaadin.flow.component.button.Button(
                                getTranslation("action.add"), e -> {
                                        if (productoSelector.getValue() != null && cantidadField.getValue() != null) {
                                                com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle detalle = new com.mariastaff.Inventario.backend.data.entity.InvMovimientoDetalle();
                                                detalle.setProductoVariante(productoSelector.getValue());
                                                detalle.setCantidad(cantidadField.getValue());
                                                detalle.setLote(batchSelector.getValue());
                                                detalle.setUbicacionOrigen(sourceLocSelector.isVisible()
                                                                ? sourceLocSelector.getValue()
                                                                : null);
                                                detalle.setUbicacionDestino(
                                                                targetLocSelector.isVisible()
                                                                                ? targetLocSelector.getValue()
                                                                                : null);

                                                detalles.add(detalle);
                                                detailsGrid.setItems(detalles);

                                                productoSelector.clear();
                                                cantidadField.clear();
                                                batchSelector.clear();
                                                sourceLocSelector.clear();
                                                targetLocSelector.clear();
                                        }
                                });

                com.vaadin.flow.component.orderedlayout.HorizontalLayout addDetailLayout = new com.vaadin.flow.component.orderedlayout.HorizontalLayout();
                addDetailLayout.addClassNames("w-full", "items-end", "mt-4");

                addDetailLayout.add(productoSelector, batchSelector, sourceLocSelector, targetLocSelector,
                                cantidadField,
                                addDetailBtn);
                addDetailLayout.setFlexGrow(1, productoSelector);

                // Trigger initial visibility
                tipo.setValue("ENTRADA");

                com.vaadin.flow.component.orderedlayout.VerticalLayout content = new com.vaadin.flow.component.orderedlayout.VerticalLayout(
                                headerForm, addDetailLayout, detailsGrid);
                content.setPadding(false);
                modal.addContent(content);

                com.vaadin.flow.component.button.Button saveButton = new com.vaadin.flow.component.button.Button(
                                getTranslation("view.movements.action.save"), e -> {
                                        if (tipo.getValue() == null) {
                                                com.mariastaff.Inventario.ui.components.base.TailwindNotification.show(
                                                                getTranslation("view.movements.error.select_type"),
                                                                com.mariastaff.Inventario.ui.components.base.TailwindNotification.Type.ERROR);
                                                return;
                                        }
                                        if (detalles.isEmpty()) {
                                                com.mariastaff.Inventario.ui.components.base.TailwindNotification.show(
                                                                getTranslation("view.movements.error.add_details"),
                                                                com.mariastaff.Inventario.ui.components.base.TailwindNotification.Type.ERROR);
                                                return;
                                        }

                                        movimiento.setTipoMovimiento(tipo.getValue());
                                        movimiento.setAlmacenOrigen(origen.getValue());
                                        movimiento.setAlmacenDestino(destino.getValue());
                                        movimiento.setObservaciones(observaciones.getValue());
                                        movimiento.setFechaMovimiento(java.time.LocalDateTime.now());

                                        try {
                                                service.crearMovimiento(movimiento, detalles);
                                                updateList();
                                                modal.close();
                                                com.mariastaff.Inventario.ui.components.base.TailwindNotification.show(
                                                                getTranslation("view.movements.msg.saved"),
                                                                com.mariastaff.Inventario.ui.components.base.TailwindNotification.Type.SUCCESS);
                                        } catch (Exception ex) {
                                                com.mariastaff.Inventario.ui.components.base.TailwindNotification.show(
                                                                getTranslation("msg.error.save"),
                                                                com.mariastaff.Inventario.ui.components.base.TailwindNotification.Type.ERROR);
                                                ex.printStackTrace();
                                        }
                                });
                saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg",
                                "shadow");

                com.vaadin.flow.component.button.Button cancelBtn = new com.vaadin.flow.component.button.Button(
                                getTranslation("action.cancel"), e -> modal.close());
                cancelBtn.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]",
                                "font-medium",
                                "py-2", "px-4", "rounded-lg");

                modal.addFooterButton(cancelBtn);
                modal.addFooterButton(saveButton);

                add(modal);
                modal.open();
        }
}
