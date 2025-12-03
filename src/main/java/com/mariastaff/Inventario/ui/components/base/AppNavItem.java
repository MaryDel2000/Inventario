package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

public class AppNavItem extends Composite<RouterLink> {

    private final AppLabel label;
    private final AppIcon icon;

    public AppNavItem(String labelText, AppIcon icon, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        this.label = new AppLabel(labelText);
        this.icon = icon;
        
        RouterLink link = getContent();
        link.setRoute(navigationTarget);
        
        // Base styles
        link.addClassNames("flex", "items-center", "gap-3", "w-full", "p-2", "rounded-lg", "cursor-pointer", "transition-colors", "no-underline");
        
        // Icon wrapper
        Div iconDiv = new Div(icon.create());
        iconDiv.addClassNames("text-xl", "text-[var(--color-text-muted)]", "flex", "items-center");

        // Use HorizontalLayout to ensure flex behavior works reliably inside RouterLink
        com.vaadin.flow.component.orderedlayout.HorizontalLayout layout = new com.vaadin.flow.component.orderedlayout.HorizontalLayout(iconDiv, label);
        layout.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(false);
        layout.setMargin(false);
        layout.addClassNames("w-full"); // Ensure layout takes full width of the link

        link.add(layout);
        
        // Highlight logic
        link.setHighlightCondition(HighlightConditions.locationPrefix());
        link.setHighlightAction((r, active) -> {
            if (active) {
                r.addClassNames("bg-[var(--color-primary)]", "text-white");
                r.removeClassNames("hover:bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]");
                label.addClassNames("text-white");
                label.removeClassNames("text-[var(--color-text-main)]");
                iconDiv.removeClassNames("text-[var(--color-text-muted)]");
                iconDiv.addClassNames("text-white");
            } else {
                r.removeClassNames("bg-[var(--color-primary)]", "text-white");
                r.addClassNames("hover:bg-[var(--color-bg-secondary)]", "text-[var(--color-text-main)]");
                label.removeClassNames("text-white");
                label.addClassNames("text-[var(--color-text-main)]");
                iconDiv.addClassNames("text-[var(--color-text-muted)]");
                iconDiv.removeClassNames("text-white");
            }
        });
    }
}
