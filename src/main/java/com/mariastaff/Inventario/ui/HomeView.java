package com.mariastaff.Inventario.ui;

import com.mariastaff.Inventario.ui.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        addClassNames("bg-gray-50", "h-full", "p-l");
        setSpacing(false);
        setPadding(false);

        // Hero Section
        Div hero = new Div();
        hero.addClassNames("bg-gradient-to-r", "from-blue-600", "to-indigo-700", "rounded-xl", "p-10", "text-white", "shadow-lg", "mb-8", "w-full");
        
        H1 title = new H1("Bienvenido a Inventario");
        title.addClassNames("text-4xl", "font-bold", "mb-4");
        
        Paragraph subtitle = new Paragraph("Gestiona tus recursos con estilo y eficiencia. Esta es una demostración de Vaadin con Tailwind CSS.");
        subtitle.addClassNames("text-xl", "opacity-90", "max-w-2xl");

        hero.add(title, subtitle);

        // Stats Grid
        Div statsGrid = new Div();
        statsGrid.addClassNames("grid", "grid-cols-1", "md:grid-cols-3", "gap-6", "w-full", "mb-8");
        
        statsGrid.add(
            createStatCard("Productos", "1,234", "text-blue-600", VaadinIcon.PACKAGE),
            createStatCard("Ventas", "$45.2k", "text-green-600", VaadinIcon.CHART),
            createStatCard("Clientes", "892", "text-purple-600", VaadinIcon.USERS)
        );

        // Action Section
        Div actionSection = new Div();
        actionSection.addClassNames("bg-white", "p-8", "rounded-xl", "shadow-md", "w-full", "flex", "flex-col", "items-center", "text-center");
        
        H2 actionTitle = new H2("¿Listo para comenzar?");
        actionTitle.addClassNames("text-2xl", "font-bold", "mb-4", "text-gray-800");
        
        Button actionBtn = new Button("Crear Nuevo Reporte");
        actionBtn.addClassNames("bg-blue-600", "text-white", "py-3", "px-6", "rounded-lg", "font-semibold", "hover:bg-blue-700", "transition-colors", "cursor-pointer");
        
        actionSection.add(actionTitle, actionBtn);

        add(hero, statsGrid, actionSection);
    }

    private Div createStatCard(String label, String value, String colorClass, VaadinIcon icon) {
        Div card = new Div();
        card.addClassNames("bg-white", "p-6", "rounded-xl", "shadow-sm", "border", "border-gray-100", "flex", "items-center", "space-x-4", "hover:shadow-md", "transition-shadow");

        Div iconBox = new Div();
        iconBox.addClassNames("p-3", "rounded-full", "bg-gray-50");
        Span iconSpan = new Span(icon.create());
        iconSpan.addClassNames(colorClass, "text-xl");
        iconBox.add(iconSpan);

        Div content = new Div();
        Paragraph labelP = new Paragraph(label);
        labelP.addClassNames("text-gray-500", "text-sm", "font-medium");
        
        H2 valueH = new H2(value);
        valueH.addClassNames("text-2xl", "font-bold", "text-gray-900", "m-0");

        content.add(labelP, valueH);
        card.add(iconBox, content);
        
        return card;
    }
}
