package com.mariastaff.Inventario.ui.components.base;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

public class ExpandableSidebarNavItem extends VerticalLayout {

    private final HorizontalLayout mainItemLayout;
    private final VerticalLayout subItemsLayout;
    private final AppLabel label;
    private final Div iconDiv;
    private final Div chevronDiv;
    private final List<AppNavItem> subItems = new ArrayList<>();
    private boolean expanded = false;
    private final Class<? extends Component> defaultNavigationTarget;

    public ExpandableSidebarNavItem(String labelText, AppIcon icon, Class<? extends Component> defaultNavigationTarget) {
        this.defaultNavigationTarget = defaultNavigationTarget;
        
        setPadding(false);
        setSpacing(false);
        setWidthFull();
        addClassNames("expandable-nav-item");

        // Main item (clickable header)
        mainItemLayout = new HorizontalLayout();
        mainItemLayout.setWidthFull();
        mainItemLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        mainItemLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        mainItemLayout.setPadding(false);
        mainItemLayout.setSpacing(false);
        mainItemLayout.addClassNames("flex", "items-center", "gap-3", "w-full", "p-2", "rounded-lg", "cursor-pointer", "transition-colors", "hover:bg-bg-secondary");

        // Left section: icon + label
        HorizontalLayout leftSection = new HorizontalLayout();
        leftSection.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSection.setSpacing(true);
        leftSection.setPadding(false);
        
        iconDiv = new Div(icon.create());
        iconDiv.addClassNames("text-xl", "text-[#607d8b]", "flex", "items-center");
        
        label = new AppLabel(labelText);
        label.removeClassNames("text-text-main");
        label.addClassNames("text-[#607d8b]");
        
        leftSection.add(iconDiv, label);

        // Right section: chevron icon
        chevronDiv = new Div(VaadinIcon.CHEVRON_DOWN.create());
        chevronDiv.addClassNames("text-[#607d8b]", "transition-transform", "duration-200");
        chevronDiv.getStyle().set("transform", "rotate(0deg)");

        mainItemLayout.add(leftSection, chevronDiv);
        
        // Click listener to toggle
        mainItemLayout.getElement().addEventListener("click", e -> toggle());

        // Sub-items container
        subItemsLayout = new VerticalLayout();
        subItemsLayout.setPadding(false);
        subItemsLayout.setSpacing(false);
        subItemsLayout.addClassNames("pl-8", "overflow-hidden", "transition-all", "duration-300");
        subItemsLayout.setVisible(false);
        subItemsLayout.getStyle().set("max-height", "0px");

        add(mainItemLayout, subItemsLayout);
    }

    public void addSubItem(AppNavItem subItem) {
        subItems.add(subItem);
        subItem.addClassNames("py-1");
        subItem.getStyle().set("margin", "5px");
        subItemsLayout.add(subItem);
    }

    private void toggle() {
        expanded = !expanded;
        if (expanded) {
            // Remove any visibility style set by JavaScript
            subItemsLayout.getElement().executeJs("this.style.removeProperty('visibility');");
            subItemsLayout.setVisible(true);
            subItemsLayout.getStyle().set("max-height", (subItems.size() * 50) + "px");
            chevronDiv.getStyle().set("transform", "rotate(180deg)");
        } else {
            subItemsLayout.getStyle().set("max-height", "0px");
            chevronDiv.getStyle().set("transform", "rotate(0deg)");
            // Delay hiding to allow animation
            subItemsLayout.getElement().executeJs(
                "setTimeout(() => { this.style.visibility = 'hidden'; }, 300);"
            );
        }
    }

    public void setCollapsed(boolean sidebarCollapsed) {
        // When sidebar is collapsed, hide the expandable functionality
        if (sidebarCollapsed) {
            label.setVisible(false);
            chevronDiv.setVisible(false);
            subItemsLayout.setVisible(false);
            mainItemLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        } else {
            label.setVisible(true);
            chevronDiv.setVisible(true);
            mainItemLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        }
    }

    public Class<? extends Component> getDefaultNavigationTarget() {
        return defaultNavigationTarget;
    }
}
