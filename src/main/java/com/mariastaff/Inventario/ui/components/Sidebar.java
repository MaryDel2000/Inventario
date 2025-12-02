package com.mariastaff.Inventario.ui.components;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.mariastaff.Inventario.ui.views.DashboardView;

public class Sidebar extends VerticalLayout {

    public Sidebar() {
        // Tailwind classes: width, height, background, padding, spacing
        addClassNames("w-64", "h-full", "bg-[var(--color-bg-secondary)]", "p-4", "space-y-4");
        setSpacing(false); // Disable Vaadin default spacing to use Tailwind's space-y-4
        setPadding(false); // Disable Vaadin default padding

        H1 logo = new H1("MARIASTAFF.COM");
        logo.addClassNames("text-xl", "font-bold", "text-[var(--color-text-main)]");
        
        Span subLogo = new Span("INVENTARIO");
        subLogo.addClassNames("text-sm", "font-medium", "text-[var(--color-text-muted)]", "tracking-wider");

        VerticalLayout header = new VerticalLayout(logo, subLogo);
        header.setSpacing(false);
        header.setPadding(false);

        SideNav nav = new SideNav();
        nav.addClassNames("w-full");
        
        // Note: SideNav styles might need specific targeting or custom CSS if Tailwind doesn't reach shadow DOM, 
        // but for the container it works.
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Productos", DashboardView.class, VaadinIcon.PACKAGE.create()));
        nav.addItem(new SideNavItem("Proveedores", DashboardView.class, VaadinIcon.TRUCK.create()));
        nav.addItem(new SideNavItem("Almacenes", DashboardView.class, VaadinIcon.STORAGE.create()));
        nav.addItem(new SideNavItem("Compras", DashboardView.class, VaadinIcon.CART.create()));

        add(header, nav);
    }
}
