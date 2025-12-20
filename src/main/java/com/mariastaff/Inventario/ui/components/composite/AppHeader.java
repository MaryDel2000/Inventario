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
        

        // Profile Trigger Button matching other header action buttons style
        Button profileTriggerBtn = new Button(VaadinIcon.USER.create());
        profileTriggerBtn.addClassNames("rounded-full", "min-w-[40px]", "w-10", "h-10", "p-0", 
            "bg-transparent", 
            "text-text-secondary", // Semantic text color
            "border", "border-transparent",
            "flex", "items-center", "justify-center",
            "hover:bg-bg-secondary", "hover:text-primary", "transition-colors", "cursor-pointer");

        ContextMenu userMenu = new ContextMenu(profileTriggerBtn);
        userMenu.setOpenOnClick(true);
        
        Div menuContent = new Div();
        // Force solid background (bg-surface) to ensure consistency with theme
        // Increased padding and gap
        menuContent.addClassNames("flex", "flex-col", "items-center", "p-6", "pt-8", "gap-4", "min-w-[280px]", 
            "bg-bg-surface", 
            "text-text-main",
            "rounded-xl", "shadow-xl", "border", "border-border");
        
        // Profile header in menu
        // Icon color matched to app theme
        Div profileIconContainer = new Div(VaadinIcon.USER.create());
        profileIconContainer.addClassNames("w-20", "h-20", "rounded-full", 
            "bg-bg-secondary", 
            "text-text-secondary", 
            "flex", "items-center", "justify-center", "text-4xl", "mb-2",
            "border", "border-border");

        Span userName = new Span("Maria Staff");
        userName.addClassNames("font-bold", "text-xl", "leading-none", "text-text-main");

        Span emailText = new Span("mail@mail.mail");
        emailText.addClassNames("text-sm", "text-text-secondary", "mb-4");
        
        // Buttons: Increased height to h-20 (80px) for larger touch target
        // Profile Button: Primary color background, explicit WHITE text, Centered content, NO ICON
        Button profileBtn = new Button("Perfil");
        profileBtn.addClassNames("w-full", "h-20", 
            "bg-primary", 
            "rounded-lg", "hover:opacity-90", "transition-opacity",
            "font-medium", "flex", "items-center", "justify-center", "gap-4", "text-base");
        profileBtn.getStyle().set("color", "white"); // Force white text
        
        // Logout Button: Same style as Profile Button, NO ICON
        Button logoutBtn = new Button("Cerrar Sesión");
        logoutBtn.addClassNames("w-full", "h-20", 
            "bg-primary", 
            "rounded-lg", "hover:opacity-90", "transition-opacity",
            "font-medium", "flex", "items-center", "justify-center", "gap-4", "text-base");
        logoutBtn.getStyle().set("color", "white"); // Force white text
        
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
