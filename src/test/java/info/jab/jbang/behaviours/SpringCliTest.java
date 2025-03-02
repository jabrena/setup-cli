package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import info.jab.jbang.util.CommandExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SpringCliTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    @Test
    void shouldPrintCommandsWhenSpringCliIsNotInstalled() throws IOException, InterruptedException {
        // Given
        CommandExecutor mockExecutor = Mockito.mock(CommandExecutor.class);
        when(mockExecutor.checkCommandInstalled("spring")).thenReturn(false);
        
        SpringCli springCli = new SpringCli(mockExecutor);
        
        // When
        springCli.execute();
        
        // Then
        assertThat(outputStreamCaptor.toString()).contains("sdk install springboot");
        assertThat(outputStreamCaptor.toString()).contains("spring init -d=web,actuator,devtools");
        assertThat(outputStreamCaptor.toString()).contains("./mvnw clean verify");
        
        verify(mockExecutor).checkCommandInstalled("spring");
        verify(mockExecutor, never()).executeCommandInstance(anyString());
    }
    
    @Test
    void shouldExecuteCommandWhenSpringCliIsInstalled() throws IOException, InterruptedException {
        // Given
        CommandExecutor mockExecutor = Mockito.mock(CommandExecutor.class);
        when(mockExecutor.checkCommandInstalled("spring")).thenReturn(true);
        when(mockExecutor.executeCommandInstance(anyString())).thenReturn("Spring command executed");
        
        SpringCli springCli = new SpringCli(mockExecutor);
        
        // When
        springCli.execute();
        
        // Then
        assertThat(outputStreamCaptor.toString()).contains("Spring CLI is installed. Executing Spring command...");
        assertThat(outputStreamCaptor.toString()).contains("Spring command executed");
        
        verify(mockExecutor).checkCommandInstalled("spring");
        verify(mockExecutor).executeCommandInstance(contains("spring init"));
    }
    
    @Test
    void shouldHandleExceptionWhenExecutingCommand() throws IOException, InterruptedException {
        // Given
        CommandExecutor mockExecutor = Mockito.mock(CommandExecutor.class);
        when(mockExecutor.checkCommandInstalled("spring")).thenReturn(true);
        when(mockExecutor.executeCommandInstance(anyString())).thenThrow(new IOException("Command failed"));
        
        SpringCli springCli = new SpringCli(mockExecutor);
        
        // When
        springCli.execute();
        
        // Then
        assertThat(errorStreamCaptor.toString()).contains("Error executing Spring command: Command failed");
        
        verify(mockExecutor).checkCommandInstalled("spring");
        verify(mockExecutor).executeCommandInstance(anyString());
    }
    
    @Test
    void shouldCreateSpringCliWithDefaultExecutor() {
        // When
        SpringCli springCli = new SpringCli();
        
        // Then
        assertThat(springCli).isNotNull();
    }
} 