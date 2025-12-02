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
        addClassNames("w-full", "bg-[var(--color-bg-primary)]", "border-b", "border-[var(--color-border)]", "px-6", "py-3");
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        setSpacing(false);

        // Left side: Title (or Breadcrumbs later)
        AppLabel title = new AppLabel("app.title"); // Could be dynamic
        title.addClassNames("text-lg", "font-semibold");
        add(title);

        // Right side: Theme Switch & Profile
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.addClassNames("flex", "items-center", "gap-4");
        rightSection.setSpacing(false);

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

        add(rightSection);
    }
}
