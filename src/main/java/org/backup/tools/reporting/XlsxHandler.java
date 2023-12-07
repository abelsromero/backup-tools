package org.backup.tools.reporting;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class XlsxHandler {

    private final Sheet sheet;
    private final Workbook workbook;

    private int rowIndex = 0;

    XlsxHandler(Workbook workbook) {
        this.workbook = workbook;
        if (workbook.getNumberOfSheets() == 0) {
            this.sheet = workbook.createSheet();
        } else {
            this.sheet = workbook.getSheetAt(0);
        }
    }

    Row addRow(Object... values) {
        final Row row = sheet.createRow(rowIndex);
        int columnIndex = 0;

        for (Object value : values) {
            Cell cell = row.createCell(columnIndex);
            switch (value) {
                case String s -> cell.setCellValue(s);
                case Long l -> cell.setCellValue(l);
                case Boolean b -> cell.setCellValue(b);
                case Integer i -> cell.setCellValue(i);
                default -> throw new IllegalArgumentException("Unexpected result: " + value);
            }
            columnIndex++;
        }
        rowIndex++;
        return row;
    }

    Iterator<Row> rowIterator() {
        return sheet.rowIterator();
    }

    List<Object> readRow(Row row) {
        List<Object> values = new ArrayList<>();
        for (short i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            switch (cell.getCellType()) {
                case NUMERIC -> values.add(Double.valueOf(cell.getNumericCellValue()).longValue());
                case BOOLEAN -> values.add(cell.getBooleanCellValue());
                default -> values.add(cell.getStringCellValue());
            }

        }
        return Collections.unmodifiableList(values);
    }

    static XlsxHandler read(File file) {
        try {
            return new XlsxHandler(new XSSFWorkbook(file));
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(File destination) {
        try {
            try (FileOutputStream stream = new FileOutputStream(destination)) {
                workbook.write(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
