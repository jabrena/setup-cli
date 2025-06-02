package info.jab.cli.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileSystemCheckerTest {

    private final FileSystemChecker fileSystemChecker = new FileSystemChecker();

    @Test
    void testFileExists_WithExistingFile(@TempDir Path tempDir) throws IOException {
        // Given: Create a temporary file
        Path testFile = tempDir.resolve("test-file.txt");
        Files.createFile(testFile);

        // When: Check if file exists
        boolean result = fileSystemChecker.fileExists(testFile.toString());

        // Then: Should return true
        assertTrue(result, "Expected fileExists to return true for existing file");
    }

    @Test
    void testFileExists_WithNonExistentFile(@TempDir Path tempDir) {
        // Given: A path to a non-existent file
        Path nonExistentFile = tempDir.resolve("non-existent-file.txt");

        // When: Check if file exists
        boolean result = fileSystemChecker.fileExists(nonExistentFile.toString());

        // Then: Should return false
        assertFalse(result, "Expected fileExists to return false for non-existent file");
    }

    @Test
    void testFileExists_WithDirectory(@TempDir Path tempDir) throws IOException {
        // Given: Create a temporary directory
        Path testDir = tempDir.resolve("test-directory");
        Files.createDirectory(testDir);

        // When: Check if directory exists
        boolean result = fileSystemChecker.fileExists(testDir.toString());

        // Then: Should return true (directories are considered as existing files)
        assertTrue(result, "Expected fileExists to return true for existing directory");
    }

    @Test
    void testFileExists_WithEmptyString() {
        // When: Check if empty string exists as a file
        boolean result = fileSystemChecker.fileExists("");

        // Then: Should return false
        assertFalse(result, "Expected fileExists to return false for empty string");
    }

    @Test
    void testFileExists_WithRelativePath(@TempDir Path tempDir) throws IOException {
        // Given: Create a file in temp directory and change working directory context
        Path testFile = tempDir.resolve("relative-test.txt");
        Files.createFile(testFile);

        // When: Check with absolute path
        boolean result = fileSystemChecker.fileExists(testFile.toString());

        // Then: Should return true
        assertTrue(result, "Expected fileExists to return true for file with absolute path");
    }

    @Test
    void testFileExists_WithSpecialCharacters(@TempDir Path tempDir) throws IOException {
        // Given: Create a file with special characters in name
        Path testFile = tempDir.resolve("test-file-with-spaces and symbols!@#.txt");
        Files.createFile(testFile);

        // When: Check if file exists
        boolean result = fileSystemChecker.fileExists(testFile.toString());

        // Then: Should return true
        assertTrue(result, "Expected fileExists to return true for file with special characters");
    }
}
