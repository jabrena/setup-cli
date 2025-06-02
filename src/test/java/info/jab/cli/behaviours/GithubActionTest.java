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
        String expectedResourcePath = "github-action-template/";

        // When
        githubAction.execute();

        // Then
        verify(copyFilesMock).copyClasspathFolder(eq(expectedResourcePath), eq(expectedPath));
    }

    @Test
    void testExecuteWithCopyFailure() {
        // Given
        Path expectedPath = Paths.get(System.getProperty("user.dir")).resolve(".github").resolve("workflows");
        String expectedResourcePath = "github-action-template/";
        doThrow(new RuntimeException("Simulated copy error"))
                .when(copyFilesMock).copyClasspathFolder(eq(expectedResourcePath), eq(expectedPath));

        // When / Then
        assertThatThrownBy(() -> githubAction.execute())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated copy error");

        // Verify no success message was printed
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
        .contains("Executing command to add GitHub Actions workflow (.github/workflows/maven.yaml");
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

        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
}
