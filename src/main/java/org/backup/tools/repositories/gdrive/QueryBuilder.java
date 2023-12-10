package org.backup.tools.repositories.gdrive;

import java.util.StringJoiner;

class QueryBuilder {

    static String and(String... parts) {
        final var stringJoiner = new StringJoiner(" and ");
        for (String part : parts)
            stringJoiner.add(part);
        return stringJoiner.toString();
    }

    static String isFolder() {
        return isFolder("=");
    }

    static String isNotFolder() {
        return isFolder("!=");
    }

    private static String isFolder(String operand) {
        return "mimeType " + operand + " 'application/vnd.google-apps.folder'";
    }

    public static String parentIs(String parent) {
        return String.format("'%s' in parents", parent);
    }

    public static String nameIs(String name) {
        return "name = '" + name + "'";
    }
}
