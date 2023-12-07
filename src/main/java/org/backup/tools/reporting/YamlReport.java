package org.backup.tools.reporting;

import org.backup.tools.Resource;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class YamlReport implements Report<File, File> {

    private static final String DEFAULT_REPORT_FILENAME = "report.yaml";

    // TODO explore jackson. Snakeyaml is simple but limitant since
    //  it does not support records.
    @Override
    public File generate(Collection<Resource> resources, File outputDir) {
        Map<String, String> metadata = generatedMetadata();
        List<Map> processesResources = generatedResources(resources);

        try {
            final File output = new File(outputDir, DEFAULT_REPORT_FILENAME);


            final DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            var data = Map.of(
                "metadata", metadata,
                "resources", processesResources);
            new Yaml(options)
                .dump(data, new FileWriter(output));
            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> generatedMetadata() {
        // final LocalDateTime now1 = LocalDateTime.now();
        final var now = LocalDateTime.now();
        String format = DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss'Z'").format(now);
        return Map.of("creationTimestamp", format);
    }

    private static List<Map> generatedResources(Collection<Resource> resources) {
        return resources.stream()
            .map(resource -> {
                final File file = resource.file();

                // Snakeyaml does not support records
                final Map<String, Object> value = new HashMap<>();
                value.put("name", file.getName());
                value.put("hash", resource.hash());
                value.put("size", resource.size());
                String parent = file.getParent();
                if (parent != null) {
                    value.put("location", parent);
                }
                return value;
            }).collect(Collectors.toList());
    }
}
