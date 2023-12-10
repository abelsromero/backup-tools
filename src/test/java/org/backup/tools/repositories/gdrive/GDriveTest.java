package org.backup.tools.repositories.gdrive;

import com.google.api.services.drive.model.File;
import org.backup.tools.test.IntegratedTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegratedTest
class GDriveTest {

    private static GDrive drive;
    private static String tempFolderId;
    private static File testFolder;

    @BeforeAll
    static void setup() throws IOException {
        final Configuration configuration = Configuration.load();

        drive = GDriveFactory.getConnection(configuration.application(), configuration.options());
        assertThat(drive).isNotNull();

        File folder = drive.listFolders("root")
            .stream()
            .filter(f -> f.getName().equals("temp"))
            .findFirst()
            .get();
        assertThat(folder.getId()).isNotBlank();

        tempFolderId = folder.getId();
        testFolder = drive.createFolder("test-" + UUID.randomUUID(), tempFolderId);
    }

    @AfterAll
    static void cleanup() throws IOException {
        if (drive != null && testFolder != null) {
            drive.delete(testFolder.getId());
        }
    }

    @Test
    void shouldCreateFolder() throws IOException {
        File folder = drive.createFolder("test-" + UUID.randomUUID(), tempFolderId);

        assertThat(folder.getId()).isNotBlank();

        drive.delete(folder.getId());
    }

    @Test
    void shouldCreateFileAndGetInfo() throws IOException {
        var fileContent = getTestFileContent();
        File file = drive.uploadFile(fileContent, testFolder.getId(), false);

        assertThat(file.getId()).isNotBlank();

        File infoInfo = drive.getInfo(file.getId());
        assertThat(infoInfo.getId()).isEqualTo(file.getId());
        assertThat(infoInfo.getName()).isEqualTo("test-resource.yaml");
        assertThat(infoInfo.getMd5Checksum()).isEqualTo("bd8559a2f6b0640d3f599c5c21d8ae32");

        drive.delete(file.getId());
    }

    @Test
    void shouldCreateLockedFile() throws IOException {
        var fileContent = getTestFileContent();
        File file = drive.uploadFile(fileContent, testFolder.getId(), true);

        assertThat(file.getId()).isNotBlank();

        File infoInfo = drive.getInfo(file.getId(), "id,contentRestrictions");
        assertThat(infoInfo.getId()).isEqualTo(file.getId());
        assertThat(infoInfo.getContentRestrictions().get(0).getReadOnly()).isTrue();

        drive.delete(file.getId());
    }

    @Test
    void shouldLockFile() throws IOException {
        var fileContent = getTestFileContent();

        File file = drive.uploadFile(fileContent, testFolder.getId(), false);
        assertIsReadOnlyFile(file, false);

        drive.lockFile(file.getId());
        assertIsReadOnlyFile(file, true);

        drive.delete(file.getId());
    }

    private void assertIsReadOnlyFile(File file, boolean isReadOnly) throws IOException {
        File infoInfo = drive.getInfo(file.getId(), "id,contentRestrictions");
        assertThat(infoInfo.getId()).isEqualTo(file.getId());
        if (isReadOnly) {
            assertThat(infoInfo.getContentRestrictions().get(0).getReadOnly()).isEqualTo(isReadOnly);
        } else {
            if (infoInfo.getContentRestrictions() != null) {
                assertThat(infoInfo.getContentRestrictions().get(0).getReadOnly()).isEqualTo(isReadOnly);
            }
        }
    }

    private java.io.File getTestFileContent() {
        return new java.io.File("src/test/resources/test-resource.yaml");
    }
}


