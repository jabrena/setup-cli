package info.jab.cli.io;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CopyFiles {

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
            if (Objects.isNull(resource)) {
                throw new IllegalArgumentException("Classpath folder not found: " + classpathFolder);
            }
            URI uri = resource.toURI();
            Path source;

            if (uri.getScheme().equals("jar")) {
                // Handle JAR file case
                FileSystem jarFileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                source = jarFileSystem.getPath(classpathFolder);
            } else {
                // Handle regular file system case
                source = Paths.get(uri);
            }

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
            Path source;

            if (uri.getScheme().equals("jar")) {
                // Handle JAR file case
                FileSystem jarFileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                source = jarFileSystem.getPath(classpathFolder);
            } else {
                // Handle regular file system case
                source = Paths.get(uri);
            }

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

    public void copyContentToFile(String content, Path destination) {
        try {
            Files.write(destination, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Error copying content to file", e);
        }
    }
}
