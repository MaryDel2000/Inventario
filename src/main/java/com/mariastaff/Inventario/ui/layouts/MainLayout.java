package com.mariastaff.Inventario.ui.layouts;

import com.mariastaff.Inventario.ui.components.base.VaadinAppIcon;
import com.mariastaff.Inventario.ui.components.base.AppNavItem;
import com.mariastaff.Inventario.ui.components.composite.AppHeader;
import com.mariastaff.Inventario.ui.components.composite.AppSidebar;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.VaadinIcon;

public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        getElement().getThemeList().clear(); 

        addHeaderCentered();
        addDrawerCentered();
    }

    private void addHeaderCentered() {
        addToNavbar(new AppHeader());
    }

    private void addDrawerCentered() {
        AppSidebar sidebar = new AppSidebar(new VaadinAppIcon(VaadinIcon.PACKAGE), "app.title");
        
        sidebar.addNavItem(new AppNavItem("nav.dashboard", new VaadinAppIcon(VaadinIcon.DASHBOARD)));
        sidebar.addNavItem(new AppNavItem("nav.products", new VaadinAppIcon(VaadinIcon.CART)));
        sidebar.addNavItem(new AppNavItem("nav.users", new VaadinAppIcon(VaadinIcon.USERS)));
        
        addToDrawer(sidebar);
    }
}
