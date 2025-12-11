package com.mariastaff.Inventario.ui.views.accounting;

import com.mariastaff.Inventario.backend.data.entity.ContAsiento;
import com.mariastaff.Inventario.backend.service.ContabilidadService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Asientos Manuales | Contabilidad")
@Route(value = "accounting/manual-entries", layout = MainLayout.class)
@PermitAll
public class ManualEntriesView extends VerticalLayout {

    private final ContabilidadService service;
    private final Binder<ContAsiento> binder = new Binder<>(ContAsiento.class);

    public ManualEntriesView(ContabilidadService service) {
        this.service = service;
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        add(new AppLabel("Nuevo Asiento Manual"));
        
        FormLayout form = createForm();
        add(form);
        
        binder.setBean(new ContAsiento());
    }
    
    private FormLayout createForm() {
        FormLayout form = new FormLayout();
        form.addClassNames("bg-white", "p-6", "rounded-lg", "shadow", "max-w-2xl");
        
        TextField descripcion = new TextField("Descripción");
        TextField origen = new TextField("Origen (Referencia)");
        // Needs proper detail grid (debe/haber) but basic header form for now.
        
        Button save = new Button("Guardar Cabecera", e -> save());
        save.addClassNames("bg-primary", "text-white", "hover:bg-primary-hover", "px-4", "py-2", "rounded");
        
        binder.forField(descripcion).bind("descripcion");
        binder.forField(origen).bind("origen");
        
        form.add(descripcion, origen, save);
        return form;
    }
    
    private void save() {
        if (binder.validate().isOk()) {
            service.saveAsiento(binder.getBean());
            Notification.show("Asiento creado");
            binder.setBean(new ContAsiento());
        }
    }
}
