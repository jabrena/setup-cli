package info.jab.cli.io;

import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

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

    public void copyDirectory(Path sourceDir, Path destinationDir) {
        try {
            FileUtils.copyDirectory(sourceDir.toFile(), destinationDir.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Error copying directory from " + sourceDir + " to " + destinationDir, e);
        }
    }

    public void copyDirectoryExcludingFiles(Path sourceDir, Path destinationDir, List<String> excludedFiles) {
        try {
            FileFilter filter = pathname -> {
                if (pathname.isDirectory()) {
                    return true; // Always copy directories to explore their contents
                }
                return !excludedFiles.contains(pathname.getName());
            };
            FileUtils.copyDirectory(sourceDir.toFile(), destinationDir.toFile(), filter);
        } catch (IOException e) {
            throw new RuntimeException("Error copying directory from " + sourceDir + " to " + destinationDir + " excluding files", e);
        }
    }
}
