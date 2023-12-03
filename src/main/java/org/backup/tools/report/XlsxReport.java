package org.backup.tools.report;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.backup.tools.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class XlsxReport implements Report<File, File> {

    private static final String DEFAULT_REPORT_FILENAME = "report.xlsx";

    @Override
    public File generate(Collection<Resource> collection, File outputDir) {

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();

        XlsxHandler xlsxHandler = new XlsxHandler(sheet);
        xlsxHandler.addRow("NAME", "LOCATION", "HASH", "SIZE");

        for (Resource resource : collection) {
            File file = resource.file();
            xlsxHandler.addRow(file.getName(), file.getParent(), resource.hash(), resource.size());
        }

        try {
            final File file = new File(outputDir, DEFAULT_REPORT_FILENAME);
            try (FileOutputStream stream = new FileOutputStream(file)) {
                wb.write(stream);
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
