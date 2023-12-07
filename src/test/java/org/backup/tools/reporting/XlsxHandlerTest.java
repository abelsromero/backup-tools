package org.backup.tools.reporting;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class XlsxHandlerTest {

    @TempDir
    private File tempDir;

    @Test
    void shouldReadWorkbook(@TempDir File tempDir) {
        final File file = new TestWorkbook(tempDir).create();

        XlsxHandler handler = XlsxHandler.read(file);

        assertThat(handler).isNotNull();
    }

    @Test
    void shouldAddRow(@TempDir File tempDir) {
        final TestWorkbook testWorkbook = new TestWorkbook(tempDir);
        final File file = testWorkbook.createEmpty();

        XlsxHandler handler = XlsxHandler.read(file);
        Row row = handler.addRow("one", 2, Boolean.TRUE);

        final Sheet sheet = row.getSheet();
        assertThat(sheet.getFirstRowNum()).isEqualTo(0);
        assertThat(sheet.getLastRowNum()).isEqualTo(0);
    }

    @Test
    void shouldAddRows(@TempDir File tempDir) {
        final TestWorkbook testWorkbook = new TestWorkbook(tempDir);
        final File file = testWorkbook.createEmpty();

        XlsxHandler handler = XlsxHandler.read(file);
        Row row = null;
        int rows = 4;
        for (int i = 0; i < rows; i++) {
            row = handler.addRow("one", 2, Boolean.TRUE);
        }

        final Sheet sheet = row.getSheet();
        assertThat(sheet.getFirstRowNum()).isEqualTo(0);
        assertThat(sheet.getLastRowNum()).isEqualTo(rows - 1);
    }

    @Test
    void shouldReadStringColumn(@TempDir File tempDir) {
        shouldReadValue(0, "one", tempDir);
    }

    @Test
    void shouldReadNumericColumn() {
        // Always returned as Long
        shouldReadValue(1, 2L, tempDir);
        shouldReadValue(2, 3L, tempDir);
    }

    @Test
    void shouldReadBooleanColumn() {
        shouldReadValue(3, true, tempDir);
    }

    void shouldReadValue(int pos, Object expected, File tempDir) {
        final TestWorkbook testWorkbook = new TestWorkbook(tempDir);
        final File file = testWorkbook.createEmpty();

        XlsxHandler handler = XlsxHandler.read(file);
        Row row = handler.addRow("one", Integer.valueOf(2), Long.valueOf(3l), Boolean.TRUE);
        List<Object> values = handler.readRow(row);

        assertThat(values.get(pos)).isEqualTo(expected);
    }

    @Test
    void shouldAllowIteration() {
        final TestWorkbook testWorkbook = new TestWorkbook(tempDir);
        final File file = testWorkbook.create();

        XlsxHandler handler = XlsxHandler.read(file);
        Iterator<Row> rowIterator = handler.rowIterator();
        int count = 0;
        while (rowIterator.hasNext()) {
            rowIterator.next();
            count++;
        }

        assertThat(count).isEqualTo(2);
    }

    class TestWorkbook {

        private final File file;
        private final Workbook wb = new XSSFWorkbook();

        TestWorkbook(File location) {
            this.file = new File(location, UUID.randomUUID() + ".xlsx");
        }

        private File create() {
            final Sheet sheet = wb.createSheet("test-data");
            sheet.createRow(0)
                .createCell(0)
                .setCellValue("some test");
            sheet.createRow(1)
                .createCell(0)
                .setCellValue("some test");

            return write();
        }

        private File createEmpty() {
            wb.createSheet("test-data");
            return write();
        }

        private File write() {
            try {
                wb.write(new FileOutputStream(file));
                return file;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
