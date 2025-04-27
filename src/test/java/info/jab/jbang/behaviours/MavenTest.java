package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class MavenTest {

    private Maven maven;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        maven = new Maven();
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @Test
    void testExecute() {
        // Execute
        maven.execute();
        
        // Verify the output contains the required information
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        assertThat(output).contains("mvn archetype:generate");
    }
} 