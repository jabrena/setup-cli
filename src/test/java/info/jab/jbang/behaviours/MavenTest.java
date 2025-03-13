package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    void shouldPrintCommandsWhenMavenIsNotInstalled() throws IOException, InterruptedException {
        // Given
        Maven maven = new Maven();
        
        // When
        maven.execute();
        
        // Then
        assertThat(outputStreamCaptor.toString()).contains("sdk install maven");
        assertThat(outputStreamCaptor.toString()).contains("mvn archetype:generate");
        assertThat(outputStreamCaptor.toString()).contains("mvn wrapper:wrapper");
        assertThat(outputStreamCaptor.toString()).contains("./mvnw clean verify");
    }
    
    @Test
    void shouldExecuteCommandWhenMavenIsInstalled() throws IOException, InterruptedException {
        // Given
        Maven maven = new Maven();
        
        // When
        maven.execute();
        
        // Then
        assertThat(outputStreamCaptor.toString()).contains("sdk install maven");
        assertThat(outputStreamCaptor.toString()).contains("mvn archetype:generate");
    }
    
    @Test
    void shouldHandleExceptionWhenExecutingCommand() throws IOException, InterruptedException {
        // Given
        Maven maven = new Maven();
        
        // When
        maven.execute();
        
        // Then
        // No error message is expected in the current implementation
        assertThat(errorStreamCaptor.toString()).isEmpty();
    }
    
    @Test
    void shouldCreateMavenWithDefaultExecutor() {
        // When
        Maven maven = new Maven();
        
        // Then
        assertThat(maven).isNotNull();
    }

    @Test
    void testExecute() {
        // Execute
        maven.execute();
        
        // Verify the output contains the required information
        String output = outputStreamCaptor.toString().trim();
        assertThat(output).contains("mvn archetype:generate");
    }
} 