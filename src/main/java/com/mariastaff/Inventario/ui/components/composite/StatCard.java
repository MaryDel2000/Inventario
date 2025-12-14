package com.mariastaff.Inventario.ui.components.composite;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class StatCard extends VerticalLayout {

    public StatCard(String title, String value) {
        addClassNames("bg-bg-surface", "rounded-lg", "shadow", "p-6", "items-center", "justify-center");
        setWidth("200px");
        
        Span valSpan = new Span(value);
        valSpan.addClassNames("text-4xl", "font-bold", "text-primary");
        
        Span titleSpan = new Span(title);
        titleSpan.addClassNames("text-text-secondary", "font-medium");
        
        add(valSpan, titleSpan);
    }
}
