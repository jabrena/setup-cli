package info.jab.cli.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CopyFilesTest {

    @TempDir
    Path tempDir;

    private Path destinationDir;
    private CopyFiles copyFiles;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        // Define a subdirectory within the temporary directory for destination
        destinationDir = tempDir.resolve("destination");
        copyFiles = new CopyFiles();
    }

    @Test
    void shouldCopyClasspathFolderSuccessfully() throws IOException {
        // Given
        String classpathFolder = "test-folder";

        // When
        copyFiles.copyClasspathFolder(classpathFolder, destinationDir);

        // Then
        assertThat(Files.exists(destinationDir)).isTrue();
        assertThat(Files.isDirectory(destinationDir)).isTrue();

        // Verify files were copied
        Path copiedFile1 = destinationDir.resolve("file1.txt");
        Path copiedFile2 = destinationDir.resolve("file2.txt");

        assertThat(Files.exists(copiedFile1)).isTrue();
        assertThat(Files.exists(copiedFile2)).isTrue();

        // Verify content
        String file1Content = Files.readString(copiedFile1);
        String file2Content = Files.readString(copiedFile2);

        assertThat(file1Content).contains("This is test file 1 content.");
        assertThat(file2Content).contains("This is test file 2 content.");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenClasspathFolderNotFoundInCopyClasspathFolder() {
        // Given
        String nonexistentClasspathFolder = "nonexistent/folder/";

        // When / Then
        assertThatThrownBy(() -> copyFiles.copyClasspathFolder(nonexistentClasspathFolder, destinationDir))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Classpath folder not found: " + nonexistentClasspathFolder);
    }

    @Test
    void shouldCopyClasspathFolderExcludingFilesSuccessfully() throws IOException {
        // Given
        String classpathFolder = "test-folder";
        List<String> excludedFiles = List.of("file2.txt");

        // When
        copyFiles.copyClasspathFolderExcludingFiles(classpathFolder, destinationDir, excludedFiles);

        // Then
        assertThat(Files.exists(destinationDir)).isTrue();
        assertThat(Files.isDirectory(destinationDir)).isTrue();

        // Verify only file1.txt was copied, file2.txt was excluded
        Path copiedFile1 = destinationDir.resolve("file1.txt");
        Path excludedFile2 = destinationDir.resolve("file2.txt");

        assertThat(Files.exists(copiedFile1)).isTrue();
        assertThat(Files.exists(excludedFile2)).isFalse();

        // Verify content of copied file
        String file1Content = Files.readString(copiedFile1);
        assertThat(file1Content).contains("This is test file 1 content.");
    }

    @Test
    void shouldCopyClasspathFolderExcludingMultipleFiles() throws IOException {
        // Given
        String classpathFolder = "test-folder";
        List<String> excludedFiles = List.of("file1.txt", "file2.txt");

        // When
        copyFiles.copyClasspathFolderExcludingFiles(classpathFolder, destinationDir, excludedFiles);

        // Then
        assertThat(Files.exists(destinationDir)).isTrue();
        assertThat(Files.isDirectory(destinationDir)).isTrue();

        // Verify no files were copied since all were excluded
        Path excludedFile1 = destinationDir.resolve("file1.txt");
        Path excludedFile2 = destinationDir.resolve("file2.txt");

        assertThat(Files.exists(excludedFile1)).isFalse();
        assertThat(Files.exists(excludedFile2)).isFalse();
    }

    @Test
    void shouldCopyClasspathFolderExcludingFilesWithEmptyExclusionList() throws IOException {
        // Given
        String classpathFolder = "test-folder";
        List<String> excludedFiles = List.of(); // Empty list

        // When
        copyFiles.copyClasspathFolderExcludingFiles(classpathFolder, destinationDir, excludedFiles);

        // Then
        assertThat(Files.exists(destinationDir)).isTrue();
        assertThat(Files.isDirectory(destinationDir)).isTrue();

        // Verify all files were copied since nothing was excluded
        Path copiedFile1 = destinationDir.resolve("file1.txt");
        Path copiedFile2 = destinationDir.resolve("file2.txt");

        assertThat(Files.exists(copiedFile1)).isTrue();
        assertThat(Files.exists(copiedFile2)).isTrue();
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenClasspathFolderNotFoundInCopyClasspathFolderExcludingFiles() {
        // Given
        String nonexistentClasspathFolder = "nonexistent/folder/";
        List<String> excludedFiles = List.of("somefile.txt");

        // When / Then
        assertThatThrownBy(() -> copyFiles.copyClasspathFolderExcludingFiles(nonexistentClasspathFolder, destinationDir, excludedFiles))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Classpath folder not found: " + nonexistentClasspathFolder);
    }

    @Test
    void shouldHandleNonExistentExcludedFiles() throws IOException {
        // Given
        String classpathFolder = "test-folder";
        List<String> excludedFiles = List.of("nonexistent.txt", "another-nonexistent.txt");

        // When
        copyFiles.copyClasspathFolderExcludingFiles(classpathFolder, destinationDir, excludedFiles);

        // Then
        assertThat(Files.exists(destinationDir)).isTrue();
        assertThat(Files.isDirectory(destinationDir)).isTrue();

        // Verify all existing files were copied since excluded files don't exist
        Path copiedFile1 = destinationDir.resolve("file1.txt");
        Path copiedFile2 = destinationDir.resolve("file2.txt");

        assertThat(Files.exists(copiedFile1)).isTrue();
        assertThat(Files.exists(copiedFile2)).isTrue();
    }

    @Test
    void shouldCopyClasspathFolderToExistingDirectory() throws IOException {
        // Given
        String classpathFolder = "test-folder";
        Files.createDirectories(destinationDir); // Pre-create destination directory

        // When
        copyFiles.copyClasspathFolder(classpathFolder, destinationDir);

        // Then
        assertThat(Files.exists(destinationDir)).isTrue();
        assertThat(Files.isDirectory(destinationDir)).isTrue();

        // Verify files were copied
        Path copiedFile1 = destinationDir.resolve("file1.txt");
        Path copiedFile2 = destinationDir.resolve("file2.txt");

        assertThat(Files.exists(copiedFile1)).isTrue();
        assertThat(Files.exists(copiedFile2)).isTrue();
    }
}
