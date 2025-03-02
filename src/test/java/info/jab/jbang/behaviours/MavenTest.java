package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import info.jab.jbang.util.CommandExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Disabled("WIP")
class MavenTest {

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
    void shouldPrintCommandsWhenMavenIsNotInstalled() throws IOException, InterruptedException {
        // Given
        CommandExecutor mockExecutor = Mockito.mock(CommandExecutor.class);
        when(mockExecutor.checkCommandInstalled("mvn")).thenReturn(false);
        
        Maven maven = new Maven(mockExecutor);
        
        // When
        maven.execute();
        
        // Then
        assertThat(outputStreamCaptor.toString()).contains("sdk install maven");
        assertThat(outputStreamCaptor.toString()).contains("mvn archetype:generate");
        assertThat(outputStreamCaptor.toString()).contains("mvn wrapper:wrapper");
        assertThat(outputStreamCaptor.toString()).contains("./mvnw clean verify");
        
        verify(mockExecutor).checkCommandInstalled("mvn");
        verify(mockExecutor, never()).executeCommandInstance(anyString());
    }
    
    @Test
    void shouldExecuteCommandWhenMavenIsInstalled() throws IOException, InterruptedException {
        // Given
        CommandExecutor mockExecutor = Mockito.mock(CommandExecutor.class);
        when(mockExecutor.checkCommandInstalled("mvn")).thenReturn(true);
        when(mockExecutor.executeCommandInstance(anyString())).thenReturn("Maven command executed");
        
        Maven maven = new Maven(mockExecutor);
        
        // When
        maven.execute();
        
        // Then
        assertThat(outputStreamCaptor.toString()).contains("Maven is installed. Executing Maven command...");
        assertThat(outputStreamCaptor.toString()).contains("Maven command executed");
        
        verify(mockExecutor).checkCommandInstalled("mvn");
        verify(mockExecutor).executeCommandInstance(contains("mvn archetype:generate"));
    }
    
    @Test
    void shouldHandleExceptionWhenExecutingCommand() throws IOException, InterruptedException {
        // Given
        CommandExecutor mockExecutor = Mockito.mock(CommandExecutor.class);
        when(mockExecutor.checkCommandInstalled("mvn")).thenReturn(true);
        when(mockExecutor.executeCommandInstance(anyString())).thenThrow(new IOException("Command failed"));
        
        Maven maven = new Maven(mockExecutor);
        
        // When
        maven.execute();
        
        // Then
        assertThat(errorStreamCaptor.toString()).contains("Error executing Maven command: Command failed");
        
        verify(mockExecutor).checkCommandInstalled("mvn");
        verify(mockExecutor).executeCommandInstance(anyString());
    }
    
    @Test
    void shouldCreateMavenWithDefaultExecutor() {
        // When
        Maven maven = new Maven();
        
        // Then
        assertThat(maven).isNotNull();
    }
} 