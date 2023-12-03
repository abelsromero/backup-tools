package org.backup.tools.validation;

import java.io.File;

public interface HashFunction {

    String hash(File file);
}
