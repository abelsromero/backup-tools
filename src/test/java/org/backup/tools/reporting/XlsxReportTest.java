package org.backup.tools.reporting;

import org.apache.poi.ss.usermodel.Row;
import org.backup.tools.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.backup.tools.reporting.ReportResource.reportResource;
import static org.backup.tools.reporting.TestData.testResources;

class XlsxReportTest {

    @Test
    void shouldGenerate(@TempDir File tempDir) {
        final XlsxReport xlsxReport = new XlsxReport();
        final List<Resource> resources = testResources(tempDir);

        final File output = xlsxReport.generate(resources, tempDir);

        assertThat(output)
            .content(StandardCharsets.UTF_8)
            .isNotEmpty();
        final String tempDirAbsolutePath = tempDir.getAbsolutePath();
        assertThat(output.getParentFile().getAbsolutePath())
            .isEqualTo(tempDirAbsolutePath);

        final List<ReportResource> resourcesList = readResources(output);
        assertThat(resourcesList)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                reportResource("resource-1.bin", tempDirAbsolutePath, "12345678", 42),
                reportResource("resource-2.bin", tempDirAbsolutePath, "90123456", 66),
                reportResource("resource-3.bin", tempDirAbsolutePath, "78901234", 92)
            );
    }

    private List<ReportResource> readResources(File output) {
        final XlsxHandler handler = XlsxHandler.read(output);
        final List<ReportResource> resources = new ArrayList<>();

        final Iterator<Row> rowIterator = handler.rowIterator();
        // skip header
        rowIterator.next();
        while (rowIterator.hasNext()) {
            List<Object> values = handler.readRow(rowIterator.next());

            final ReportResource resource = new ReportResource();
            resource.setName((String) values.get(0));
            resource.setLocation((String) values.get(1));
            resource.setHash((String) values.get(2));
            resource.setSize((Long) values.get(3));

            resources.add(resource);
        }

        return resources;
    }

}
