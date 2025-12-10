package com.mariastaff.Inventario.ui.views.purchases;

import com.mariastaff.Inventario.backend.data.entity.InvCompra;
import com.mariastaff.Inventario.backend.service.CompraService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Nueva Compra | Compras")
@Route(value = "purchases/new", layout = MainLayout.class)
@PermitAll
public class NewPurchaseView extends VerticalLayout {

    private final CompraService service;
    private final Binder<InvCompra> binder = new Binder<>(InvCompra.class);

    public NewPurchaseView(CompraService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        add(new AppLabel("Registrar Nueva Compra"));
        
        FormLayout form = createForm();
        add(form);
        
        binder.setBean(new InvCompra());
    }
    
    private FormLayout createForm() {
        FormLayout form = new FormLayout();
        form.addClassNames("bg-white", "p-6", "rounded-lg", "shadow", "max-w-2xl");
        
        TextField numeroDocumento = new TextField("Número Documento");
        TextField tipoDocumento = new TextField("Tipo Documento");
        
        Button save = new Button("Guardar", e -> save());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        binder.bindInstanceFields(this);
        binder.forField(numeroDocumento).bind("numeroDocumento");
        binder.forField(tipoDocumento).bind("tipoDocumento");
        
        form.add(numeroDocumento, tipoDocumento, save);
        
        return form;
    }
    
    private void save() {
        if (binder.validate().isOk()) {
            service.saveCompra(binder.getBean());
            Notification.show("Compra guardada");
            binder.setBean(new InvCompra());
        }
    }
}
