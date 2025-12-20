package com.mariastaff.Inventario.ui.components.composite;

import com.mariastaff.Inventario.ui.components.base.AppIcon;
import com.mariastaff.Inventario.ui.components.base.AppLabel;
import com.mariastaff.Inventario.ui.components.base.VaadinAppIcon;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class AppHeader extends HorizontalLayout {

    private final Button menuButton;

    public AppHeader() {
        setWidthFull();
        addClassNames("flex", "w-full", "bg-bg-primary", "border-b", "border-border", "px-4", "md:px-6", "py-3");
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        setSpacing(false);

        // Left side: Toggle & Title
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSection.setSpacing(true);
        leftSection.addClassNames("flex", "items-center", "gap-2");
        
        // Add Menu Button for sidebar toggle
        menuButton = new Button(VaadinIcon.MENU.create());
        menuButton.addClassNames("header-action-btn");
        menuButton.getStyle().setMarginRight("15px");
        
        leftSection.add(menuButton);
        
        // Right side: Theme Switch & Profile
        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.addClassNames("flex", "items-center", "gap-4");
        rightSection.setSpacing(false);

        Button themeToggle = new Button(VaadinIcon.MOON_O.create());
        themeToggle.addClassNames("header-action-btn", "hover:text-primary");
        themeToggle.getStyle().setMarginLeft("15px");
        
        
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
        

        Button profileTriggerBtn = new Button(VaadinIcon.USER.create());
        profileTriggerBtn.addClassNames("rounded-full", "min-w-[40px]", "w-10", "h-10", "p-0", 
            "bg-gray-100", "dark:bg-gray-800", "text-gray-600", "dark:text-gray-300",
            "border", "border-border", "flex", "items-center", "justify-center",
            "hover:ring-2", "hover:ring-gray-300", "transition-all");

        ContextMenu userMenu = new ContextMenu(profileTriggerBtn);
        userMenu.setOpenOnClick(true);
        
        Div menuContent = new Div();
        menuContent.addClassNames("flex", "flex-col", "items-center", "p-5", "gap-4", "min-w-[240px]", 
            "bg-bg-surface", 
            "text-text-main", 
            "rounded-xl", "shadow-xl", "border", "border-border");
        
        // Profile header in menu
        Div profileIconContainer = new Div(VaadinIcon.USER.create());
        profileIconContainer.addClassNames("w-16", "h-16", "rounded-full", 
            "bg-gray-100", "dark:bg-gray-700", 
            "text-gray-500", "dark:text-gray-400", 
            "flex", "items-center", "justify-center", "text-3xl", "mb-1");

        Span userName = new Span("Maria Staff");
        userName.addClassNames("font-bold", "text-lg", "leading-none");

        Span emailText = new Span("mail@mail.mail");
        emailText.addClassNames("text-sm", "text-gray-500", "dark:text-gray-400", "mb-2");
        
        Button profileBtn = new Button("Perfil", VaadinIcon.USER.create());
        profileBtn.addClassNames("w-full", "h-11", "bg-black", "dark:bg-gray-700", "text-white", 
            "rounded-lg", "hover:bg-gray-800", "dark:hover:bg-gray-600", 
            "font-medium", "flex", "items-center", "justify-start", "px-4", "gap-3", "text-base"); // Taller and spaced
        
        Button logoutBtn = new Button("Cerrar Sesión", VaadinIcon.EXIT.create());
        logoutBtn.addClassNames("w-full", "h-11", "bg-black", "dark:bg-gray-700", "text-white", 
            "rounded-lg", "hover:bg-gray-800", "dark:hover:bg-gray-600", 
            "font-medium", "flex", "items-center", "justify-start", "px-4", "gap-3", "text-base");
        logoutBtn.addClickListener(e -> UI.getCurrent().getPage().setLocation("/logout"));

        menuContent.add(profileIconContainer, userName, emailText, profileBtn, logoutBtn);
        userMenu.add(menuContent);

        rightSection.add(themeToggle, profileTriggerBtn);

        // Center side: Navigation
        HorizontalLayout centerSection = new HorizontalLayout();
        centerSection.setAlignItems(FlexComponent.Alignment.CENTER);
        centerSection.setSpacing(true);
        centerSection.addClassNames("header-center-nav", "flex", "flex-1", "justify-center");
        
        add(leftSection, centerSection, rightSection);
    }

    public void addNavigationItem(com.vaadin.flow.component.Component item) {
        // We assume the second component is the centerSection
        if (getComponentCount() >= 2 && getComponentAt(1) instanceof HorizontalLayout) {
            ((HorizontalLayout) getComponentAt(1)).add(item);
        }
    }

    public void clearNavigationItems() {
        if (getComponentCount() >= 2 && getComponentAt(1) instanceof HorizontalLayout) {
            ((HorizontalLayout) getComponentAt(1)).removeAll();
        }
    }

    public void addMenuListener(com.vaadin.flow.component.ComponentEventListener<com.vaadin.flow.component.ClickEvent<Button>> listener) {
        menuButton.addClickListener(listener);
    }
}
