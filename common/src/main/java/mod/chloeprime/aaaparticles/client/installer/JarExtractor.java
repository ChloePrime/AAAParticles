package mod.chloeprime.aaaparticles.client.installer;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
}
