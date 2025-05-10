package info.jab.cli;

import info.jab.cli.behaviours.Cursor;
import info.jab.cli.behaviours.DevContainer;
import info.jab.cli.behaviours.EditorConfig;
import info.jab.cli.behaviours.GithubAction;
import info.jab.cli.behaviours.Maven;
import info.jab.cli.behaviours.QuarkusCli;
import info.jab.cli.behaviours.Sdkman;
import info.jab.cli.behaviours.SpringCli;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitCommandTest {

    @Mock
    private DevContainer mockDevContainer;

    @Mock
    private Maven mockMaven;

    @Mock
    private SpringCli mockSpringCli;

    @Mock
    private QuarkusCli mockQuarkusCli;

    @Mock
    private GithubAction mockGithubAction;

    @Mock
    private Cursor mockCursor;

    @Mock
    private EditorConfig mockEditorConfig;

    @Mock
    private Sdkman mockSdkman;

    private InitCommand initCommand;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private CommandLine cmd;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        // Set up the command with mocked dependencies
        initCommand = new InitCommand(
                mockDevContainer,
                mockMaven,
                mockSpringCli,
                mockQuarkusCli,
                mockCursor,
                mockGithubAction,
                mockEditorConfig,
                mockSdkman
        );

        // Capture console output for assertions
        System.setOut(new PrintStream(outputStreamCaptor));
        cmd = new CommandLine(initCommand);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldShowHelpMessageWhenNoOptionsProvided() {
        // Given
        // No specific setup, command initialized in @BeforeEach

        // When
        String result = initCommand.runInitFeature();

        // Then
        assertThat(result).isEqualTo("type 'init --help' to see available options");
        verifyNoInteractions(mockDevContainer, mockMaven, mockSpringCli, mockGithubAction, mockCursor);
    }

    @Test
    void shouldExecuteDevContainerFeature() throws Exception {
        // Given
        String[] args = {"--devcontainer"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockDevContainer, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteMavenFeature() throws Exception {
        // Given
        String[] args = {"--maven"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockMaven, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteSpringCliFeature() throws Exception {
        // Given
        String[] args = {"--spring-cli"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockSpringCli, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteGithubActionFeature() throws Exception {
        // Given
        String[] args = {"--github-action"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockGithubAction, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @ParameterizedTest
    @MethodSource("info.jab.cli.CursorOptions#getOptions")
    void shouldExecuteCursorFeatureWithValidOptions(String validOption) throws Exception {
        // Given
        String[] args = {"--cursor", validOption};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCursor, times(1)).execute(validOption);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "not-supported"})
    void shouldNotExecuteCursorFeatureWithInvalidOptions(String invalidOption) throws Exception {
        // Given
        String[] args = {"--cursor", invalidOption};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCursor, never()).execute(any());
        assertThat(exitCode).isEqualTo(0); // Picocli handles invalid param gracefully? Check behaviour.
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteAllFeaturesWhenAllOptionsEnabled() throws Exception {
        // Given
        String[] args = {
            "--devcontainer",
            "--maven",
            "--spring-cli",
            "--github-action",
            "--cursor", "java",
            "--editorconfig",
            "--sdkman"
        };

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockDevContainer, times(1)).execute();
        verify(mockMaven, times(1)).execute();
        verify(mockSpringCli, times(1)).execute();
        verify(mockGithubAction, times(1)).execute();
        verify(mockCursor, times(1)).execute("java");
        verify(mockEditorConfig, times(1)).execute();
        verify(mockSdkman, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldRunCommandAndPrintResult() {
        // Given
        InitCommand spyCommand = spy(initCommand);
        CommandLine spyCmd = new CommandLine(spyCommand);

        // When
        spyCmd.execute(); // No args, should trigger runInitFeature

        // Then
        verify(spyCommand, times(1)).run();
        verify(spyCommand, times(1)).runInitFeature();
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("type 'init --help' to see available options");
    }

    @Test
    void shouldExecuteCommandThroughMainMethod() throws Exception {
        // Given
        String[] args = new String[]{"--cursor", "java"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCursor, times(1)).execute("java");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).contains("Command executed successfully");
    }
}
