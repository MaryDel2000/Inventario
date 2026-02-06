package com.mariastaff.Inventario.ui.views.sales;

import com.mariastaff.Inventario.backend.data.entity.PosVenta;
import com.mariastaff.Inventario.backend.service.PosService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.mariastaff.Inventario.backend.data.entity.PosPago;
import java.math.BigDecimal;
import jakarta.annotation.security.PermitAll;

@PageTitle("Cuentas por Cobrar | Ventas")
@Route(value = "sales/receivables", layout = MainLayout.class)
@PermitAll
public class ReceivablesView extends VerticalLayout {

    private final PosService service;
    private final Grid<PosVenta> grid = new Grid<>(PosVenta.class);

    public ReceivablesView(PosService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");

        configureGrid();

        add(grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames("bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns();

        grid.addColumn(PosVenta::getNumeroDocumento).setHeader(getTranslation("grid.header.document"));
        grid.addColumn(PosVenta::getFechaHora).setHeader(getTranslation("grid.header.date"));

        // Total Amount
        grid.addColumn(v -> v.getTotalNeto()).setHeader("Total").setAutoWidth(true);

        // Paid Amount
        grid.addColumn(v -> {
            BigDecimal paid = v.getPagos() != null ? v.getPagos().stream()
                    .map(PosPago::getMontoTotal)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO;
            return paid;
        }).setHeader("Pagado").setAutoWidth(true);

        // Pending Amount
        grid.addColumn(v -> {
            BigDecimal total = v.getTotalNeto() != null ? v.getTotalNeto() : BigDecimal.ZERO;
            BigDecimal paid = v.getPagos() != null ? v.getPagos().stream()
                    .map(PosPago::getMontoTotal)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO;
            return total.subtract(paid);
        }).setHeader("Pendiente").setAutoWidth(true);

        grid.addColumn(v -> {
            BigDecimal total = v.getTotalNeto() != null ? v.getTotalNeto() : BigDecimal.ZERO;
            BigDecimal paid = v.getPagos() != null ? v.getPagos().stream()
                    .map(PosPago::getMontoTotal)
                    .filter(java.util.Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO;
            BigDecimal pending = total.subtract(paid);

            if (pending.compareTo(BigDecimal.ZERO) <= 0) {
                return "COMPLETO";
            }
            return v.getEstadoPago();
        }).setHeader(getTranslation("grid.header.status"));

        grid.addColumn(v -> v.getCliente() != null ? v.getCliente().getEntidad().getNombreCompleto()
                : getTranslation("client.anonymous"))
                .setHeader(getTranslation("grid.header.client"));

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findVentasPorCobrar());
    }
}
