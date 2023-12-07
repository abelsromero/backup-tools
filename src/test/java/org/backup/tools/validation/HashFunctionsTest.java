package org.backup.tools.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HashFunctionsTest {

    @ParameterizedTest
    @MethodSource("stringArgumentsProvider")
    void shouldProcessFile(HashFunction function, String inputFile, String expected) {
        final File file = new File(fromClasspath(inputFile));

        final String hash = function.hash(file);

        assertThat(hash).isEqualTo(expected);
    }

    private String fromClasspath(String file) {
        return this.getClass().getResource(file).getFile();
    }

    private static Stream<Arguments> stringArgumentsProvider() {

        var md5sum = new MessageDigester.Md5sum();
        var sha256sum = new MessageDigester.Sha256sum();
        // TODO empty files
        return Stream.of(
            Arguments.of(md5sum, "/test-resource.yaml", "bd8559a2f6b0640d3f599c5c21d8ae32"),
            Arguments.of(sha256sum, "/test-resource.yaml", "788f8fe20774921c652cfac70078cd7fca41ed2e970a5f794852f4cb290c6e30"),
            Arguments.of(md5sum, "/empty-resource.txt", "d41d8cd98f00b204e9800998ecf8427e"),
            Arguments.of(sha256sum, "/empty-resource.txt", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
        );
    }
}
