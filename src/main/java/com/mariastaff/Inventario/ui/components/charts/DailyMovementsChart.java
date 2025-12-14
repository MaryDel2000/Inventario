package com.mariastaff.Inventario.ui.components.charts;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class DailyMovementsChart extends Div {

    private final Map<LocalDate, Long> data;

    public DailyMovementsChart(Map<LocalDate, Long> data) {
        this.data = new TreeMap<>(data); // Sort by date
        addClassNames("w-full", "h-96", "bg-white", "dark:bg-gray-800", "rounded-xl", "shadow-sm", "p-6");
        setId("chart-" + UUID.randomUUID().toString());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        renderChart();
    }

    private void renderChart() {
        if (data.isEmpty()) {
            setText(getTranslation("chart.daily_movements.no_data"));
            addClassNames("flex", "items-center", "justify-center", "text-gray-500");
            return;
        }

        String categories = data.keySet().stream()
                .map(date -> "'" + date.toString() + "'")
                .collect(Collectors.joining(","));
        String seriesData = data.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        String seriesName = getTranslation("chart.daily_movements.title");

        // Use Java text block for JS
        String js = String.format("""
            if (!window.InventarioCharts) {
                window.InventarioCharts = {
                    initDailyMovementsChart: function(id, dates, values, seriesName) {
                        var options = {
                            series: [{
                                name: seriesName,
                                data: values
                            }],
                            chart: {
                                type: 'area',
                                height: '100%%',
                                fontFamily: 'Inter, sans-serif',
                                background: 'transparent',
                                toolbar: { show: false },
                                animations: { enabled: true }
                            },
                            dataLabels: { enabled: false },
                            stroke: { curve: 'smooth', width: 3 },
                            xaxis: {
                                categories: dates,
                                labels: { 
                                    style: { 
                                        colors: '#94a3b8',
                                        fontSize: '12px'
                                    } 
                                },
                                axisBorder: { show: false },
                                axisTicks: { show: false },
                                tooltip: { enabled: false }
                            },
                            yaxis: {
                                labels: { 
                                    style: { 
                                        colors: '#94a3b8', 
                                        fontSize: '12px' 
                                    } 
                                }
                            },
                            grid: {
                                borderColor: '#e2e8f0', // Light mode default
                                strokeDashArray: 4,
                                padding: { top: 0, right: 0, bottom: 0, left: 10 }
                            },
                            theme: {
                                mode: 'light' 
                            },
                            colors: ['#0ea5e9'], // Sky 500
                            fill: {
                                type: 'gradient',
                                gradient: {
                                    shadeIntensity: 1,
                                    opacityFrom: 0.4,
                                    opacityTo: 0.1,
                                    stops: [0, 90, 100]
                                }
                            },
                            tooltip: {
                                theme: 'light',
                                x: { format: 'dd MMM' }
                            }
                        };
                        
                        // Dark mode adjustments
                        if (document.documentElement.classList.contains('dark')) {
                             options.theme.mode = 'dark';
                             options.grid.borderColor = '#334155'; // Slate 700
                             options.chart.foreColor = '#94a3b8';
                             options.tooltip.theme = 'dark';
                        }

                        var chart = new ApexCharts(document.getElementById(id), options);
                        chart.render();
                    }
                };
            }

            if (!window.ApexCharts) {
                var script = document.createElement('script');
                script.src = 'https://cdn.jsdelivr.net/npm/apexcharts';
                script.onload = function() { 
                    window.InventarioCharts.initDailyMovementsChart('%s', [%s], [%s], '%s'); 
                };
                document.head.appendChild(script);
            } else {
                window.InventarioCharts.initDailyMovementsChart('%s', [%s], [%s], '%s');
            }
            """, getId().get(), categories, seriesData, seriesName, getId().get(), categories, seriesData, seriesName);

        UI.getCurrent().getPage().executeJs(js);
    }
}
