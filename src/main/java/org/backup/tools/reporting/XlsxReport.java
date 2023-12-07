package org.backup.tools.reporting;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.backup.tools.Resource;

import java.io.File;
import java.util.Collection;

public class XlsxReport implements Report<File, File> {

    private static final String DEFAULT_REPORT_FILENAME = "report.xlsx";

    @Override
    public File generate(Collection<Resource> collection, File outputDir) {

        final Workbook wb = new XSSFWorkbook();

        XlsxHandler xlsxHandler = new XlsxHandler(wb);
        xlsxHandler.addRow("NAME", "LOCATION", "HASH", "SIZE");

        for (Resource resource : collection) {
            File file = resource.file();
            xlsxHandler.addRow(file.getName(), file.getParent(), resource.hash(), resource.size());
        }

        final File file = new File(outputDir, DEFAULT_REPORT_FILENAME);
        xlsxHandler.write(file);

        return file;
    }
}
