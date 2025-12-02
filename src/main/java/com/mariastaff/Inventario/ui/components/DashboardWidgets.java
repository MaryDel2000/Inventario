package com.mariastaff.Inventario.ui.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@NpmPackage(value = "apexcharts", version = "3.41.0")
@JsModule("apexcharts/dist/apexcharts.min.js")
public class DashboardWidgets extends VerticalLayout {

    public DashboardWidgets() {
        addClassNames("w-full", "space-y-6");
        setSpacing(false);
        setPadding(false);

        // Product Cards
        HorizontalLayout cardsLayout = new HorizontalLayout();
        cardsLayout.addClassNames("w-full", "grid", "grid-cols-1", "md:grid-cols-3", "gap-6");
        cardsLayout.setSpacing(false); // Use gap-6
        
        cardsLayout.add(createCard("Total Productos", "1,234", "border-l-4 border-blue-500"));
        cardsLayout.add(createCard("Ventas Hoy", "$5,678", "border-l-4 border-green-500"));
        cardsLayout.add(createCard("Stock Bajo", "12", "border-l-4 border-red-500"));
        add(cardsLayout);

        // Charts
        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.addClassNames("w-full", "grid", "grid-cols-1", "md:grid-cols-2", "gap-6");
        chartsLayout.setSpacing(false);
        
        Div chart1 = createApexChart("chart-sales", "Ventas Mensuales");
        Div chart2 = createApexChart("chart-stock", "Distribución de Stock");
        
        chartsLayout.add(chart1, chart2);
        add(chartsLayout);
    }

    private Div createCard(String title, String value, String borderClass) {
        Div card = new Div();
        // Tailwind: bg-surface, shadow, rounded, padding
        card.addClassNames("bg-[var(--color-bg-surface)]", "shadow-sm", "rounded-lg", "p-6", borderClass);
        
        H3 titleEl = new H3(title);
        titleEl.addClassNames("text-sm", "font-medium", "text-[var(--color-text-muted)]", "uppercase", "tracking-wider");
        
        Span valueEl = new Span(value);
        valueEl.addClassNames("text-2xl", "font-bold", "text-[var(--color-text-main)]", "mt-2", "block");
        
        card.add(titleEl, valueEl);
        return card;
    }

    private Div createApexChart(String id, String title) {
        Div chartContainer = new Div();
        chartContainer.setId(id);
        chartContainer.addClassNames("bg-[var(--color-bg-surface)]", "shadow-sm", "rounded-lg", "p-6", "h-80");

        // Simple JS injection to render chart
        UI.getCurrent().getPage().executeJs(
            "setTimeout(() => {" +
            "  if (!window.ApexCharts) return;" +
            "  var options = {" +
            "    chart: { type: 'bar', height: '100%', fontFamily: 'inherit', background: 'transparent' }," +
            "    theme: { mode: document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light' }," +
            "    series: [{ name: 'Data', data: [30, 40, 35, 50, 49, 60, 70, 91, 125] }]," +
            "    xaxis: { categories: [1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998, 1999] }," +
            "    colors: ['#3b82f6']" +
            "  };" +
            "  var chart = new ApexCharts(document.querySelector('#' + $0), options);" +
            "  chart.render();" +
            "  // Listen for theme changes to update chart theme if needed (simplified)" +
            "}, 500);", id
        );

        return chartContainer;
    }
}
