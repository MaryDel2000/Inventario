package com.mariastaff.Inventario.ui.components.charts;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class StockByWarehouseChart extends Div {

    private final Map<String, BigDecimal> data;

    public StockByWarehouseChart(Map<String, BigDecimal> data) {
        this.data = data;
        addClassNames("w-full"); 
        setId("chart-" + UUID.randomUUID().toString());
        setHeight("400px"); 
        setWidthFull();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        renderChart();
    }

    private void renderChart() {
        if (data.isEmpty()) {
            setText(getTranslation("chart.stock_warehouse.no_data", "No data")); // Fallback text
            addClassNames("flex", "items-center", "justify-center", "text-gray-500");
            return;
        }

        String labels = data.keySet().stream()
                .map(k -> "'" + k + "'")
                .collect(Collectors.joining(","));
        String values = data.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        String title = "Distribución de Stock por Almacén"; 

        String js = String.format("""
            window.InventarioCharts = window.InventarioCharts || {};
            
            window.InventarioCharts.initStockChart = function(id, labels, values) {
                var options = {
                    series: values,
                    chart: {
                        type: 'pie',
                        height: '100%%',
                        fontFamily: 'Inter, sans-serif',
                        background: 'transparent',
                        animations: { enabled: true }
                    },
                    labels: labels,
                    legend: {
                        position: 'bottom',
                        labels: {
                            colors: document.documentElement.classList.contains('dark') ? '#94a3b8' : '#334155'
                        }
                    },
                    dataLabels: { enabled: true },
                    stroke: { show: false },
                    theme: {
                        mode: document.documentElement.classList.contains('dark') ? 'dark' : 'light',
                        palette: 'palette1' 
                    },
                    tooltip: {
                         y: {
                            formatter: function(val) {
                                return val + " u."
                            }
                        }
                    }
                };
                
                // Dark mode watcher for potential rerender? (Optional)
                // For now, static init based on current class.

                var chart = new ApexCharts(document.getElementById(id), options);
                chart.render();
            };

            if (!window.ApexCharts) {
                var script = document.createElement('script');
                script.src = 'https://cdn.jsdelivr.net/npm/apexcharts';
                script.onload = function() { 
                    window.InventarioCharts.initStockChart('%s', [%s], [%s]); 
                };
                document.head.appendChild(script);
            } else {
                window.InventarioCharts.initStockChart('%s', [%s], [%s]);
            }
            """, getId().get(), labels, values, getId().get(), labels, values);

        UI.getCurrent().getPage().executeJs(js);
    }
}
