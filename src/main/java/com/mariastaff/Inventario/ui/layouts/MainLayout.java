package com.mariastaff.Inventario.ui.layouts;

import com.vaadin.flow.component.applayout.AppLayout;
import com.mariastaff.Inventario.ui.components.MainHeader;
import com.mariastaff.Inventario.ui.components.Sidebar;

public class MainLayout extends AppLayout {

    public MainLayout() {
        // Tailwind: remove default styling if needed, but AppLayout is complex.
        // We will just add our components.
        setPrimarySection(Section.DRAWER);
        
        // Remove default navbar styling to let MainHeader handle it
        getElement().getThemeList().clear(); 
        
        addDrawerCentered();
        addHeaderCentered();
    }

    private void addHeaderCentered() {
        addToNavbar(new MainHeader());
    }

    private void addDrawerCentered() {
        addToDrawer(new Sidebar());
    }
}
