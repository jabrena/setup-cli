package info.jab.cli.io;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CopyFilesTest {

    @TempDir
    Path tempDir;

    private Path rulesDir;
    private CopyFiles copyFiles;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
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

    /*
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
    */

    @Test
    void shouldThrowRuntimeExceptionWhenClasspathFolderNotFoundInCopyClasspathFolder() {
        // Given
        String nonexistentClasspathFolder = "nonexistent/folder/";

        // When / Then
        assertThatThrownBy(() -> copyFiles.copyClasspathFolder(nonexistentClasspathFolder, rulesDir))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Classpath folder not found: " + nonexistentClasspathFolder);
    }

    /*
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
        Files.createFile(rulesDir); // Target path is a file, not a directory
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
                 */
}
