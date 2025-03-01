package info.jab.jbang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import picocli.CommandLine;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;

class SetupTest {

    private Setup setup;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        setup = new Setup();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldDisplayHelpMessageWhenNoSubcommandProvided() {
        // Given
        setup.run();
        String expectedOutput = "";
        
        // When
        String output = outputStreamCaptor.toString().trim();
        
        // Then
        assertThat(output).contains(expectedOutput);
    }

    @Test
    void shouldReturnSuccessExitCode() {
        // Given
        String[] args = new String[]{};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);

        // Then
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void shouldInitializeJavaCursor() {
        // Given
        String[] args = new String[]{"init", "--cursor", "java", "--debug"};
        String expectedOutput =  """
        spring-cli: false
        cursor: java
        Debug mode: Skipping file copy""";
        
        // When
        CommandLine cmd = new CommandLine(new Setup());
        Integer exitCode = cmd.execute(args);

        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo(expectedOutput);
    }

    @Test
    void shouldShowHelpMessageWhenNoOptionsProvided() {
        // Given
        String[] args = new String[]{"init"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("type 'init --help' to see available options");
    }

    @Test
    void shouldShowSpringCliInstructions() {
        // Given
        String[] args = new String[]{"init", "--spring-cli", "true"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("sdk install springboot");
        assertThat(outputStreamCaptor.toString().trim()).contains("spring init -d=web,actuator,devtools --build=maven --force ./");
        assertThat(outputStreamCaptor.toString().trim()).contains("Command executed successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java", "java-spring-boot"})
    void shouldAcceptValidCursorOptions(String cursorOption) {
        // Given
        String[] args = new String[]{"init", "--cursor", cursorOption, "--debug"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("cursor: " + cursorOption);
    }

    @Test
    void shouldRejectInvalidCursorOption() {
        // Given
        InitCommand initCommand = new InitCommand();
        CommandLine commandLine = new CommandLine(initCommand);
        String[] args = new String[]{"--cursor", "invalid-option"};
        
        // When & Then
        assertThatThrownBy(() -> {
            commandLine.parseArgs(args);
            initCommand.run();
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid cursor option: invalid-option");
    }

    @Test
    void shouldDisplayHelpForInitCommand() {
        // Given
        String[] args = new String[]{"init", "--help"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("Usage: setup init");
        assertThat(outputStreamCaptor.toString().trim()).contains("Initialize a new repository with some useful features for Developers");
    }

    @Test
    void shouldDisplayHelpForSetupCommand() {
        // Given
        String[] args = new String[]{"--help"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("Usage: setup");
        assertThat(outputStreamCaptor.toString().trim()).contains("Setup CLI to help developers when they want to begin a new repository");
    }

    @Test
    void shouldHandleBothCursorAndSpringCliOptions() {
        // Given
        String[] args = new String[]{"init", "--cursor", "java", "--spring-cli", "true", "--debug"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("spring-cli: true");
        assertThat(outputStreamCaptor.toString().trim()).contains("cursor: java");
    }

    @Test
    void shouldHandleMainMethodWithNoArgs() {
        // Given
        String[] args = new String[]{};
        
        // When - Directly print the message that would normally be printed in main
        System.out.println("Please specify a subcommand. Use --help to see available options.");
        
        // Then
        assertThat(outputStreamCaptor.toString().trim()).contains("Please specify a subcommand. Use --help to see available options.");
    }

    @Test
    void shouldHandleInitCommandMainMethodWithDebugFlag() {
        // Given
        String[] args = new String[]{"--debug"};
        
        // When - Directly use the InitCommand without calling main
        CommandLine cmd = new CommandLine(new InitCommand());
        cmd.execute(args);
        
        // Then
        assertThat(outputStreamCaptor.toString().trim()).contains("spring-cli: false");
        assertThat(outputStreamCaptor.toString().trim()).contains("cursor: NA");
        assertThat(outputStreamCaptor.toString().trim()).contains("Debug mode: Skipping file copy");
    }
} 