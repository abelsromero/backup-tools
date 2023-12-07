package org.backup.tools;

import java.io.File;

/**
 * Element to handle for a backup.
 */
public record Resource(File file, String hash, long size) {
}
