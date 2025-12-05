package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AppNavItem extends Composite<RouterLink> {

    private final AppLabel label;
    private final AppIcon icon;
    private final HorizontalLayout layout;

    public AppNavItem(String labelText, AppIcon icon, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        this.label = new AppLabel(labelText);
        this.label.removeClassNames("text-text-main");
        this.label.addClassNames("text-[#607d8b]");
        this.icon = icon;
        
        RouterLink link = getContent();
        link.setRoute(navigationTarget);
        
        // Base styles
        link.addClassNames("flex", "items-center", "gap-3", "w-full", "p-2", "rounded-lg", "cursor-pointer", "transition-colors", "no-underline");
        
        // Icon wrapper
        Div iconDiv = new Div(icon.create());
        iconDiv.addClassNames("text-xl", "text-[#607d8b]", "flex", "items-center");

        // Use HorizontalLayout to ensure flex behavior works reliably inside RouterLink
        layout = new HorizontalLayout(iconDiv, label);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(false);
        layout.setMargin(false);
        layout.addClassNames("w-full"); // Ensure layout takes full width of the link

        link.add(layout);
        
        // Highlight logic
        link.setHighlightCondition(HighlightConditions.locationPrefix());
        link.setHighlightAction((r, active) -> {
            if (active) {
                r.addClassNames("bg-primary", "text-white");
                r.removeClassNames("hover:bg-bg-secondary", "text-[#607d8b]");
                label.addClassNames("text-white");
                label.removeClassNames("text-[#607d8b]");
                iconDiv.removeClassNames("text-[#607d8b]");
                iconDiv.addClassNames("text-white");
            } else {
                r.removeClassNames("bg-primary", "text-white");
                r.addClassNames("hover:bg-bg-secondary", "text-[#607d8b]");
                label.removeClassNames("text-white");
                label.addClassNames("text-[#607d8b]");
                iconDiv.addClassNames("text-[#607d8b]");
                iconDiv.removeClassNames("text-white");
            }
        });
    }

    public void setExpanded(boolean expanded) {
        label.setVisible(expanded);
        if (expanded) {
            getContent().removeClassName("justify-center");
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        } else {
            getContent().addClassName("justify-center");
            layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        }
    }
}
