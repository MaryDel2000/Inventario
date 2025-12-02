package com.mariastaff.Inventario.ui.components.composite;

import com.mariastaff.Inventario.ui.components.base.AppIcon;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.components.base.AppNavItem;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public class AppSidebar extends VerticalLayout {

    private final List<AppNavItem> navItems = new ArrayList<>();

    public AppSidebar(AppIcon logoIcon, String logoText) {
        addClassNames("w-64", "h-full", "bg-[var(--color-bg-surface)]", "border-r", "border-[var(--color-border)]", "p-4");
        setSpacing(true);
        setPadding(false);

        // Logo Area
        VerticalLayout logoLayout = new VerticalLayout();
        logoLayout.setAlignItems(Alignment.CENTER);
        logoLayout.add(logoIcon.create());
        
        AppLabel logoLabel = new AppLabel(logoText);
        logoLabel.addClassNames("text-xl", "font-bold", "mt-2");
        logoLayout.add(logoLabel);
        
        add(logoLayout);
    }

    public void addNavItem(AppNavItem item) {
        navItems.add(item);
        add(item);
    }
}
