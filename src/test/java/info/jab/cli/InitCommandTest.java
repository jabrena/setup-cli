package info.jab.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import picocli.CommandLine;

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
    }

    @Test
    void shouldShowHelpMessageWhenNoOptionsProvided() {
        // Given
        // No specific setup, command initialized in @BeforeEach

        // When
        String result = initCommand.runInitFeature();

        // Then
        assertThat(result).isEqualTo("type 'init --help' to see available options");
        verifyNoInteractions(mockDevContainer, mockMaven, mockGithubAction, mockCursor);
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

    @Test
    void shouldNotExecuteCursorFeatureWithInvalidOption() throws Exception {
        // Given
        String[] args = {"--cursor", "invalid"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCursor, never()).execute(any());
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).endsWith("type 'init --help' to see available options");
    }

    @Test
    void shouldExecuteAllFeaturesWhenAllOptionsEnabled() throws Exception {
        // Given
        String[] args = {
            "--devcontainer",
            "--maven",
            "--github-action",
            "--cursor", "java",
            "--editorconfig",
            "--sdkman",
            "--gitignore"
        };

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockDevContainer, times(1)).execute();
        verify(mockMaven, times(1)).execute();
        verify(mockGithubAction, times(1)).execute();
        verify(mockCursor, times(1)).execute("java");
        verify(mockEditorConfig, times(1)).execute();
        verify(mockSdkman, times(1)).execute();
        verify(mockGitignore, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteEditorConfigFeature() throws Exception {
        // Given
        String[] args = {"--editorconfig"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockEditorConfig, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteSdkmanFeature() throws Exception {
        // Given
        String[] args = {"--sdkman"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockSdkman, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteMultipleFeaturesSimultaneously() throws Exception {
        // Given
        String[] args = {"--maven", "--devcontainer"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockMaven, times(1)).execute();
        verify(mockDevContainer, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldHandleCombinationOfValidCursorOptionWithOtherFeatures() throws Exception {
        // Given
        String[] args = {"--cursor", "java", "--maven"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCursor, times(1)).execute("java");
        verify(mockMaven, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldRunCommandAndPrintResult() throws Exception {
        // Given
        InitCommand spyCommand = spy(initCommand);
        CommandLine spyCmd = new CommandLine(spyCommand);

        // When
        spyCmd.execute(); // No args, should trigger run method

        // Then
        verify(spyCommand, times(1)).run();
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).endsWith("type 'init --help' to see available options");
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

    @Test
    void shouldCreateInitCommandWithDefaultConstructor() {
        // Given & When
        InitCommand defaultCommand = new InitCommand();

        // Then
        assertThat(defaultCommand).isNotNull();
        // Verify that runInitFeature works with default constructor
        String result = defaultCommand.runInitFeature();
        assertThat(result).isEqualTo("type 'init --help' to see available options");
    }

    @Test
    void shouldExecuteGitignoreFeature() throws Exception {
        // Given
        String[] args = {"--gitignore"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockGitignore, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }
}
