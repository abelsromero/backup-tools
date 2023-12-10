package org.backup.tools.repositories.gdrive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;

class FilesListPaginator {

    private final Drive.Files client;

    private FileList fileList;
    private int page = 0;
    private int index = 0;

    FilesListPaginator(Drive.Files client, FileList files) {
        this.client = client;
        this.fileList = files;
    }

    boolean hasNext() {
        return currentPageHasNext() || hasNextPage();
    }

    File next() throws IOException {
        if (currentPageHasNext()) {
            return fileList.getFiles().get(index++);
        } else {
            fileList = client.list()
                .setPageToken(fileList.getNextPageToken())
                .execute();
            page++;
            index = 0;
            return next();
        }
    }

    private boolean currentPageHasNext() {
        return index < fileList.getFiles().size();
    }

    private boolean hasNextPage() {
        return fileList.getNextPageToken() != null;
    }

    public int getPage() {
        return page;
    }

    public int getIndex() {
        return index;
    }
}
