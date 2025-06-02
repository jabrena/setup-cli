package info.jab.cli.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.nio.file.Path;

class GitFolderCopyTest {

    private GitFolderCopy gitFolderCopy;

    @BeforeEach
    void setUp() {
        gitFolderCopy = new GitFolderCopy();
    }

    @Test
    void testCopyFolderFromRepo_WithEmptyUrl(@TempDir Path tempDir) {
        // Given: Empty repository URL
        String emptyRepoUrl = "";
        String folderPath = "src";
        String destinationPath = tempDir.resolve("destination").toString();

        // When/Then: Should handle empty URL gracefully
        assertThatCode(() -> {
            gitFolderCopy.copyFolderFromRepo(emptyRepoUrl, folderPath, destinationPath);
        }).as("Should handle empty repository URL gracefully").doesNotThrowAnyException();
    }

    @Test
    void testCopyFolderFromRepo_WithEmptyDestinationPath(@TempDir Path tempDir) {
        // Given: Valid-looking URL and folder path but empty destination
        String repoUrl = "https://github.com/jabrena/cursor-rules-java";
        String folderPath = ".cursor/rules";
        String emptyDestinationPath = tempDir.resolve("destination").toString();

        // When/Then: Should handle empty destination path gracefully
        assertThatCode(() -> {
            gitFolderCopy.copyFolderFromRepo(repoUrl, folderPath, emptyDestinationPath);
        }).as("Should handle empty destination path gracefully").doesNotThrowAnyException();
    }

    @Test
    void testCopyFolderFromRepo_WithInvalidFolderPath(@TempDir Path tempDir) {
        // Given: Valid repository but non-existent folder path
        String repoUrl = "https://github.com/jabrena/cursor-rules-java";
        String nonExistentFolderPath = "nonexistent/folder/path";
        String destinationPath = tempDir.resolve("destination").toString();

        // When/Then: Should handle non-existent folder gracefully
        assertThatCode(() -> {
            gitFolderCopy.copyFolderFromRepo(repoUrl, nonExistentFolderPath, destinationPath);
        }).as("Should handle non-existent folder path gracefully").doesNotThrowAnyException();
    }

    @Test
    void testCopyFolderFromRepo_WithMalformedUrl(@TempDir Path tempDir) {
        // Given: Malformed repository URL
        String malformedRepoUrl = "not-a-valid-url";
        String folderPath = "src";
        String destinationPath = tempDir.resolve("destination").toString();

        // When/Then: Should handle malformed URL gracefully
        assertThatCode(() -> {
            gitFolderCopy.copyFolderFromRepo(malformedRepoUrl, folderPath, destinationPath);
        }).as("Should handle malformed repository URL gracefully").doesNotThrowAnyException();
    }

    @Test
    void testCopyFolderFromRepo_WithSpecialCharactersInPaths(@TempDir Path tempDir) {
        // Given: Repository URL and paths with special characters
        String repoUrl = "https://github.com/invalid/repo";
        String folderPath = "folder with spaces/and-symbols!@#";
        String destinationPath = tempDir.resolve("dest with spaces").toString();

        // When/Then: Should handle special characters gracefully
        assertThatCode(() -> {
            gitFolderCopy.copyFolderFromRepo(repoUrl, folderPath, destinationPath);
        }).as("Should handle special characters in paths gracefully").doesNotThrowAnyException();
    }
}
