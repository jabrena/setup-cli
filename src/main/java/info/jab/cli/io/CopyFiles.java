package info.jab.cli.io;

import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

public class CopyFiles {

    /*
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
    */

    private static FileVisitResult createDirectoryAndContinue(Path source, Path destination, Path dir) throws IOException {
        // Convert to string to avoid ProviderMismatchException
        String relativePath = source.relativize(dir).toString();
        Path targetDir = destination.resolve(relativePath);
        Files.createDirectories(targetDir);
        return FileVisitResult.CONTINUE;
    }

    private static FileVisitResult copyFileAndContinue(Path source, Path destination, Path file) throws IOException {
        // Convert to string to avoid ProviderMismatchException
        String relativePath = source.relativize(file).toString();
        Path targetFile = destination.resolve(relativePath);
        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
    }

    public void copyClasspathFolder(String classpathFolder, Path destination) {
        try {
            URL resource = getClass().getClassLoader().getResource(classpathFolder);
            URI uri = resource.toURI();

            FileSystem jarFileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());;
            Path source = jarFileSystem.getPath(classpathFolder);

            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return createDirectoryAndContinue(source, destination, dir);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    return copyFileAndContinue(source, destination, file);
                }
            });
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error copying folder from " + classpathFolder + " to " + destination, e);
        }
    }

    public void copyClasspathFolderExcludingFiles(String classpathFolder, Path destination, List<String> excludedFiles) {
        try {
            URL resource = getClass().getClassLoader().getResource(classpathFolder);
            if (Objects.isNull(resource)) {
                throw new IllegalArgumentException("Classpath folder not found: " + classpathFolder);
            }
            URI uri = resource.toURI();

            FileSystem jarFileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            Path source = jarFileSystem.getPath(classpathFolder);

            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return createDirectoryAndContinue(source, destination, dir);
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileName = file.getFileName().toString();
                    if (excludedFiles.contains(fileName)) {
                        return FileVisitResult.CONTINUE;
                    }
                    return copyFileAndContinue(source, destination, file);
                }
            });
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error copying folder from " + classpathFolder + " to " + destination + " excluding files", e);
        }
    }
}
