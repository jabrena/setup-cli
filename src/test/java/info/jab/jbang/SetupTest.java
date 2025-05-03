package info.jab.jbang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SetupTest {

    @Mock
    private InitCommand mockInitCommand;

    private Setup setupWithMock;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
        setupWithMock = new Setup(mockInitCommand);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testRunWithMockedInitCommand() {
        // Given
        // setupWithMock initialized in @BeforeEach

        // When
        setupWithMock.run();

        // Then
        verify(mockInitCommand, times(1)).runInitFeature();
    }

    @Test
    void testRunCLINoArgs() {
        // Given
        String[] args = {};

        // When
        int exitCode = Setup.runCLI(args);

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        assertThat(output).contains("Please specify a command. Use --help to see available options.");
        assertThat(exitCode).isZero();
    }

    @Test
    void testRunCLIWithInitNoOpts() {
        // Given
        String[] args = {"init"};

        // When
        int exitCode = Setup.runCLI(args);

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        assertThat(output).contains("type 'init --help' to see available options");
        assertThat(exitCode).isZero();
    }

    @Test
    void testRunCLIWithInitValidOpt() {
        // Given
        String[] args = {"init", "-ec"}; // Example: enable editorconfig

        // When
        int exitCode = Setup.runCLI(args);

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        assertThat(output).contains("Command executed successfully");
        assertThat(exitCode).isZero();
    }

    @Test
    void testRunCLIWithInitHelp() {
        // Given
        String[] args = {"init", "--help"};

        // When
        int exitCode = Setup.runCLI(args);

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        assertThat(output).contains("Usage: setup init");
        assertThat(exitCode).isZero();
    }
}
