package info.jab.cli.behaviours;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VisualvmTest {

    private Visualvm visualvm;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        visualvm = new Visualvm();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldExecuteAndPrintVisualvmCommands() {
        // When
        visualvm.execute();

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        var lines = output.lines().toList();

        assertThat(lines).hasSize(4);
        assertThat(lines.get(0)).isEqualTo("sdk install visualvm");
        assertThat(lines.get(1)).isEqualTo("sdk install java 21.0.2-graalce");
        assertThat(lines.get(2)).isEqualTo("sdk default java 21.0.2-graalce");
        assertThat(lines.get(3)).isEqualTo("visualvm");
    }

    @Test
    void shouldImplementBehaviour0Interface() {
        // Then
        assertThat(visualvm).isInstanceOf(Behaviour0.class);
    }

    @Test
    void shouldCreateInstanceSuccessfully() {
        // Given & When
        Visualvm newVisualvm = new Visualvm();

        // Then
        assertThat(newVisualvm).isNotNull();
        assertThat(newVisualvm).isInstanceOf(Behaviour0.class);
    }

    @Test
    void shouldPrintExpectedNumberOfCommands() {
        // When
        visualvm.execute();

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        long lineCount = output.lines().count();
        assertThat(lineCount).isEqualTo(4);
    }

    @Test
    void shouldContainExpectedVisualvmSetupCommands() {
        // When
        visualvm.execute();

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("sdk install visualvm");
        assertThat(output).contains("sdk install java 21.0.2-graalce");
        assertThat(output).contains("sdk default java 21.0.2-graalce");
        assertThat(output).contains("visualvm");
    }

    @Test
    void shouldExecuteMultipleTimesConsistently() {
        // When
        visualvm.execute();
        String firstOutput = outputStreamCaptor.toString(StandardCharsets.UTF_8);

        outputStreamCaptor.reset();
        visualvm.execute();
        String secondOutput = outputStreamCaptor.toString(StandardCharsets.UTF_8);

        // Then
        assertThat(firstOutput).isEqualTo(secondOutput);
    }

    @Test
    void shouldPrintCommandsInCorrectOrder() {
        // When
        visualvm.execute();

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        var lines = output.lines().toList();

        // Verify the order of commands
        assertThat(lines.get(0)).isEqualTo("sdk install visualvm");
        assertThat(lines.get(1)).isEqualTo("sdk install java 21.0.2-graalce");
        assertThat(lines.get(2)).isEqualTo("sdk default java 21.0.2-graalce");
        assertThat(lines.get(3)).isEqualTo("visualvm");
    }

    @Test
    void shouldUseTextBlockForCommands() {
        // When
        visualvm.execute();

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        // Verify that all expected commands are present (text block functionality)
        assertThat(output.lines()).hasSize(4);
        assertThat(output).doesNotContain("null");
        assertThat(output.trim()).isNotEmpty();
    }
}
