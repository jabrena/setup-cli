package info.jab.cli.behaviours;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JMCTest {

    private JMC jmc;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        jmc = new JMC();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldImplementBehaviour0Interface() {
        // Then
        assertThat(jmc).isInstanceOf(Behaviour0.class);
    }

    @Test
    void shouldCreateInstanceSuccessfully() {
        // Given & When
        JMC newJmc = new JMC();

        // Then
        assertThat(newJmc).isNotNull();
        assertThat(newJmc).isInstanceOf(Behaviour0.class);
    }

    @Test
    void shouldPrintExpectedNumberOfCommands() {
        // When
        jmc.execute();

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        long lineCount = output.lines().count();
        assertThat(lineCount).isEqualTo(6);
    }

    @Test
    void shouldContainExpectedJMCSetupCommands() {
        // When
        jmc.execute();

        // Then
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8);
        assertThat(output).contains("sdk install jmc");
        assertThat(output).contains("jmc");
    }

    @Test
    void shouldExecuteMultipleTimesConsistently() {
        // When
        jmc.execute();
        String firstOutput = outputStreamCaptor.toString(StandardCharsets.UTF_8);

        outputStreamCaptor.reset();
        jmc.execute();
        String secondOutput = outputStreamCaptor.toString(StandardCharsets.UTF_8);

        // Then
        assertThat(firstOutput).isEqualTo(secondOutput);
    }
}
