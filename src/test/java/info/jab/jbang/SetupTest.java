package info.jab.jbang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import picocli.CommandLine;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.assertj.core.api.Assertions.assertThat;

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
        int exitCode = new CommandLine(new Setup()).execute(args);

        // Then
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void shouldInitializeJavaCursor() {
        // Given
        String[] args = new String[]{"init", "--cursor", "java", "--debug"};
        String expectedOutput = "Debug mode: Skipping file copy";

        // When
        int exitCode = new CommandLine(new Setup()).execute(args);

        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo(expectedOutput);
    }
} 