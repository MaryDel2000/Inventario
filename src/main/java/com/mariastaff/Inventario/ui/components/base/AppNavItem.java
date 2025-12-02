package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AppNavItem extends Composite<Div> {

    private final AppLabel label;
    private final AppIcon icon;
    private boolean selected = false;
    private final HorizontalLayout layout;

    public AppNavItem(String labelText, AppIcon icon) {
        this.label = new AppLabel(labelText);
        this.icon = icon;
        
        layout = new HorizontalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        layout.addClassNames("w-full", "p-2", "rounded-lg", "cursor-pointer", "transition-colors");
        
        // Icon wrapper
        Div iconDiv = new Div(icon.create());
        iconDiv.addClassNames("text-xl", "text-[var(--color-text-muted)]");

        layout.add(iconDiv, label);
        
        getContent().add(layout);
        updateStyles();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateStyles();
    }

    private void updateStyles() {
        if (selected) {
            layout.addClassNames("bg-[var(--color-primary)]", "text-white");
            layout.removeClassNames("hover:bg-[var(--color-bg-secondary)]");
            // Force label color change if needed, though parent text-white usually cascades
            label.addClassNames("text-white");
        } else {
            layout.removeClassNames("bg-[var(--color-primary)]", "text-white");
            layout.addClassNames("hover:bg-[var(--color-bg-secondary)]");
            label.removeClassNames("text-white");
        }
    }
}
