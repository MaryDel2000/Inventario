package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class TailwindModal extends Div {

    private final VerticalLayout bodyLayout;
    private final HorizontalLayout footerLayout;
    private final Div modalContent;

    public TailwindModal(String title) {
        addClassNames("fixed", "inset-0", "z-50", "overflow-y-auto");
        
        // Backdrop with blur effect and dark overlay
        // Using bg-gray-900 with clear opacity class and explicit full viewport dimensions
        // Backdrop with blur effect and dark overlay
        // z-40 to be behind the panelContainer (z-50)
        Div backdrop = new Div();
        backdrop.addClassNames("fixed", "inset-0", "h-screen", "w-screen", "bg-gray-900", "bg-opacity-75", "backdrop-blur-sm", "transition-opacity", "z-40");
        
        // Modal Panel Container (Centering)
        Div panelContainer = new Div();
        // Fully decoupled fixed overlay for centering
        panelContainer.addClassNames("fixed", "inset-0", "z-50", "flex", "items-center", "justify-center", "p-4", "sm:p-0");
        // Actually, panelContainer is on top of backdrop. We want clicks on panelContainer (empty space) to close? 
        // For now, let's just center.
        panelContainer.getStyle().remove("pointer-events"); // revert

        // Modal Content Card
        // Increased max-width to max-w-2xl
        modalContent = new Div();
        modalContent.addClassNames("relative", "transform", "rounded-xl", "text-left", "shadow-2xl", "transition-all", "w-full", "max-w-2xl", "border", "bg-white");
        modalContent.getStyle().set("background-color", "var(--color-bg-surface)");
        modalContent.getStyle().set("color", "var(--color-text-main)");
        modalContent.getStyle().set("border-color", "var(--color-border)");
        
        // Header
        Div header = new Div();
        header.addClassNames("px-6", "py-4", "border-b");
        header.getStyle().set("background-color", "var(--color-bg-surface)");
        header.getStyle().set("border-color", "var(--color-border)");
        
        Div headerContent = new Div();
        headerContent.addClassNames("flex", "items-center", "justify-between");
        
        H3 titleComponent = new H3(title);
        titleComponent.addClassNames("text-xl", "font-bold", "m-0");
        titleComponent.getStyle().set("color", "var(--color-text-main)");
        
        Button closeButton = new Button(com.vaadin.flow.component.icon.VaadinIcon.CLOSE.create());
        closeButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickListener(e -> close());
        
        headerContent.add(titleComponent, closeButton);
        header.add(headerContent);
        
        // Body
        bodyLayout = new VerticalLayout();
        bodyLayout.addClassNames("px-6", "py-4");
        bodyLayout.setPadding(false);
        bodyLayout.setSpacing(true);
        
        // Footer
        footerLayout = new HorizontalLayout();
        footerLayout.addClassNames("px-6", "py-4", "flex", "justify-end", "gap-3", "border-t");
        footerLayout.getStyle().set("background-color", "var(--color-bg-secondary)");
        footerLayout.getStyle().set("border-color", "var(--color-border)");
        footerLayout.setSpacing(false); // Managed by gap-3

        modalContent.add(header, bodyLayout, footerLayout);
        panelContainer.add(modalContent);
        
        add(backdrop, panelContainer);
    }

    public void addContent(Component... components) {
        bodyLayout.add(components);
    }

    public void addFooterButton(Button button) {
        footerLayout.add(button);
    }

    public void open() {
        // Automatically attach to the current UI if not already attached
        if (!getParent().isPresent()) {
            com.vaadin.flow.component.UI ui = com.vaadin.flow.component.UI.getCurrent();
            if (ui != null) {
                ui.add(this);
            } else {
                throw new IllegalStateException("UI.getCurrent() is null. Cannot open modal.");
            }
        }
        super.setVisible(true);
    }

    public void close() {
        super.setVisible(false);
        getElement().removeFromParent();
    }

    public void setDialogMaxWidth(String maxWidthClass) {
        modalContent.removeClassName("max-w-2xl");
        modalContent.addClassName(maxWidthClass);
    }
}
