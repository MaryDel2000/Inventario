package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Component;

public class SidebarNavItem extends AppNavItem {

    public SidebarNavItem(String labelText, AppIcon icon, Class<? extends Component> navigationTarget) {
        super(labelText, icon, navigationTarget);
    }
}
