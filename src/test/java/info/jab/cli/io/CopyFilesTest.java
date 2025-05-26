package info.jab.cli.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    @Test
    void shouldCopyContentToFileSuccessfully() throws IOException {
        // Given
        String content = "Hello, World!\nThis is a test file.";
        Path destinationFile = tempDir.resolve("test-output.txt");

        // When
        copyFiles.copyContentToFile(content, destinationFile);

        // Then
        assertThat(Files.exists(destinationFile)).isTrue();
        assertThat(Files.isRegularFile(destinationFile)).isTrue();

        String actualContent = Files.readString(destinationFile, StandardCharsets.UTF_8);
        assertThat(actualContent).isEqualTo(content);
    }

    @Test
    void shouldCopyEmptyContentToFile() throws IOException {
        // Given
        String emptyContent = "";
        Path destinationFile = tempDir.resolve("empty-file.txt");

        // When
        copyFiles.copyContentToFile(emptyContent, destinationFile);

        // Then
        assertThat(Files.exists(destinationFile)).isTrue();
        assertThat(Files.isRegularFile(destinationFile)).isTrue();

        String actualContent = Files.readString(destinationFile, StandardCharsets.UTF_8);
        assertThat(actualContent).isEmpty();
    }

    @Test
    void shouldOverwriteExistingFileWithCopyContentToFile() throws IOException {
        // Given
        String originalContent = "Original content";
        String newContent = "New content that should replace the original";
        Path destinationFile = tempDir.resolve("overwrite-test.txt");

        // Create file with original content
        Files.writeString(destinationFile, originalContent, StandardCharsets.UTF_8);
        assertThat(Files.readString(destinationFile, StandardCharsets.UTF_8)).isEqualTo(originalContent);

        // When
        copyFiles.copyContentToFile(newContent, destinationFile);

        // Then
        assertThat(Files.exists(destinationFile)).isTrue();
        String actualContent = Files.readString(destinationFile, StandardCharsets.UTF_8);
        assertThat(actualContent).isEqualTo(newContent);
        assertThat(actualContent).doesNotContain(originalContent);
    }

    @Test
    void shouldCopyMultilineContentToFile() throws IOException {
        // Given
        String multilineContent = """
            Line 1
            Line 2 with special chars: !@#$%^&*()
            Line 3 with unicode: 你好世界
            Line 4 with tabs	and spaces
            """;
        Path destinationFile = tempDir.resolve("multiline-test.txt");

        // When
        copyFiles.copyContentToFile(multilineContent, destinationFile);

        // Then
        assertThat(Files.exists(destinationFile)).isTrue();
        String actualContent = Files.readString(destinationFile, StandardCharsets.UTF_8);
        assertThat(actualContent).isEqualTo(multilineContent);
        assertThat(actualContent).contains("Line 1");
        assertThat(actualContent).contains("special chars: !@#$%^&*()");
        assertThat(actualContent).contains("unicode: 你好世界");
        assertThat(actualContent).contains("tabs	and spaces");
    }

    @Test
    void shouldThrowExceptionWhenParentDirectoriesDoNotExist() {
        // Given
        String content = "Content for nested file";
        Path nestedFile = tempDir.resolve("nested").resolve("deep").resolve("file.txt");

        // Verify parent directories don't exist initially
        Path parentDir = nestedFile.getParent();
        assertThat(parentDir).isNotNull();
        assertThat(Files.exists(parentDir)).isFalse();

        // When & Then
        assertThatThrownBy(() -> copyFiles.copyContentToFile(content, nestedFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error copying content to file");
    }

    @Test
    void shouldThrowRuntimeExceptionWhenCopyContentToFileFailsWithInvalidPath() {
        // Given
        String content = "Test content";
        // Create an invalid path (trying to write to a directory that exists as a file)
        Path invalidPath = tempDir.resolve("invalid-path.txt").resolve("cannot-create-file-here.txt");

        try {
            // Create a file at the parent path to make it invalid
            Files.writeString(tempDir.resolve("invalid-path.txt"), "blocking file", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Test setup failed", e);
        }

        // When / Then
        assertThatThrownBy(() -> copyFiles.copyContentToFile(content, invalidPath))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error copying content to file");
    }
}
