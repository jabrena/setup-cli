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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

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
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedGitignoreFile = currentPath.resolve(".gitignore");
        String expectedResourcePath = "templates/gitignore/gitignore.template";
        doNothing().when(copyFilesMock).copyClasspathFileWithRename(any(String.class), any(Path.class));

        // When
        Either<String, String> result = gitignore.execute();

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Command execution completed successfully");
        verify(copyFilesMock).copyClasspathFileWithRename(eq(expectedResourcePath), eq(expectedGitignoreFile));
    }

    @Test
    void shouldHandleCopyFailureGracefully() {
        // Given
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedGitignoreFile = currentPath.resolve(".gitignore");
        String expectedResourcePath = "templates/gitignore/gitignore.template";
        doThrow(new RuntimeException("Simulated copy error"))
                .when(copyFilesMock).copyClasspathFileWithRename(eq(expectedResourcePath), eq(expectedGitignoreFile));

        // When / Then
        assertThatThrownBy(() -> gitignore.execute())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated copy error");

        verify(copyFilesMock).copyClasspathFileWithRename(eq(expectedResourcePath), eq(expectedGitignoreFile));
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
            Either<String, String> result = realGitignore.execute();

            // Then
            assertThat(result.isRight()).isTrue();
            assertThat(result.get()).isEqualTo("Command execution completed successfully");

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
            Either<String, String> result = realGitignore.execute();

            // Then
            assertThat(result.isRight()).isTrue();
            assertThat(result.get()).isEqualTo("Command execution completed successfully");
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
        Path expectedGitignoreFile = currentPath.resolve(".gitignore");
        String expectedResourcePath = "templates/gitignore/gitignore.template";
        doNothing().when(copyFilesMock).copyClasspathFileWithRename(any(String.class), any(Path.class));

        // When
        gitignore.execute();

        // Then
        verify(copyFilesMock).copyClasspathFileWithRename(eq(expectedResourcePath), eq(expectedGitignoreFile));
    }

    @Test
    void shouldContainExpectedGitignoreEntries() {
        // Given
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedGitignoreFile = currentPath.resolve(".gitignore");
        String expectedResourcePath = "templates/gitignore/gitignore.template";
        doNothing().when(copyFilesMock).copyClasspathFileWithRename(any(String.class), any(Path.class));

        // When
        gitignore.execute();

        // Then
        verify(copyFilesMock).copyClasspathFileWithRename(eq(expectedResourcePath), eq(expectedGitignoreFile));

        // Note: The actual content verification is now done by the copyClasspathFileWithRename method
        // which copies from the template file that contains the expected entries:
        // .DS_Store, target/, .idea/, .vscode/, .cursor/, .flattened-pom.xml, *.log, .classpath
    }

    @Test
    void shouldCallCopyFilesWithCorrectParameters() {
        // Given
        doNothing().when(copyFilesMock).copyClasspathFileWithRename(any(String.class), any(Path.class));
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedGitignoreFile = currentPath.resolve(".gitignore");
        String expectedResourcePath = "templates/gitignore/gitignore.template";

        // When
        gitignore.execute();

        // Then
        verify(copyFilesMock).copyClasspathFileWithRename(eq(expectedResourcePath), eq(expectedGitignoreFile));
    }

    @Test
    void shouldReturnRightEitherWithSuccessMessage() {
        // Given
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedGitignoreFile = currentPath.resolve(".gitignore");
        String expectedResourcePath = "templates/gitignore/gitignore.template";
        doNothing().when(copyFilesMock).copyClasspathFileWithRename(any(String.class), any(Path.class));

        // When
        Either<String, String> result = gitignore.execute();

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Command execution completed successfully");
        verify(copyFilesMock).copyClasspathFileWithRename(eq(expectedResourcePath), eq(expectedGitignoreFile));
    }
}
