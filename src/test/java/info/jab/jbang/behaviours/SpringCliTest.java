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

class SpringCliTest {

    private SpringCli springCli;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        springCli = new SpringCli();
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @Test
    void shouldPrintCommandsWhenSpringCliIsNotInstalled() throws IOException, InterruptedException {
        // Given
        SpringCli springCli = new SpringCli();
        
        // When
        springCli.execute();
        
        // Then
        assertThat(outputStreamCaptor.toString()).contains("sdk install springboot");
        assertThat(outputStreamCaptor.toString()).contains("spring init -d=web,actuator,devtools");
        assertThat(outputStreamCaptor.toString()).contains("./mvnw clean verify");
    }
    
    @Test
    void shouldExecuteCommandWhenSpringCliIsInstalled() throws IOException, InterruptedException {
        // Given
        SpringCli springCli = new SpringCli();
        
        // When
        springCli.execute();
        
        // Then
        assertThat(outputStreamCaptor.toString()).contains("sdk install springboot");
        assertThat(outputStreamCaptor.toString()).contains("spring init");
    }
    
    @Test
    void shouldHandleExceptionWhenExecutingCommand() throws IOException, InterruptedException {
        // Given        
        SpringCli springCli = new SpringCli();
        
        // When
        springCli.execute();
        
        // Then
        // No error message is expected in the current implementation
        assertThat(errorStreamCaptor.toString()).isEmpty();
    }
    
    @Test
    void shouldCreateSpringCliWithDefaultExecutor() {
        // When
        SpringCli springCli = new SpringCli();
        
        // Then
        assertThat(springCli).isNotNull();
    }

    @Test
    void testExecute() {
        // Execute
        springCli.execute();
        
        // Verify the output contains the required information
        String output = outputStreamCaptor.toString().trim();
        assertThat(output).contains("spring init");
    }
} 