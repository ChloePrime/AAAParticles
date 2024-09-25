package mod.chloeprime.aaaparticles.client.installer;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author ChloePrime
 */
public class JarExtractor {
    public static void extract(String from, File targetFile) throws IOException {
        try (var input = Objects.requireNonNull(JarExtractor.class.getClassLoader().getResource(from)).openStream()) {
            try (OutputStream output = FileUtils.openOutputStream(targetFile)) {
                input.transferTo(output);
            }
        }
    }

    public static boolean update(String resource, File target) throws IOException {
        boolean isUpToDate;
        try (var res = getResource(resource)) {
            try (var fs = Files.newInputStream(target.toPath())) {
                var resHash = DigestUtils.sha1(res);
                var fileHash = DigestUtils.sha1(fs);
                isUpToDate = Arrays.equals(resHash, fileHash);
            }
        }
        if (isUpToDate) {
            return false;
        }
        if (!target.delete()) {
            throw new IOException("Failed to delete %s".formatted(target.getCanonicalPath()));
        }
        extract(resource, target);
        return true;
    }

    private static InputStream getResource(String resource) throws IOException {
        return Objects.requireNonNull(JarExtractor.class.getClassLoader().getResource(resource)).openStream();
    }
}
