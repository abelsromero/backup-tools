package org.backup.tools;

import org.apache.commons.io.FileUtils;
import org.backup.tools.validation.HashFunction;
import org.backup.tools.validation.MessageDigester;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reports duplicated files in a folder structure.
 */
public class FileComparator {

    private static final HashFunction hashFunction = new MessageDigester.Md5sum();

    public static void main(String[] args) throws IOException {

        final Path path = Path.of("/media/")
            //.resolve("bios")
            ;

        final List<Resource> data = Files.walk(path)
            .parallel()
            .filter(p -> !p.toFile().isDirectory())
            .map(Path::toFile)
            .map(file -> new Resource(file, hashFunction.hash(file), file.length()))
            .collect(Collectors.toList());

        System.out.println("Total files: " + data.size());

        Long totalSize = data.stream()
            .map(resource -> resource.size())
            .reduce(0L, Long::sum);
        System.out.println("Total files size: " + humanUnits(totalSize));

        Set<String> processed = new HashSet<>();
        Long uniquesSize = data.stream()
            .filter(resource -> !processed.contains(resource.hash()))
            .peek(resource -> processed.add(resource.hash()))
            .map(resource -> Long.valueOf(resource.size()))
            .reduce(0L, Long::sum);

        System.out.println("Unique files: " + processed.size());
        System.out.println("Unique files size: " + humanUnits(uniquesSize));
        System.out.println("Saved space files: " + humanUnits(totalSize - uniquesSize));
    }

    private static String humanUnits(Long uniquesSize) {
        return FileUtils.byteCountToDisplaySize(uniquesSize);
    }

}
