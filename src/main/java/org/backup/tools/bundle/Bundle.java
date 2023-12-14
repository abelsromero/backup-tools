package org.backup.tools.bundle;

import java.io.File;
import java.io.IOException;

public class Bundle {

    private final String name;
    private final String location;

    public Bundle(String name, String location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Generates a single file.
     */
    public void pack(File output) throws IOException {
        if (output.isFile()) {
            throw new IllegalArgumentException("Output must be a directory");
        }
        final File file = new File(output, name + ".tar.gz");
        TarGzHandler.create(new File(location), file);
    }

    /*
    public unpack() throws IOException {
        try (InputStream fi = new FileInputStream(new File("input.tar.gz"));
             BufferedInputStream bi = new BufferedInputStream(fi);
             GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
             TarArchiveInputStream ti = new TarArchiveInputStream(gzi)) {

            ArchiveEntry entry;
            while ((entry = ti.getNextEntry()) != null) {

                // create a new path, remember check zip slip attack
                Path newPath = filename(entry, targetDir);
                //checking
                // copy TarArchiveInputStream to newPath
                Files.copy(ti, newPath);
            }
        }
    }
    */
}
