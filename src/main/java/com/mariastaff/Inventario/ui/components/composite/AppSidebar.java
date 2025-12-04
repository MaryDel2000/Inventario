package com.mariastaff.Inventario.ui.components.composite;

import com.mariastaff.Inventario.ui.components.base.AppIcon;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.components.base.AppNavItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppSidebar extends VerticalLayout {

    private final List<AppNavItem> navItems = new ArrayList<>();
    private boolean expanded = true; // Logical state
    private boolean hoverExpanded = false; // Hover state
    private final VerticalLayout logoLayout;
    private final com.vaadin.flow.component.Component logoComponent;
    private final com.vaadin.flow.component.Component collapsedLogoComponent;
    private final AppLabel logoLabel;

    public AppSidebar(AppIcon logoIcon, AppIcon collapsedLogoIcon, String logoText) {
        addClassNames("w-64", "h-full", "bg-[var(--color-bg-surface)]", "border-r", "border-[var(--color-border)]", "p-4", "transition-all", "duration-300", "z-50");
        setSpacing(true);
        setPadding(false);

        // Logo Area
        logoLayout = new VerticalLayout();
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        logoLayout.setPadding(false);
        logoLayout.setSpacing(true);
        logoLayout.setHeight("100px");
        logoLayout.setMinHeight("100px");
        logoLayout.getStyle().set("flex-shrink", "0");
        logoLayout.addClassNames("shrink-0", "transition-all", "duration-300"); // Fixed height to prevent jump
        
        logoComponent = logoIcon.create();
        collapsedLogoComponent = collapsedLogoIcon.create();
        collapsedLogoComponent.setVisible(false);
        
        // Apply styles to collapsed logo to ensure it fits well
        collapsedLogoComponent.getElement().getStyle().set("max-height", "100%");
        collapsedLogoComponent.getElement().getStyle().set("max-width", "100%");
        
        logoLayout.add(logoComponent, collapsedLogoComponent);
        
        logoLabel = new AppLabel(logoText);
        logoLabel.addClassNames("text-xl", "font-bold", "mt-2", "whitespace-nowrap", "overflow-hidden");
        if (logoText != null && !logoText.isEmpty()) {
            logoLayout.add(logoLabel);
        }
        
        add(logoLayout);

        // Hover listeners for auto-expand
        getElement().addEventListener("mouseenter", e -> {
            if (!expanded) {
                hoverExpanded = true;
                updateSidebarState(true);
            }
        });

        getElement().addEventListener("mouseleave", e -> {
            if (!expanded) {
                hoverExpanded = false;
                updateSidebarState(false);
            }
        });
    }

    public void addNavItem(AppNavItem item) {
        navItems.add(item);
        add(item);
        // Ensure item respects current state
        item.setExpanded(expanded);
    }

    public void toggleSidebar() {
        expanded = !expanded;
        updateSidebarState(expanded);
    }

    private Consumer<Boolean> stateChangeHandler;

    public void setStateChangeHandler(Consumer<Boolean> handler) {
        this.stateChangeHandler = handler;
    }

    private void updateSidebarState(boolean showExpanded) {
        if (showExpanded) {
            addClassName("w-64");
            removeClassName("w-[45px]");
            removeClassName("p-2");
            addClassName("p-4");
            
            logoComponent.setVisible(true);
            collapsedLogoComponent.setVisible(false);
            logoLabel.setVisible(true);
            
            logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            logoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            
            // Restore expanded styles
            logoComponent.getElement().getStyle().set("height", "75%");
        } else {
            removeClassName("w-64");
            removeClassName("w-10"); // Ensure old class is removed if present
            addClassName("w-[45px]");
            removeClassName("p-4");
            addClassName("p-2");
            
            logoComponent.setVisible(false);
            collapsedLogoComponent.setVisible(true);
            logoLabel.setVisible(false);
            
            logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            logoLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
            
            // Collapsed styles
            collapsedLogoComponent.getElement().getStyle().set("height", "auto");
        }
        
        if (stateChangeHandler != null) {
            stateChangeHandler.accept(showExpanded);
        }
        
        navItems.forEach(item -> item.setExpanded(showExpanded));
    }
}
