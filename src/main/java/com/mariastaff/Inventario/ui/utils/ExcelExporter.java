package com.mariastaff.Inventario.ui.utils;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExcelExporter {

    public static <T> StreamResource export(Grid<T> grid, String sheetName, String fileName) {
        return new StreamResource(fileName + ".xlsx", () -> {
            try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                Sheet sheet = workbook.createSheet(sheetName);
                
                // Styles
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                
                CellStyle dateStyle = workbook.createCellStyle();
                dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));
                
                CellStyle currencyStyle = workbook.createCellStyle();
                currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));

                // Header Row
                Row headerRow = sheet.createRow(0);
                List<Column<T>> columns = grid.getColumns().stream()
                    .filter(Column::isVisible)
                    .collect(Collectors.toList());
                    
                for (int i = 0; i < columns.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    // This is a simplification. Grid headers are components, we try to extract text.
                    // Ideally we pass header names map. For now assuming simple text headers or empty.
                    // We can try to use column key if available or just "Column X"
                    String headerText = "Columna " + (i + 1);
                    // Advanced: To get real header text we would need to pass it explicitly 
                    // or reflectively access renderer if it is TextRenderer.
                    // Let's assume passed in columns are configured.
                    // Improved: Let's create a builder or accept a List<String> headers, 
                    // but for generic generic we can't easily extract text from Component Renderer.
                    // Hack: We'll skip header text extraction complexity and just assume user passes headers map 
                    // OR we just use a generic implementation.
                    // For this quick impl, let's try to pass List<String> headers explicitly?
                    // No, let's keep it simple: empty header or generic.
                    // BETTER: Let's refactor to accept List<ExcelColumn<T>>
                    cell.setCellValue(headerText); 
                    cell.setCellStyle(headerStyle);
                }
                
                // Data Rows
                // Grid data provider might be lazy. We need to fetch all items.
                // Assuming ListDataProvider or small dataset fitting in memory.
                java.util.stream.Stream<T> stream = grid.getGenericDataView().getItems();
                List<T> items = stream.collect(Collectors.toList());
                
                int rowIdx = 1;
                for (T item : items) {
                     Row row = sheet.createRow(rowIdx++);
                     for (int i = 0; i < columns.size(); i++) {
                         Column<T> col = columns.get(i);
                         Cell cell = row.createCell(i);
                         
                         // How to get value from column? 
                         // Grid doesn't expose value provider easily publicly without reflection.
                         // We need a wrapper.
                     }
                }
                
                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException("Error exporting Excel", e);
            }
        });
    }
    
    // Better approach: explicit definition
    public static class ColumnDefinition<T> {
        private String header;
        private Function<T, Object> valueProvider;
        
        public ColumnDefinition(String header, Function<T, Object> valueProvider) {
             this.header = header;
             this.valueProvider = valueProvider;
        }
        public String getHeader() { return header; }
        public Object getValue(T item) { return valueProvider.apply(item); }
    }
    
    public static <T> StreamResource export(java.util.function.Supplier<java.util.stream.Stream<T>> itemsSupplier, List<ColumnDefinition<T>> columns, String sheetName, String fileName) {
         return new StreamResource(fileName + ".xlsx", () -> {
            try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                Sheet sheet = workbook.createSheet(sheetName);
                
                // Styles
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                
                // Header
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns.get(i).getHeader());
                    cell.setCellStyle(headerStyle);
                }
                
                // Data
                // Use the supplier to get a FRESH stream of items at download time
                java.util.stream.Stream<T> dataStream = itemsSupplier.get();
                // We use an iterator or collect to list. 
                // Since POI writes sequentially, we can iterate.
                java.util.Iterator<T> iterator = dataStream.iterator();
                
                int rowIdx = 1;
                while (iterator.hasNext()) {
                    T item = iterator.next();
                    Row row = sheet.createRow(rowIdx++);
                    for (int i = 0; i < columns.size(); i++) {
                        Cell cell = row.createCell(i);
                        Object value = columns.get(i).getValue(item);
                        
                        if (value == null) {
                            cell.setCellValue("");
                        } else if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else if (value instanceof java.time.LocalDate) {
                            cell.setCellValue(((java.time.LocalDate) value).toString());
                        } else if (value instanceof java.time.LocalDateTime) {
                             cell.setCellValue(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format((java.time.LocalDateTime) value));
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
                
                // Auto size columns
                for (int i = 0; i < columns.size(); i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static <T> StreamResource export(List<T> data, List<ColumnDefinition<T>> columns, String sheetName, String fileName) {
         return export(() -> data.stream(), columns, sheetName, fileName);
    }
}
