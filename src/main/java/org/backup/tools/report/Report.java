package org.backup.tools.report;

import org.backup.tools.Resource;

import java.util.Collection;

public interface Report<T, V> {

    T generate(Collection<Resource> resources, V options);
}
