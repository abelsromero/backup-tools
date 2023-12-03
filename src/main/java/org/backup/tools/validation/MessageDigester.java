package org.backup.tools.validation;

import org.apache.commons.codec.binary.Hex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public sealed class MessageDigester implements HashFunction permits MessageDigester.Md5sum, MessageDigester.Sha256sum {

    private static final int BUFFER_SIZE = 1024 * 8;

    private final String algorithm;

    private MessageDigester(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String hash(File file) {
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);

            final FileInputStream stream = new FileInputStream(file);
            final byte[] buffer = new byte[BUFFER_SIZE];
            try (DigestInputStream dis = new DigestInputStream(stream, md)) {
                // Read the file in chunks and update the message digest
                while (dis.read(buffer) != -1) ;
            }

            return Hex.encodeHexString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class Md5sum extends MessageDigester {

        public Md5sum() {
            super("MD5");
        }
    }

    public static final class Sha256sum extends MessageDigester {

        public Sha256sum() {
            super("SHA-256");
        }
    }
}
