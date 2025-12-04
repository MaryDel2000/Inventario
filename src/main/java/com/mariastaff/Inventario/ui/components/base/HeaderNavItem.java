package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Component;

public class HeaderNavItem extends AppNavItem {

    public HeaderNavItem(String labelText, AppIcon icon, Class<? extends Component> navigationTarget) {
        super(labelText, icon, navigationTarget);
        // Currently no specific styles, but separated as requested
    }
}
