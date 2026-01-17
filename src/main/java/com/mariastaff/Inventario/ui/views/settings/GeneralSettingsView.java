package com.mariastaff.Inventario.ui.views.settings;

import com.mariastaff.Inventario.backend.data.entity.GenMoneda;
import com.mariastaff.Inventario.backend.data.entity.SysConfiguracion;
import com.mariastaff.Inventario.backend.service.ConfiguracionService;
import com.mariastaff.Inventario.backend.service.GeneralService;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.components.base.TailwindNotification;
import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
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
    
    // Company Info Fields
    private final TextField nombreEmpresa = new TextField();
    private final TextField direccion = new TextField();
    private final TextField telefono = new TextField();
    private final TextField nit = new TextField();

    // Default Configuration Fields
    private final ComboBox<GenMoneda> monedaDefault = new ComboBox<>();
    private final BigDecimalField ivaPorcentajeDefault = new BigDecimalField();
    
    private final Button saveButton = new Button();
    
    private final Binder<SysConfiguracion> binder = new Binder<>(SysConfiguracion.class);
    private SysConfiguracion currentConfig;

    public GeneralSettingsView(ConfiguracionService configuracionService, GeneralService generalService) {
        this.configuracionService = configuracionService;
        this.generalService = generalService;
        
        addClassNames("w-full", "h-full", "bg-bg-secondary", "p-6", "overflow-auto");
        
        configureFields();
        bindFields();
        loadData();
        
        add(new AppLabel(getTranslation("view.settings.general.title")), createForm());
    }

    private void configureFields() {
        // Translations
        nombreEmpresa.setLabel(getTranslation("view.settings.company.name"));
        direccion.setLabel(getTranslation("view.settings.company.address"));
        telefono.setLabel(getTranslation("view.settings.company.phone"));
        nit.setLabel(getTranslation("view.settings.company.taxid"));
        
        monedaDefault.setLabel(getTranslation("view.settings.defaults.currency"));
        ivaPorcentajeDefault.setLabel(getTranslation("view.settings.defaults.tax"));
        saveButton.setText(getTranslation("action.save.changes"));

        // Metadata
        monedaDefault.setItemLabelGenerator(GenMoneda::getNombre);
        monedaDefault.setItems(generalService.findAllMonedas());
        
        // Styles
        saveButton.addClassNames("bg-primary", "text-white", "hover:bg-primary-hover", "px-6", "py-2", "rounded-md", "font-medium", "mt-4");
        saveButton.addClickListener(e -> save());
        
        // Styling inputs
        nombreEmpresa.addClassNames("w-full");
        direccion.addClassNames("w-full");
    }
    
    private VerticalLayout createForm() {
        VerticalLayout container = new VerticalLayout();
        container.addClassNames("bg-bg-surface", "p-8", "rounded-xl", "shadow-sm", "max-w-4xl", "mx-auto");
        container.setSpacing(true);

        // Section 1: Company Info
        H3 sectionCompany = new H3(getTranslation("view.settings.general.section.company"));
        sectionCompany.addClassNames("text-lg", "font-bold", "text-text-primary", "mb-4", "border-b", "pb-2", "border-border-subtle");
        
        FormLayout companyForm = new FormLayout();
        companyForm.add(nombreEmpresa, nit, direccion, telefono);
        companyForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        companyForm.setColspan(direccion, 2);

        // Section 2: Defaults
        H3 sectionDefaults = new H3(getTranslation("view.settings.general.section.defaults"));
        sectionDefaults.addClassNames("text-lg", "font-bold", "text-text-primary", "mt-6", "mb-4", "border-b", "pb-2", "border-border-subtle");

        FormLayout defaultsForm = new FormLayout();
        defaultsForm.add(monedaDefault, ivaPorcentajeDefault);
        
        container.add(sectionCompany, companyForm, sectionDefaults, defaultsForm, saveButton);
        return container;
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
            TailwindNotification.show(getTranslation("msg.settings.saved"), TailwindNotification.Type.SUCCESS);
        } else {
            TailwindNotification.show(getTranslation("msg.error.validation"), TailwindNotification.Type.ERROR);
        }
    }
}
