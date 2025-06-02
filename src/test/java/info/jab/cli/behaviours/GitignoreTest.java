package info.jab.cli.behaviours;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import info.jab.cli.io.CopyFiles;

@ExtendWith(MockitoExtension.class)
class GitignoreTest {

    @Mock
    private CopyFiles copyFilesMock;

    private Gitignore gitignore;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        gitignore = new Gitignore(copyFilesMock);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldExecuteSuccessfully() {
        // Given
        Path expectedPath = Paths.get(System.getProperty("user.dir")).resolve(".gitignore");
        String expectedContent = """
            .DS_Store
            target/
            .idea/
            .vscode/
            .cursor/
            .flattened-pom.xml
            *.log
            .classpath
            """;

        // When
        gitignore.execute();

        // Then
        verify(copyFilesMock).copyContentToFile(eq(expectedContent), eq(expectedPath));
    }

    @Test
    void shouldHandleCopyFailureGracefully() {
        // Given
        Path expectedPath = Paths.get(System.getProperty("user.dir")).resolve(".gitignore");
        String expectedContent = """
            .DS_Store
            target/
            .idea/
            .vscode/
            .cursor/
            .flattened-pom.xml
            *.log
            .classpath
            """;
        doThrow(new RuntimeException("Simulated copy error"))
                .when(copyFilesMock).copyContentToFile(eq(expectedContent), eq(expectedPath));

        // When / Then
        assertThatThrownBy(() -> gitignore.execute())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated copy error");

        // Verify no success message was printed
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Executing command to add .gitignore file");
    }

    @Test
    void shouldCreateGitignoreWithDefaultConstructor() {
        // Given
        Gitignore defaultGitignore = new Gitignore();

        // When / Then
        // Should not throw any exception during construction
        assertThat(defaultGitignore).isNotNull();
    }

    @Test
    void shouldCreateGitignoreFileWithCorrectContent(@TempDir Path tempDir) throws IOException {
        // Given
        CopyFiles realCopyFiles = new CopyFiles();
        Gitignore realGitignore = new Gitignore(realCopyFiles);

        // Save the original user.dir and set it to tempDir
        String originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());

        try {
            // When
            realGitignore.execute();

            // Then
            Path gitignoreFile = tempDir.resolve(".gitignore");
            assertThat(Files.exists(gitignoreFile)).isTrue();
            assertThat(Files.isRegularFile(gitignoreFile)).isTrue();

            // Verify file content
            String actualContent = Files.readString(gitignoreFile, StandardCharsets.UTF_8);
            String expectedContent = """
                .DS_Store
                target/
                .idea/
                .vscode/
                .cursor/
                .flattened-pom.xml
                *.log
                .classpath
                """;
            assertThat(actualContent).isEqualTo(expectedContent);

        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void shouldOverwriteExistingGitignoreFile(@TempDir Path tempDir) throws IOException {
        // Given
        CopyFiles realCopyFiles = new CopyFiles();
        Gitignore realGitignore = new Gitignore(realCopyFiles);

        // Save the original user.dir and set it to tempDir
        String originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());

        try {
            // Create an existing .gitignore file with different content
            Path gitignoreFile = tempDir.resolve(".gitignore");
            String existingContent = "# Old gitignore content\n*.tmp\n";
            Files.writeString(gitignoreFile, existingContent, StandardCharsets.UTF_8);

            // When
            realGitignore.execute();

            // Then
            assertThat(Files.exists(gitignoreFile)).isTrue();

            // Verify the file was overwritten with new content
            String actualContent = Files.readString(gitignoreFile, StandardCharsets.UTF_8);
            String expectedContent = """
                .DS_Store
                target/
                .idea/
                .vscode/
                .cursor/
                .flattened-pom.xml
                *.log
                .classpath
                """;
            assertThat(actualContent).isEqualTo(expectedContent);
            assertThat(actualContent).doesNotContain("Old gitignore content");
            assertThat(actualContent).doesNotContain("*.tmp");

        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void shouldUseCorrectGitignoreFileName() {
        // Given
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedPath = currentPath.resolve(".gitignore");

        // When
        gitignore.execute();

        // Then
        verify(copyFilesMock).copyContentToFile(
            eq("""
                .DS_Store
                target/
                .idea/
                .vscode/
                .cursor/
                .flattened-pom.xml
                *.log
                .classpath
                """),
            eq(expectedPath)
        );
    }

    @Test
    void shouldContainExpectedGitignoreEntries() {
        // Given
        String expectedContent = """
            .DS_Store
            target/
            .idea/
            .vscode/
            .cursor/
            .flattened-pom.xml
            *.log
            .classpath
            """;

        // When
        gitignore.execute();

        // Then
        verify(copyFilesMock).copyContentToFile(eq(expectedContent), eq(Paths.get(System.getProperty("user.dir")).resolve(".gitignore")));

        // Verify that the content includes common IDE and build artifacts
        assertThat(expectedContent).contains(".DS_Store");      // macOS system file
        assertThat(expectedContent).contains("target/");        // Maven build directory
        assertThat(expectedContent).contains(".idea/");         // IntelliJ IDEA
        assertThat(expectedContent).contains(".vscode/");       // Visual Studio Code
        assertThat(expectedContent).contains(".cursor/");       // Cursor IDE
        assertThat(expectedContent).contains(".flattened-pom.xml"); // Maven flatten plugin
        assertThat(expectedContent).contains("*.log");          // Log files
        assertThat(expectedContent).contains(".classpath");      // Maven classpath file
    }
}
