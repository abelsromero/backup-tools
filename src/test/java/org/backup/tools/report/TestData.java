package org.backup.tools.report;

import org.backup.tools.Resource;

import java.io.File;
import java.util.List;

class TestData {

    static List<Resource> testResources(File tempDir) {
        return List.of(
            new Resource(new File(tempDir, "resource-1.bin"), "12345678", 42),
            new Resource(new File(tempDir, "resource-2.bin"), "90123456", 66),
            new Resource(new File(tempDir, "resource-3.bin"), "78901234", 92)
        );
    }
}
