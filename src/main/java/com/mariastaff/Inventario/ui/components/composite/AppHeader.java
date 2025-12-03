package com.mariastaff.Inventario.ui.components.composite;

import com.mariastaff.Inventario.ui.components.base.AppIcon;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.components.base.VaadinAppIcon;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AppHeader extends HorizontalLayout {

    public AppHeader() {
        setWidthFull();
        addClassNames("w-full", "bg-[var(--color-bg-primary)]", "border-b", "border-[var(--color-border)]", "px-6", "py-3");
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        setSpacing(false);

        // Left side: Toggle & Title
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSection.setSpacing(true);
        
        // Add DrawerToggle for sidebar
        com.vaadin.flow.component.applayout.DrawerToggle toggle = new com.vaadin.flow.component.applayout.DrawerToggle();
        toggle.addClassNames("text-[var(--color-text-secondary)]");
        
        AppLabel title = new AppLabel("app.title");
        title.addClassNames("text-lg", "font-semibold");
        
        leftSection.add(toggle, title);
        add(leftSection);
        
 

        // Right side: Theme Switch & Profile
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.addClassNames("flex", "items-center", "gap-4");
        rightSection.setSpacing(false);

        Button themeToggle = new Button(VaadinIcon.MOON_O.create());
        themeToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        themeToggle.addClassNames("text-[var(--color-text-secondary)]", "hover:text-[var(--color-primary)]");
        
        themeToggle.addClickListener(e -> {
            UI.getCurrent().getPage().executeJs(
                "if (document.documentElement.getAttribute('theme') === 'dark') {" +
                "  document.documentElement.removeAttribute('theme');" +
                "  return 'light';" +
                "} else {" +
                "  document.documentElement.setAttribute('theme', 'dark');" +
                "  return 'dark';" +
                "}"
            ).then(response -> {
                if ("dark".equals(response.asString())) {
                    themeToggle.setIcon(VaadinIcon.SUN_O.create());
                } else {
                    themeToggle.setIcon(VaadinIcon.MOON_O.create());
                }
            });
        });

        Avatar avatar = new Avatar("Maria Staff");
        avatar.setImage("https://i.pravatar.cc/150?img=32");
        avatar.addClassNames("border-2", "border-[var(--color-border)]");

        rightSection.add(themeToggle, avatar);

        // Center side: Navigation
        HorizontalLayout centerSection = new HorizontalLayout();
        centerSection.setAlignItems(FlexComponent.Alignment.CENTER);
        centerSection.setSpacing(true);
        centerSection.addClassNames("hidden", "md:flex", "flex-1", "justify-center"); // Hide on small screens, center on large
        
        add(leftSection, centerSection, rightSection);
    }

    public void addNavigationItem(com.vaadin.flow.component.Component item) {
        // We assume the second component is the centerSection
        if (getComponentCount() >= 2 && getComponentAt(1) instanceof HorizontalLayout) {
            ((HorizontalLayout) getComponentAt(1)).add(item);
        }
    }
}
