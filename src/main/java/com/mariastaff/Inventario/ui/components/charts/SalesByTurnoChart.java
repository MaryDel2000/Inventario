package com.mariastaff.Inventario.ui.components.charts;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.dependency.NpmPackage;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@NpmPackage(value = "apexcharts", version = "3.41.0")
public class SalesByTurnoChart extends Div {

    private final Map<String, BigDecimal> data;

    public SalesByTurnoChart(Map<String, BigDecimal> data) {
        this.data = data;
        addClassNames("w-full");
        setId("chart-" + UUID.randomUUID().toString());
        setHeight("400px");
        setWidthFull();

        renderChart();
    }

    private void renderChart() {
        String categories = data.keySet().stream()
                .map(k -> "'" + k + "'")
                .collect(Collectors.joining(","));

        String seriesData = data.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String seriesName = getTranslation("chart.sales_by_turno.series", "Ventas");

        // ApexCharts options (Bar chart)
        // Ensure namespace 'InventarioCharts' is used
        String js = String.format("""
                window.InventarioCharts = window.InventarioCharts || {};

                window.InventarioCharts.initSalesByTurnoChart = function(id, categories, values, seriesName) {
                    var options = {
                        series: [{
                            name: seriesName,
                            data: [%s]
                        }],
                        chart: {
                            type: 'bar',
                            height: '100%%',
                            toolbar: { show: false },
                            fontFamily: 'Inter, sans-serif'
                        },
                        plotOptions: {
                            bar: {
                                borderRadius: 4,
                                horizontal: false,
                                columnWidth: '55%%',
                            }
                        },
                        dataLabels: {
                            enabled: false
                        },
                        stroke: {
                            show: true,
                            width: 2,
                            colors: ['transparent']
                        },
                        xaxis: {
                            categories: [%s],
                            labels: {
                                style: { colors: '#64748b' }
                            }
                        },
                        yaxis: {
                            title: {
                                text: seriesName
                            },
                            labels: {
                                style: { colors: '#64748b' },
                                formatter: function (value) {
                                    return "$" + value.toFixed(2);
                                }
                            }
                        },
                        fill: {
                            opacity: 1,
                            colors: ['#0ea5e9'] // Sky-500
                        },
                        tooltip: {
                            y: {
                                formatter: function (val) {
                                    return "$" + val.toFixed(2);
                                }
                            },
                            theme: document.documentElement.classList.contains('dark') ? 'dark' : 'light'
                        },
                        grid: {
                            borderColor: '#f1f5f9',
                            strokeDashArray: 4
                        }
                    };

                    var chart = new ApexCharts(document.querySelector("#" + id), options);
                    chart.render();
                };

                setTimeout(() => window.InventarioCharts.initSalesByTurnoChart('%s', [%s], [%s], '%s'), 100);
                """, seriesData, categories, getId().get(), categories, seriesData, seriesName);

        getElement().executeJs(js);
    }
}
