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

    public TailwindModal(String title) {
        addClassNames("fixed", "inset-0", "z-[9999]", "overflow-y-auto");
        
        // Backdrop with blur effect and dark overlay
        // Using bg-gray-900 with clear opacity class and explicit full viewport dimensions
        Div backdrop = new Div();
        backdrop.addClassNames("fixed", "inset-0", "h-screen", "w-screen", "bg-gray-900", "bg-opacity-75", "backdrop-blur-sm", "transition-opacity");
        
        // Modal Panel Container (Centering)
        Div panelContainer = new Div();
        panelContainer.addClassNames("flex", "min-h-full", "items-center", "justify-center", "p-4", "text-center");

        // Modal Content Card
        // Increased max-width to max-w-2xl
        // Modal Content Card
        // Increased max-width to max-w-2xl
        // Modal Content Card
        // Increased max-width to max-w-2xl
        Div modalContent = new Div();
        modalContent.addClassNames("relative", "transform", "rounded-xl", "text-left", "shadow-2xl", "transition-all", "w-full", "max-w-2xl", "border", "overflow-hidden");
        modalContent.getStyle().set("background-color", "var(--color-bg-surface)");
        modalContent.getStyle().set("color", "var(--color-text-main)");
        modalContent.getStyle().set("border-color", "var(--color-border)");
        
        // Header
        Div header = new Div();
        header.addClassNames("px-6", "py-4", "border-b");
        header.getStyle().set("background-color", "var(--color-bg-surface)");
        header.getStyle().set("border-color", "var(--color-border)");
        
        H3 titleComponent = new H3(title);
        titleComponent.addClassNames("text-xl", "font-bold", "m-0");
        titleComponent.getStyle().set("color", "var(--color-text-main)");
        header.add(titleComponent);
        
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
        // In Vaadin, strictly speaking, we are just a component. 
        // We assume the caller adds us to the layout.
        // But to mimic Dialog, we can't easily auto-attach to UI body without UI access.
        // It's safer to rely on the caller to 'add(modal)'
        super.setVisible(true);
    }

    public void close() {
        getElement().removeFromParent();
    }
}
