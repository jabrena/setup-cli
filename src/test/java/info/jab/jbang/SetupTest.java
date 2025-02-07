package info.jab.jbang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import picocli.CommandLine;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class SetupTest {

    private Setup setup;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        setup = new Setup();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void shouldDisplayHelpMessageWhenNoSubcommandProvided() {
        // Given
        setup.run();

        // Then
        String expectedOutput = "Please specify a subcommand. Use --help to see available options.";
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Disabled
    @Test
    void shouldReturnSuccessExitCode() {
        // Given
        String[] args = new String[]{};

        // When
        int exitCode = new CommandLine(new Setup()).execute(args);

        // Then
        assertEquals(0, exitCode);
    }

    @Test
    void shouldInitializeJavaCursor() {
        // Given
        String[] args = new String[]{"init", "--cursor", "java", "--debug"};

        // When
        int exitCode = new CommandLine(new Setup()).execute(args);

        // Then
        assertEquals(0, exitCode);
        String expectedOutput = "Debug mode: Skipping file copy";
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }
} 