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
import info.jab.cli.io.CommandExecutor;
import picocli.CommandLine;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.contains;

@ExtendWith(MockitoExtension.class)
class InitCommandTest {

    @Mock
    private DevContainer mockDevContainer;

    @Mock
    private CommandExecutor mockCommandExecutor;

    @Mock
    private Maven.FileSystemChecker mockFileSystemChecker;

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

    @Mock
    private Visualvm mockVisualvm;

    @Mock
    private JMC mockJMC;

    @Mock
    private Gitignore mockGitignore;

    private InitCommand initCommand;
    private Maven maven; // Real Maven instance with mocked CommandExecutor
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private CommandLine cmd;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        // Setup default successful Maven version check for all tests using lenient stubbing
        CommandExecutor.CommandResult versionCheckResult = CommandExecutor.CommandResult.success("Apache Maven 3.9.0");
        lenient().when(mockCommandExecutor.execute(eq("mvn --version"))).thenReturn(versionCheckResult);

        // Setup default file system check - no pom.xml exists by default
        lenient().when(mockFileSystemChecker.fileExists(eq("pom.xml"))).thenReturn(false);

        // Setup default successful responses for all Maven commands using lenient stubbing
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Command executed successfully");
        lenient().when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);
        lenient().when(mockCommandExecutor.execute(contains("mv maven-demo"))).thenReturn(successResult);
        lenient().when(mockCommandExecutor.execute(eq("rmdir maven-demo"))).thenReturn(successResult);
        lenient().when(mockCommandExecutor.execute(eq("mvn wrapper:wrapper"))).thenReturn(successResult);
        lenient().when(mockCommandExecutor.execute(eq("./mvnw clean verify"))).thenReturn(successResult);

        // Create real Maven instance with mocked CommandExecutor and FileSystemChecker
        maven = new Maven(mockCommandExecutor, mockFileSystemChecker);

        // Set up the command with mocked dependencies (except Maven which is real but uses mocked CommandExecutor)
        initCommand = new InitCommand(
                mockDevContainer,
                maven,              // Real Maven with mocked CommandExecutor
                mockSpringCli,
                mockQuarkusCli,
                mockCursor,
                mockGithubAction,
                mockEditorConfig,
                mockSdkman,
                mockVisualvm,
                mockJMC,
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
        verifyNoInteractions(mockDevContainer, mockCommandExecutor, mockSpringCli, mockGithubAction, mockCursor);
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
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Maven project created successfully");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
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
    void shouldExecuteQuarkusCliFeature() throws Exception {
        // Given
        String[] args = {"--quarkus-cli"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockQuarkusCli, times(1)).execute();
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

    @Test
    void shouldExecuteVisualvmFeature() throws Exception {
        // Given
        String[] args = {"--visualvm"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockVisualvm, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteJmcFeature() throws Exception {
        // Given
        String[] args = {"--jmc"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockJMC, times(1)).execute();
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
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("type 'init --help' to see available options");
    }

    @Test
    void shouldExecuteAllFeaturesWhenAllOptionsEnabled() throws Exception {
        // Given
        String[] args = {
            "--devcontainer",
            "--maven",
            "--spring-cli",
            "--quarkus-cli",
            "--github-action",
            "--cursor", "java",
            "--editorconfig",
            "--sdkman",
            "--visualvm",
            "--jmc",
            "--gitignore"
        };
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Maven command executed successfully");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockDevContainer, times(1)).execute();
        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
        verify(mockSpringCli, times(1)).execute();
        verify(mockQuarkusCli, times(1)).execute();
        verify(mockGithubAction, times(1)).execute();
        verify(mockCursor, times(1)).execute("java");
        verify(mockEditorConfig, times(1)).execute();
        verify(mockSdkman, times(1)).execute();
        verify(mockVisualvm, times(1)).execute();
        verify(mockJMC, times(1)).execute();
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
        String[] args = {"--maven", "--spring-cli", "--devcontainer"};
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Maven command executed");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
        verify(mockSpringCli, times(1)).execute();
        verify(mockDevContainer, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldHandleCombinationOfValidCursorOptionWithOtherFeatures() throws Exception {
        // Given
        String[] args = {"--cursor", "java", "--maven", "--spring-cli"};
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Maven command executed");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCursor, times(1)).execute("java");
        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
        verify(mockSpringCli, times(1)).execute();
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
}
