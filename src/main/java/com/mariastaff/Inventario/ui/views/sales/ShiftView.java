package com.mariastaff.Inventario.ui.views.sales;

import com.mariastaff.Inventario.backend.data.entity.PosTurno;
import com.mariastaff.Inventario.backend.service.PosService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.mariastaff.Inventario.ui.components.base.TailwindModal;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.mariastaff.Inventario.backend.data.entity.PosCaja;
import com.mariastaff.Inventario.backend.data.entity.SysUsuario;
import com.mariastaff.Inventario.backend.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.List;

@PageTitle("Turnos y Cierres | Ventas")
@Route(value = "sales/shifts", layout = MainLayout.class)
@PermitAll
public class ShiftView extends VerticalLayout {

    private final PosService service;
    private final UserService userService;
    private final Grid<PosTurno> grid = new Grid<>(PosTurno.class);

    public ShiftView(PosService service, UserService userService) {
        this.service = service;
        this.userService = userService;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureGrid();
        

        Button addBtn = new Button("Abrir Turno", VaadinIcon.PLUS.create());
        addBtn.addClassNames("bg-primary", "text-white", "text-sm", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow", "hover:shadow-md", "transition-all");
        addBtn.addClickListener(e -> openDialog());

        HorizontalLayout header = new HorizontalLayout(new AppLabel("Turnos de Caja"), addBtn);
        header.addClassNames("w-full", "justify-between", "items-center");

        add(header, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassNames( "bg-bg-surface", "rounded-lg", "shadow");
        grid.setSizeFull();
        grid.setColumns("fechaHoraApertura", "fechaHoraCierre", "montoInicialEfectivo", "diferencia", "estado");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateList() {
        grid.setItems(service.findAllTurnos());
    }

    private void openDialog() {
        TailwindModal modal = new TailwindModal("Abrir Turno");
        
        PosTurno item = new PosTurno();
        item.setFechaHoraApertura(LocalDateTime.now());
        item.setEstado("ABIERTO");

        Binder<PosTurno> binder = new Binder<>(PosTurno.class);

        FormLayout formLayout = new FormLayout();
        formLayout.addClassNames("w-full");
        
        BigDecimalField montoInicial = new BigDecimalField("Monto Inicial Efectivo");
        montoInicial.addClassName("w-full");

        formLayout.add(montoInicial);
        
        modal.addContent(formLayout);

        binder.forField(montoInicial).asRequired("Monto inicial requerido").bind(PosTurno::getMontoInicialEfectivo, PosTurno::setMontoInicialEfectivo);

        Button saveButton = new Button("Abrir", e -> {
            try {
                binder.writeBean(item);
                
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                SysUsuario user = userService.findByUsername(username);
                item.setUsuarioCajero(user);
                
                List<PosCaja> cajas = service.findAllCajas();
                if (cajas.isEmpty()) {
                    PosCaja c = new PosCaja();
                    c.setNombre("Caja Principal");
                    c = service.saveCaja(c);
                    item.setCaja(c);
                } else {
                    item.setCaja(cajas.get(0));
                }
                
                service.saveTurno(item);
                TailwindNotification.show("Turno abierto exitosamente", TailwindNotification.Type.SUCCESS);
                updateList();
                modal.close();
            } catch (ValidationException ex) {
                TailwindNotification.show("Revise los campos requeridos", TailwindNotification.Type.ERROR);
            }
        });
        saveButton.addClassNames("bg-primary", "text-white", "font-semibold", "py-2", "px-4", "rounded-lg", "shadow");

        Button cancelButton = new Button("Cancelar", e -> {
            TailwindNotification.show("Acción cancelada", TailwindNotification.Type.INFO);
            modal.close();
        });
        cancelButton.addClassNames("bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]", "font-medium", "py-2", "px-4", "rounded-lg");

        modal.addFooterButton(cancelButton);
        modal.addFooterButton(saveButton);
        
        add(modal);
        modal.open();
    }
}
