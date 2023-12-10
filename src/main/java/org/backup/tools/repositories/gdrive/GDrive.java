package org.backup.tools.repositories.gdrive;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ContentRestriction;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.tika.Tika;
import org.backup.tools.repositories.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GDrive implements Repository {

    private static final String FOLDER_EMOJI = "\uD83D\uDCC1";
    private static final String FILE_EMOJI = "\uD83D\uDDCE";

    private final Drive.Files client;

    GDrive(Drive.Files service) {
        this.client = service;
    }

    /**
     * Notes:
     * - Owner is the user who granted permission (not the app)
     * - if '.setFields("parents")' not used, it returns id, kind, mimeType & name
     * - Parent for parent-less folder is "My Drive" (id -> 0ALKp2xRFXkNQUk9PVA)
     * - default MimeType: application/octet-stream
     */
    public File createFolder(String name, String parent) throws IOException {
        final File metadata = new File()
            .setName(name)
            .setMimeType("application/vnd.google-apps.folder")
            .setParents(List.of(parent));

        return client.create(metadata)
            .setFields("id,name,parents")
            .execute();
    }

    /**
     * TODO
     *  * Consider other lightweight alternative content-type detections or even, do we need it for backups fi we only store compressed bundles?
     * Notes:
     *  - Does not fail if file with same name exists
     *  - Can upload big files (tested 1 GB)
     *  - fields set are for the returned values
     */
    public File uploadFile(java.io.File content, String parent, boolean lock) throws IOException {
        final File metadata = new File()
            .setName(content.getName())
            .setParents(List.of(parent));

        if (lock) {
            metadata.setContentRestrictions(List.of(new ContentRestriction().setReadOnly(true)));
        }

        return client.create(metadata, new FileContent(detectContentType(content), content))
            .setFields("id")
            .execute();
    }

    public File getInfo(String id) throws IOException {
        return getInfo(id, "id,name,md5Checksum");
    }

    public File getInfo(String id, String fields) throws IOException {
        return client.get(id)
            .setFields(fields)
            .execute();
    }

    /**
     * Notes:
     * - Only files created via app can be deleted
     */
    public void delete(String fileId) throws IOException {
        client.delete(fileId)
            .execute();
    }

    public void lockFile(String fileId) throws IOException {
        final File metadata = new File()
            .setContentRestrictions(List.of(new ContentRestriction().setReadOnly(true)));

        final File execute = client.update(fileId, metadata)
            .setFields("contentRestrictions")
            .execute();

        System.out.println(execute);
    }

    public List<File> listFolders(String parent) throws IOException {
        return listFiles(parent, true);
    }

    public List<File> listFiles(String parent) throws IOException {
        return listFiles(parent, false);
    }

    /**
     * Learnt:
     * - Seems filtering to "root" shows files in "My Drive"
     * - Can filter by folder
     * - md5Checksum,sha1Checksum,sha256Checksum are returned for non gdocs files and folders.
     * - There's no direct method to get full path: must obtain parents recursively.
     */
    List<File> listFiles(String parent, boolean onlyFolders) throws IOException {
        final FileList result = client.list()
            .setPageSize(1000)
            .setQ(QueryBuilder.and(QueryBuilder.parentIs(parent), onlyFolders ? QueryBuilder.isFolder() : QueryBuilder.isNotFolder()))
            .setFields("nextPageToken,files(id,name,mimeType,md5Checksum)")
            .setOrderBy("name")
            .execute();

        final var paginator = new FilesListPaginator(client, result);
        final var allFiles = new ArrayList<File>();

        while (paginator.hasNext()) {
            final File file = paginator.next();
            allFiles.add(file);
        }
        return allFiles;
    }

    public String getLocation(File file) throws IOException {
        if (file.getParents() == null)
            return FOLDER_EMOJI + " ";

        final String parentId = file.getParents().get(0);
        final File parentInfo = client.get(parentId)
            .setFields("id,name,parents")
            .execute();

        final String name = parentInfo.getName();
        return getLocation(parentInfo) + "/" + name;
    }

    private static String detectContentType(java.io.File content) throws IOException {
        return new Tika().detect(content);
    }
}
