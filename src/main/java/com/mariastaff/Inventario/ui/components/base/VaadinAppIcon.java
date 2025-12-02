package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

public class VaadinAppIcon implements AppIcon {
    private final VaadinIcon icon;

    public VaadinAppIcon(VaadinIcon icon) {
        this.icon = icon;
    }

    @Override
    public Component create() {
        return icon.create();
    }
}
