package info.jab.cli.io;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class CopyFiles {

    public void copyFilesToDirectory(List<String> files, String resourceBasePath, Path path) {
        try {
            // Create directory if it doesn't exist
            FileUtils.forceMkdir(path.toFile());

            // Copy files to the rules directory
            for (String fileName : files) {
                String resourcePath = resourceBasePath + fileName;
                // Use ClassLoader to get resource stream, expects path relative to classpath root
                try (InputStream resourceStream = CopyFiles.class.getClassLoader().getResourceAsStream(resourcePath)) {
                    if (Objects.isNull(resourceStream)) {
                        throw new IOException("Resource not found at " + resourcePath);
                    }
                    FileUtils.copyInputStreamToFile(resourceStream, path.resolve(fileName).toFile());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying rules files", e);
        }
    }
}
