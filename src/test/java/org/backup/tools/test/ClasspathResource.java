package org.backup.tools.test;

import java.io.InputStream;

public class ClasspathResource {

    public static InputStream getResourceAsStream(String file) {
        return ClasspathResource.class.getResourceAsStream(file);
    }

    public static String getLocation(String file) {
        return ClasspathResource.class.getResource(file).getFile();
    }
}
