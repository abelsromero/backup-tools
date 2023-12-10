package org.backup.tools.repositories.gdrive;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.backup.tools.repositories.gdrive.QueryBuilder.*;

class QueryBuilderTest {

    private static final String IS_FOLDER = "mimeType = 'application/vnd.google-apps.folder'";
    private static final String IS_FILE = "mimeType != 'application/vnd.google-apps.folder'";
    private static final String PARENT_IS_ROOT = "'root' in parents";

    @Test
    void shouldFilterFolders() {
        final String isFolder = isFolder();

        assertThat(isFolder).isEqualTo(IS_FOLDER);
    }

    @Test
    void shouldFilterFiles() {
        final String isFolder = isNotFolder();

        assertThat(isFolder).isEqualTo(IS_FILE);
    }

    @Test
    void shouldCombineQueries() {
        final String isFolder = isFolder();
        final String parentIs = parentIs("root");
        final String query = QueryBuilder.and(isFolder, parentIs);

        assertThat(query).isEqualTo(String.format("%s and %s", IS_FOLDER, PARENT_IS_ROOT));
    }


    @Test
    void shouldNotCombineSingleQuery() {
        final String isFolder = isFolder();
        final String query = QueryBuilder.and(isFolder);

        assertThat(query).isEqualTo(IS_FOLDER);
    }
}
