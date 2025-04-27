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
import java.lang.reflect.Field;
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
        setPrivateField(initCommand, "devcontainerOption", true);

        // When
        String result = initCommand.runInitFeature();

        // Then
        verify(mockDevContainer, times(1)).execute();
        assertThat(result).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteMavenFeature() throws Exception {
        // Given
        setPrivateField(initCommand, "mavenOption", true);

        // When
        String result = initCommand.runInitFeature();

        // Then
        verify(mockMaven, times(1)).execute();
        assertThat(result).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteSpringCliFeature() throws Exception {
        // Given
        setPrivateField(initCommand, "springCliOption", true);

        // When
        String result = initCommand.runInitFeature();

        // Then
        verify(mockSpringCli, times(1)).execute();
        assertThat(result).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteGithubActionFeature() throws Exception {
        // Given
        setPrivateField(initCommand, "githubActionOption", true);

        // When
        String result = initCommand.runInitFeature();

        // Then
        verify(mockGithubAction, times(1)).execute();
        assertThat(result).isEqualTo("Command executed successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java", "java-spring-boot"})
    void shouldExecuteCursorFeatureWithValidOptions(String validOption) throws Exception {
        // Given
        setPrivateField(initCommand, "cursorOption", validOption);

        // When
        String result = initCommand.runInitFeature();

        // Then
        verify(mockCursor, times(1)).execute(validOption);
        assertThat(result).isEqualTo("Command executed successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "not-supported"})
    void shouldNotExecuteCursorFeatureWithInvalidOptions(String invalidOption) throws Exception {
        // Given
        setPrivateField(initCommand, "cursorOption", invalidOption);

        // When
        String result = initCommand.runInitFeature();

        // Then
        verify(mockCursor, never()).execute(any());
        assertThat(result).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteAllFeaturesWhenAllOptionsEnabled() throws Exception {
        // Given
        setPrivateField(initCommand, "devcontainerOption", true);
        setPrivateField(initCommand, "mavenOption", true);
        setPrivateField(initCommand, "springCliOption", true);
        setPrivateField(initCommand, "githubActionOption", true);
        setPrivateField(initCommand, "cursorOption", "java");

        // When
        String result = initCommand.runInitFeature();

        // Then
        verify(mockDevContainer, times(1)).execute();
        verify(mockMaven, times(1)).execute();
        verify(mockSpringCli, times(1)).execute();
        verify(mockGithubAction, times(1)).execute();
        verify(mockCursor, times(1)).execute("java");
        assertThat(result).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldRunCommandAndPrintResult() {
        // Given
        InitCommand spyCommand = spy(initCommand);
        doReturn("Command executed successfully").when(spyCommand).runInitFeature();

        // When
        spyCommand.run();

        // Then
        verify(spyCommand, times(1)).runInitFeature();
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("Command executed successfully");
    }

    @Test
    void shouldExecuteCommandThroughMainMethod() throws Exception {
        // Given
        setPrivateField(initCommand, "cursorOption", "java");
        String[] args = new String[]{"--cursor", "java"};

        // When
        int exitCode = new CommandLine(initCommand).execute(args);

        // Then
        verify(mockCursor, times(1)).execute("java");
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).contains("Command executed successfully");
    }

    /**
     * Helper method to set private fields using reflection.
     * 
     * @param object The object instance to modify
     * @param fieldName The name of the private field
     * @param value The value to set
     * @throws Exception If reflection fails
     */
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
} 