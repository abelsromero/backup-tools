package org.backup.tools.bundle;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Cannot compress a folder and store output into the same path
// Alternative https://gist.github.com/tadeboro/fb106dabc07bdfab50fed5e485b72410
public class TarGzHandler {

    static void create(File sourceDirectory, File outputFile) throws IOException {
        try (
            var fileOutputStream = new FileOutputStream(outputFile);
            var gzipOutputStream = new GzipCompressorOutputStream(fileOutputStream);
            var tarOutputStream = new TarArchiveOutputStream(gzipOutputStream)
        ) {
            addFiles(tarOutputStream, sourceDirectory, "");
        }
    }

    private static void addFiles(TarArchiveOutputStream tarOutputStream, File file, String entryName) throws IOException {
        final TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);
        tarOutputStream.putArchiveEntry(tarEntry);
        if (file.isFile()) {
            IOUtils.copy(new FileInputStream(file), tarOutputStream);
            tarOutputStream.closeArchiveEntry();
        } else if (file.isDirectory()) {
            tarOutputStream.closeArchiveEntry();
            for (File childFile : file.listFiles()) {
                final String newEntry = entryName + "/" + childFile.getName();
                addFiles(tarOutputStream, childFile, newEntry);
            }
        }
    }

    static List<String> list(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             GzipCompressorInputStream gzIn = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn)
        ) {
            TarArchiveEntry entry;
            final List<String> files = new ArrayList<>();
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (!entry.getName().equals("/"))
                    files.add(entry.getName());
            }
            return files;
        }
    }
}
