package info.jab.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import info.jab.cli.behaviours.Cursor;
import info.jab.cli.behaviours.DevContainer;
import info.jab.cli.behaviours.EditorConfig;
import info.jab.cli.behaviours.GithubAction;
import info.jab.cli.behaviours.Gitignore;
import info.jab.cli.behaviours.JMC;
import info.jab.cli.behaviours.Maven;
import info.jab.cli.behaviours.QuarkusCli;
import info.jab.cli.behaviours.Sdkman;
import info.jab.cli.behaviours.SpringCli;
import info.jab.cli.behaviours.Visualvm;
import io.vavr.control.Either;
import picocli.CommandLine;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NullAway.Init")
class InitCommandTest {

    InitCommandTest() {
        super();
    }

    @Mock
    private DevContainer mockDevContainer;

    @Mock
    private Maven mockMaven;

    @Mock
    private SpringCli mockSpringCli;

    @Mock
    private QuarkusCli mockQuarkusCli;

    @Mock
    private Cursor mockCursor;

    @Mock
    private GithubAction mockGithubAction;

    @Mock
    private EditorConfig mockEditorConfig;

    @Mock
    private Sdkman mockSdkman;

    @Mock
    private Visualvm mockVisualvm;

    @Mock
    private JMC mockJmc;

    @Mock
    private Gitignore mockGitignore;

    private InitCommand initCommand;
    private ByteArrayOutputStream outputStreamCaptor;
    private CommandLine cmd;
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Reset the output stream for each test
        outputStreamCaptor = new ByteArrayOutputStream();

        // Set up the command with mocked dependencies
        initCommand = new InitCommand(
                mockDevContainer,
                mockMaven,
                mockSpringCli,
                mockQuarkusCli,
                mockCursor,
                mockGithubAction,
                mockEditorConfig,
                mockSdkman,
                mockVisualvm,
                mockJmc,
                mockGitignore
        );

        // Capture console output for assertions
        System.setOut(new PrintStream(outputStreamCaptor));
        cmd = new CommandLine(initCommand);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        // Close the output stream to free memory
        try {
            outputStreamCaptor.close();
        } catch (Exception e) {
            // Ignore close exception
        }
    }

    @Test
    @org.junit.jupiter.api.Disabled("Temporarily disabled to isolate JVM crash issue")
    void shouldExecuteDevContainerFeature() throws Exception {
        // Given
        when(mockDevContainer.execute()).thenReturn(Either.right("DevContainer executed successfully"));
        String[] args = {"--devcontainer"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockDevContainer, times(1)).execute();
        assertThat(exitCode).isEqualTo(1);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("DevContainer executed successfully");
    }

    @Test
    void shouldExecuteMavenFeature() throws Exception {
        // Given
        when(mockMaven.execute()).thenReturn(Either.right("Maven executed successfully"));
        String[] args = {"--maven"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockMaven, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Maven executed successfully");
    }

    @Test
    void shouldExecuteSpringCliFeature() throws Exception {
        // Given
        when(mockSpringCli.execute()).thenReturn(Either.right("SpringCli executed successfully"));
        String[] args = {"--spring-cli"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockSpringCli, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("SpringCli executed successfully");
    }

    @Test
    void shouldExecuteQuarkusCliFeature() throws Exception {
        // Given
        when(mockQuarkusCli.execute()).thenReturn(Either.right("QuarkusCli executed successfully"));
        String[] args = {"--quarkus-cli"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockQuarkusCli, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("QuarkusCli executed successfully");
    }

    @Test
    void shouldExecuteCursorFeature() throws Exception {
        // Given
        when(mockCursor.execute("java")).thenReturn(Either.right("Cursor executed successfully"));
        String[] args = {"--cursor", "java"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCursor, times(1)).execute("java");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Cursor executed successfully");
    }

    @Test
    void shouldExecuteGithubActionFeature() throws Exception {
        // Given
        when(mockGithubAction.execute()).thenReturn(Either.right("GithubAction executed successfully"));
        String[] args = {"--github-action"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockGithubAction, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("GithubAction executed successfully");
    }

    @Test
    void shouldExecuteEditorConfigFeature() throws Exception {
        // Given
        when(mockEditorConfig.execute()).thenReturn(Either.right("EditorConfig executed successfully"));
        String[] args = {"--editorconfig"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockEditorConfig, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("EditorConfig executed successfully");
    }

    @Test
    void shouldExecuteSdkmanFeature() throws Exception {
        // Given
        when(mockSdkman.execute()).thenReturn(Either.right("Sdkman executed successfully"));
        String[] args = {"--sdkman"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockSdkman, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Sdkman executed successfully");
    }

    @Test
    void shouldExecuteVisualmFeature() throws Exception {
        // Given
        when(mockVisualvm.execute()).thenReturn(Either.right("Visualvm executed successfully"));
        String[] args = {"--visualvm"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockVisualvm, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Visualvm executed successfully");
    }

    @Test
    void shouldExecuteJmcFeature() throws Exception {
        // Given
        when(mockJmc.execute()).thenReturn(Either.right("Jmc executed successfully"));
        String[] args = {"--jmc"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockJmc, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Jmc executed successfully");
    }

    @Test
    void shouldExecuteGitignoreFeature() throws Exception {
        // Given
        when(mockGitignore.execute()).thenReturn(Either.right("Gitignore executed successfully"));
        String[] args = {"--gitignore"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockGitignore, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Gitignore executed successfully");
    }

    @Test
    void shouldFailWhenNoOptionProvided() throws Exception {
        // Given
        String[] args = {};

        // When
        int exitCode = cmd.execute(args);

        // Then
        assertThat(exitCode).isNotEqualTo(0); // Should fail due to required argument group
    }

    @Test
    void shouldRejectMultipleOptions() throws Exception {
        // Given
        String[] args = {"--devcontainer", "--maven"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        assertThat(exitCode).isNotEqualTo(0); // Should fail due to mutually exclusive options
        verify(mockDevContainer, never()).execute();
        verify(mockMaven, never()).execute();
    }
}
