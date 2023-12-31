package org.backup.tools.reporting;

import org.backup.tools.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.backup.tools.reporting.ReportResource.reportResource;
import static org.backup.tools.reporting.TestData.testResources;

class YamlReportTest {

    @Test
    void shouldGenerateResources(@TempDir File tempDir) {
        final YamlReport report = new YamlReport();
        final List<Resource> resources = testResources(tempDir);

        final File output = report.generate(resources, tempDir);

        assertThat(output)
            .content(StandardCharsets.UTF_8)
            .isNotEmpty();
        final String tempDirAbsolutePath = tempDir.getAbsolutePath();
        assertThat(output.getParentFile().getAbsolutePath())
            .isEqualTo(tempDirAbsolutePath);

        final ReportResourceList reportAsYaml = readResources(output);
        assertThat(reportAsYaml.getResources())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyInAnyOrder(
                reportResource("resource-1.bin", tempDirAbsolutePath, "12345678", 42),
                reportResource("resource-2.bin", tempDirAbsolutePath, "90123456", 66),
                reportResource("resource-3.bin", tempDirAbsolutePath, "78901234", 92)
            );
    }

    @Test
    void shouldGenerateMetadata(@TempDir File tempDir) {
        final YamlReport report = new YamlReport();
        final List<Resource> resources = testResources(tempDir);

        final File output = report.generate(resources, tempDir);

        // metadata:
        //  creationTimestamp: "2023-12-03T17:25:53Z"

        Map<String, String> metadata = readMetadata(output);

        assertThat(metadata.get("creationTimestamp"))
            .matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z");
    }

    private ReportResourceList readResources(File file) {
        try {
            final Yaml yaml = new Yaml();
            // TODO make output name customizable
            return yaml.loadAs(new FileReader(file), ReportResourceList.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> readMetadata(File file) {
        return readResources(file).getMetadata();
    }

    @Test
    void shouldFailWhenOutputCannotBeWritten(@TempDir File tempDir) throws IOException {
        final File testFile = new File(tempDir, UUID.randomUUID().toString());
        Files.writeString(testFile.toPath(), "this.is.not=a_yaml");

        final YamlReport report = new YamlReport();
        Exception exception = catchException(() -> report.generate(List.of(), testFile));

        assertThat(exception)
            .isInstanceOf(RuntimeException.class);
    }
}
