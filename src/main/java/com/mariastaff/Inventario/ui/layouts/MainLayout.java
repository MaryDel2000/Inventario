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
        AppHeader header = new AppHeader();
        
        // Move old sidebar items to header
        header.addNavigationItem(new AppNavItem("nav.dashboard", new VaadinAppIcon(VaadinIcon.DASHBOARD), com.mariastaff.Inventario.ui.views.DashboardView.class));
        header.addNavigationItem(new AppNavItem("nav.products", new VaadinAppIcon(VaadinIcon.CART), com.mariastaff.Inventario.ui.views.ProductsView.class));
        header.addNavigationItem(new AppNavItem("nav.users", new VaadinAppIcon(VaadinIcon.USERS), com.mariastaff.Inventario.ui.views.UsersView.class));
        
        addToNavbar(header);
    }

    private void addDrawerCentered() {
        AppSidebar sidebar = new AppSidebar(new VaadinAppIcon(VaadinIcon.PACKAGE), "app.title");
        
        // Add new items to sidebar
        sidebar.addNavItem(new AppNavItem("nav.pos", new VaadinAppIcon(VaadinIcon.CASH), com.mariastaff.Inventario.ui.views.POSView.class));
        sidebar.addNavItem(new AppNavItem("nav.inventory", new VaadinAppIcon(VaadinIcon.STORAGE), com.mariastaff.Inventario.ui.views.InventoryView.class));
        sidebar.addNavItem(new AppNavItem("nav.accounting", new VaadinAppIcon(VaadinIcon.CHART), com.mariastaff.Inventario.ui.views.AccountingView.class));
        
        addToDrawer(sidebar);
    }
}
