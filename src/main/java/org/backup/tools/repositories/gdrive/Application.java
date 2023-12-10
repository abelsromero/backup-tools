package org.backup.tools.repositories.gdrive;

import java.io.Reader;
import java.util.Collection;

public record Application(String name, Collection<String> scopes, Reader credentials) {
}
