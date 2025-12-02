package com.mariastaff.Inventario.ui.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

public class MainHeader extends HorizontalLayout {

    public MainHeader() {
        // Tailwind classes: width full, background, border bottom, padding, flex alignment
        addClassNames("w-full", "bg-[var(--color-bg-primary)]", "border-b", "border-[var(--color-border)]", "px-6", "py-3");
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        setSpacing(false);

        // Tabs
        Tabs tabs = new Tabs();
        tabs.addClassNames("border-none"); // Remove default Vaadin border
        HeaderItem[] items = {
            new HeaderItem("Inventario", VaadinIcon.HOME),
            new HeaderItem("Punto de Venta", VaadinIcon.SHOP),
            new HeaderItem("Contabilidad", VaadinIcon.CHART)
        };

        for (HeaderItem item : items) {
            tabs.add(new Tab(item.icon().create(), new Span(" " + item.label())));
        }
        
        // Right side: Theme Switch & Profile
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.addClassNames("flex", "items-center", "gap-4");
        rightSection.setSpacing(false); // Use gap-4

        Button themeToggle = new Button(VaadinIcon.MOON_O.create());
        themeToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        themeToggle.addClassNames("text-[var(--color-text-secondary)]", "hover:text-[var(--color-primary)]");
        
        themeToggle.addClickListener(e -> {
            String currentTheme = UI.getCurrent().getElement().getAttribute("data-theme");
            if ("dark".equals(currentTheme)) {
                UI.getCurrent().getElement().removeAttribute("data-theme");
                themeToggle.setIcon(VaadinIcon.MOON_O.create());
            } else {
                UI.getCurrent().getElement().setAttribute("data-theme", "dark");
                themeToggle.setIcon(VaadinIcon.SUN_O.create());
            }
        });

        Avatar avatar = new Avatar("Maria Staff");
        avatar.setImage("https://i.pravatar.cc/150?img=32");
        avatar.addClassNames("border-2", "border-[var(--color-border)]");

        rightSection.add(themeToggle, avatar);

        add(tabs, rightSection);
    }

    private record HeaderItem(String label, VaadinIcon icon) {}
}
