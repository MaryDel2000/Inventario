package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.backend.data.entity.GenMoneda;
import com.mariastaff.Inventario.backend.data.entity.SysConfiguracion;
import com.mariastaff.Inventario.backend.service.ConfiguracionService;
import com.mariastaff.Inventario.backend.service.GeneralService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.util.List;

@PageTitle("Configuración General | Configuración")
@Route(value = "settings/general", layout = MainLayout.class)
@PermitAll
public class GeneralSettingsView extends VerticalLayout {

    private final ConfiguracionService configuracionService;
    private final GeneralService generalService;
    
    private final ComboBox<GenMoneda> monedaDefault = new ComboBox<>("Moneda Principal");
    private final BigDecimalField ivaPorcentajeDefault = new BigDecimalField("IVA % Base");
    private final Button saveButton = new Button("Guardar Cambios");
    
    private final Binder<SysConfiguracion> binder = new Binder<>(SysConfiguracion.class);
    private SysConfiguracion currentConfig;

    public GeneralSettingsView(ConfiguracionService configuracionService, GeneralService generalService) {
        this.configuracionService = configuracionService;
        this.generalService = generalService;
        
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6");
        
        configureFields();
        bindFields();
        loadData();
        
        add(new AppLabel("Configuración General"), createForm());
    }

    private void configureFields() {
        monedaDefault.setItemLabelGenerator(GenMoneda::getNombre);
        monedaDefault.setItems(generalService.findAllMonedas());
        
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> save());
    }
    
    private FormLayout createForm() {
        FormLayout form = new FormLayout();
        form.addClassNames("bg-white", "p-6", "rounded-lg", "shadow", "max-w-2xl");
        form.add(monedaDefault, ivaPorcentajeDefault, saveButton);
        return form;
    }

    private void bindFields() {
        binder.bindInstanceFields(this);
    }
    
    private void loadData() {
        List<SysConfiguracion> configs = configuracionService.findAll();
        if (!configs.isEmpty()) {
            currentConfig = configs.get(0);
        } else {
            currentConfig = new SysConfiguracion();
        }
        binder.setBean(currentConfig);
    }
    
    private void save() {
        if (binder.validate().isOk()) {
            configuracionService.save(currentConfig);
            Notification.show("Configuración guardada exitosamente");
        }
    }
}
