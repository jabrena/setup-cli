package info.jab.cli.io;

import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;

public class GitFolderCopy {

    private static final Logger logger = LoggerFactory.getLogger(GitFolderCopy.class);

    public void copyFolderFromRepo(String repoUrl, String folderPath, String destinationPath) {
        try {
            // Clone to temporary directory
            Path tempDir = Files.createTempDirectory("git-clone");

            // For public repositories, don't set any credentials provider
            // JGit will handle anonymous access automatically
            Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(tempDir.toFile())
                .call();

            // Copy specific folder
            Path sourcePath = tempDir.resolve(folderPath);
            Path destPath = Paths.get(destinationPath);

            if (Files.exists(sourcePath)) {
                copyDirectory(sourcePath, destPath);
            }

            // Clean up temp directory using NIO
            deleteDirectory(tempDir);

        } catch (Exception e) {
            logger.error("Error copying folder from repo: {}", e.getMessage(), e);
        }
    }

    /**
     * Deletes a directory and all its contents using NIO approach.
     * This method handles both files and directories recursively.
     *
     * @param directory the directory path to delete
     * @throws IOException if an I/O error occurs during deletion
     */
    private void deleteDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Copies a directory from source to destination.
     * @param source the source directory path to copy from
     * @param destination the destination directory path to copy to
     * @throws IOException if an I/O error occurs during the copy operation
     */
    private void copyDirectory(Path source, Path destination) throws IOException {
        try (var stream = Files.walk(source)) {
            stream.filter(sourcePath -> {
                // Always include directories to maintain structure
                if (Files.isDirectory(sourcePath)) {
                    return true;
                }
                // Only include files with .mdc extension
                return sourcePath.toString().toLowerCase(Locale.ENGLISH).endsWith(".mdc");
            }).forEach(sourcePath -> {
                Path destPath = null;
                try {
                    destPath = destination.resolve(source.relativize(sourcePath));
                    if (Files.isDirectory(sourcePath)) {
                        Files.createDirectories(destPath);
                    } else {
                        Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    logger.error("Error copying file from {} to {}: {}", sourcePath, destPath, e.getMessage(), e);
                }
            });
        }
    }
}
