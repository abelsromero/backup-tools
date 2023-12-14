package org.backup.tools.bundle;

import org.backup.tools.test.ClasspathResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BundleTest {

    @Test
    void shouldCreateABundle() throws IOException {
        final String bundleRoot = ClasspathResource.getLocation("/configs");

        final String name = "test-bundle-" + UUID.randomUUID();
        final Bundle bundle = new Bundle(name, bundleRoot);
        final File output = new File("build");
        bundle.pack(output);

        final File expected = new File(output, name + ".tar.gz");
        assertThat(expected).isNotEmpty();

        List<String> files = TarGzHandler.list(expected);
        assertThat(files)
            .containsExactlyInAnyOrder("test-config.yaml", "fake-credentials.json");
    }

    @Test
    void shouldCreateABundleWithSubPaths(@TempDir File tempDir) throws IOException {
        final File root1 = new File(tempDir, "folder-01");
        root1.mkdir();
        final File root2 = new File(tempDir, "folder-02");
        root2.mkdir();
        new File(root1, "folder-11").mkdir();
        new File(root1, "folder-12").mkdir();
        new File(root2, "folder-21").mkdir();

        final String name = "test-bundle-" + UUID.randomUUID();
        final Bundle bundle = new Bundle(name, tempDir.getAbsolutePath());
        final File output = new File("build");
        bundle.pack(output);

        final File expected = new File(output, name + ".tar.gz");
        assertThat(expected).isNotEmpty();

        List<String> files = TarGzHandler.list(expected);
        assertThat(files)
            .containsExactlyInAnyOrder(
                "folder-01/",
                "folder-01/folder-11/",
                "folder-01/folder-12/",
                "folder-02/",
                "folder-02/folder-21/"
            );
    }
}
