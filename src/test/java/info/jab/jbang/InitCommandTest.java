package info.jab.jbang;

import info.jab.jbang.behaviours.Cursor;
import info.jab.jbang.behaviours.DevContainer;
import info.jab.jbang.behaviours.EditorConfig;
import info.jab.jbang.behaviours.GithubAction;
import info.jab.jbang.behaviours.Maven;
import info.jab.jbang.behaviours.QuarkusCli;
import info.jab.jbang.behaviours.Sdkman;
import info.jab.jbang.behaviours.SpringCli;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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
        // When
        String result = initCommand.runInitFeature();

        // Then
        assertThat(result).isEqualTo("type 'init --help' to see available options");
        verifyNoInteractions(mockDevContainer, mockMaven, mockSpringCli, mockGithubAction, mockCursor);
    }

    @Test
    void shouldExecuteDevContainerFeature() throws Exception {
        // Given
        //setPrivateField(initCommand, "devcontainerOption", true);

        // When
        int exitCode = cmd.execute("--devcontainer");

        // Then
        verify(mockDevContainer, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteMavenFeature() throws Exception {
        // Given
        //setPrivateField(initCommand, "mavenOption", true);

        // When
        int exitCode = cmd.execute("--maven");

        // Then
        verify(mockMaven, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteSpringCliFeature() throws Exception {
        // Given
        //setPrivateField(initCommand, "springCliOption", true);

        // When
        int exitCode = cmd.execute("--spring-cli");

        // Then
        verify(mockSpringCli, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteGithubActionFeature() throws Exception {
        // Given
        //setPrivateField(initCommand, "githubActionOption", true);

        // When
        int exitCode = cmd.execute("--github-action");

        // Then
        verify(mockGithubAction, times(1)).execute();
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java", "java-spring-boot"})
    void shouldExecuteCursorFeatureWithValidOptions(String validOption) throws Exception {
        // Given
        //setPrivateField(initCommand, "cursorOption", validOption);

        // When
        int exitCode = cmd.execute("--cursor", validOption);

        // Then
        verify(mockCursor, times(1)).execute(validOption);
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "not-supported"})
    void shouldNotExecuteCursorFeatureWithInvalidOptions(String invalidOption) throws Exception {
        // Given
        //setPrivateField(initCommand, "cursorOption", invalidOption);

        // When
        int exitCode = cmd.execute("--cursor", invalidOption);

        // Then
        verify(mockCursor, never()).execute(any());
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteAllFeaturesWhenAllOptionsEnabled() throws Exception {
        // Given
        //setPrivateField(initCommand, "devcontainerOption", true);
        //setPrivateField(initCommand, "mavenOption", true);
        //setPrivateField(initCommand, "springCliOption", true);
        //setPrivateField(initCommand, "githubActionOption", true);
        //setPrivateField(initCommand, "cursorOption", "java");
        String[] args = {
            "--devcontainer", 
            "--maven", 
            "--spring-cli", 
            "--github-action", 
            "--cursor", "java"
        };

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockDevContainer, times(1)).execute();
        verify(mockMaven, times(1)).execute();
        verify(mockSpringCli, times(1)).execute();
        verify(mockGithubAction, times(1)).execute();
        verify(mockCursor, times(1)).execute("java");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldRunCommandAndPrintResult() {
        // Given
        InitCommand spyCommand = spy(initCommand);
        CommandLine spyCmd = new CommandLine(spyCommand);

        // When
        spyCmd.execute();

        // Then
        verify(spyCommand, times(1)).run();
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("type 'init --help' to see available options");
    }

    @Test
    void shouldExecuteCommandThroughMainMethod() throws Exception {
        // Given
        //setPrivateField(initCommand, "cursorOption", "java");
        String[] args = new String[]{"--cursor", "java"};

        // When
        int exitCode = cmd.execute(args);

        // Then
        verify(mockCursor, times(1)).execute("java");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).contains("Command executed successfully");
    }
} 