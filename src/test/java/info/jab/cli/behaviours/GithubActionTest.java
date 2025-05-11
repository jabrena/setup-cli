package info.jab.cli.behaviours;

import info.jab.cli.io.CopyFiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubActionTest {

    @Mock
    private CopyFiles copyFilesMock;

    private GithubAction githubAction;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        githubAction = new GithubAction(copyFilesMock);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testExecute() {
        // Given
        Path expectedPath = Paths.get(System.getProperty("user.dir")).resolve(".github").resolve("workflows");
        List<String> expectedFiles = List.of("maven.yaml");
        String expectedResourcePath = "github-action/";

        // When
        githubAction.execute();

        // Then
        verify(copyFilesMock).copyFilesToDirectory(eq(expectedFiles), eq(expectedResourcePath), eq(expectedPath));
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .isEqualTo("GitHub Actions workflow added successfully");
    }

    @Test
    void testExecuteWithCopyFailure() {
        // Given
        Path expectedPath = Paths.get(System.getProperty("user.dir")).resolve(".github").resolve("workflows");
        List<String> expectedFiles = List.of("maven.yaml");
        String expectedResourcePath = "github-action/";
        doThrow(new RuntimeException("Simulated copy error"))
                .when(copyFilesMock).copyFilesToDirectory(eq(expectedFiles), eq(expectedResourcePath), eq(expectedPath));

        // When / Then
        assertThatThrownBy(() -> githubAction.execute())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated copy error");

        // Verify no success message was printed
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEmpty();
    }

    // Test the real file copy logic (integration-like test)
    @Test
    void testRealFileCopy(@TempDir Path tempDir) throws IOException {
        // Given
        CopyFiles realCopyFiles = new CopyFiles(); // Use the real implementation
        GithubAction realGithubAction = new GithubAction(realCopyFiles); // Inject the real CopyFiles

        // Save the original user.dir and set it to tempDir
        String originalUserDir = System.getProperty("user.dir");
        System.setProperty("user.dir", tempDir.toString());

        try {
            // When
            realGithubAction.execute();

            // Then
            // Verify directory structure
            Path githubDir = tempDir.resolve(".github");
            Path workflowsDir = githubDir.resolve("workflows");
            assertThat(Files.exists(githubDir)).isTrue();
            assertThat(Files.isDirectory(githubDir)).isTrue();
            assertThat(Files.exists(workflowsDir)).isTrue();
            assertThat(Files.isDirectory(workflowsDir)).isTrue();

            // Verify file existence
            Path mavenYamlFile = workflowsDir.resolve("maven.yaml");
            assertThat(Files.exists(mavenYamlFile)).isTrue();
            assertThat(Files.isRegularFile(mavenYamlFile)).isTrue();

            // Verify file content (basic check)
            String mavenYamlContent = Files.readString(mavenYamlFile);
            assertThat(mavenYamlContent).contains("CI Builds");
            assertThat(mavenYamlContent).contains("Maven");

            // Verify console output
            assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
                .isEqualTo("GitHub Actions workflow added successfully");

        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
}
