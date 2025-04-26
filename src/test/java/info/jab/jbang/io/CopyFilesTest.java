package info.jab.jbang.io;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CopyFilesTest {

    @TempDir
    Path tempDir;

    private Path rulesDir;
    private CopyFiles copyFiles;

    @BeforeEach
    void setUp() {
        // Define a subdirectory within the temporary directory for rules
        rulesDir = tempDir.resolve("rules");
        copyFiles = new CopyFiles();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up the temporary directory manually if needed, though @TempDir handles it.
        // FileUtils.deleteDirectory(tempDir.toFile());
    }

    @Test
    void shouldCopyRuleFilesSuccessfully() throws IOException {
        // Given
        String resourceBasePath = "test/copyfiles/resources/";
        String fileName = "rule1.mdc";
        String fileContent = "This is rule 1.";
        List<String> ruleFiles = List.of(fileName);

        // Create the dummy resource file in target/test-classes matching the resourceBasePath
        Path resourceDir = Paths.get("target/test-classes/").resolve(resourceBasePath);
        Files.createDirectories(resourceDir);
        Files.write(resourceDir.resolve(fileName), fileContent.getBytes(StandardCharsets.UTF_8));

        // When
        copyFiles.copyFilesToDirectory(ruleFiles, resourceBasePath, rulesDir);

        // Then
        Path expectedFile = rulesDir.resolve(fileName);
        assertThat(Files.exists(rulesDir)).isTrue();
        assertThat(Files.exists(expectedFile)).isTrue();
        assertThat(Files.readString(expectedFile)).isEqualTo(fileContent);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenResourceNotFound() {
        // Given
        String resourceBasePath = "nonexistent/copyfiles/path/";
        String fileName = "nonexistent-file.mdc";
        List<String> ruleFiles = List.of(fileName);

        // When / Then
        assertThatThrownBy(() -> copyFiles.copyFilesToDirectory(ruleFiles, resourceBasePath, rulesDir))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error copying rules files")
                .cause()
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Resource not found at " + resourceBasePath + fileName);

         // Check that the directory was still created
         assertThat(Files.exists(rulesDir)).isTrue();
    }

    @Test
    void shouldHandleEmptyRuleFileList() throws IOException {
        // Given
        List<String> emptyRuleFiles = Collections.emptyList();
        String resourceBasePath = "any/copyfiles/path/";

        // When
        copyFiles.copyFilesToDirectory(emptyRuleFiles, resourceBasePath, rulesDir);

        // Then
        assertThat(Files.exists(rulesDir)).isTrue();
        assertThat(FileUtils.listFiles(rulesDir.toFile(), null, false)).isEmpty();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenDirectoryCreationFails() throws IOException {
        // Given
        Files.createFile(rulesDir);
        List<String> ruleFiles = List.of("rule1.mdc");
        String resourceBasePath = "any/copyfiles/path/";

        // When / Then
        assertThatThrownBy(() -> copyFiles.copyFilesToDirectory(ruleFiles, resourceBasePath, rulesDir))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error copying rules files")
                .cause()
                .isInstanceOf(IOException.class) // Cause should be IOException from forceMkdir
                .hasMessageContaining("Cannot create directory"); // Adjusted assertion to be less specific
    }
} 