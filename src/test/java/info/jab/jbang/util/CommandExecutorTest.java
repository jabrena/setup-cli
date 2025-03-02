package info.jab.jbang.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Disabled("WIP")
class CommandExecutorTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    void shouldGetSingletonInstance() {
        // When
        CommandExecutor instance1 = CommandExecutor.getInstance();
        CommandExecutor instance2 = CommandExecutor.getInstance();
        
        // Then
        assertThat(instance1).isSameAs(instance2);
    }
    
    @Test
    void shouldDetectInstalledCommand() {
        // When
        boolean isEchoInstalled = CommandExecutor.isCommandInstalled("echo");
        
        // Then
        assertThat(isEchoInstalled).isTrue();
    }
    
    @Test
    void shouldDetectNotInstalledCommand() {
        // When
        boolean isNonExistentCommandInstalled = CommandExecutor.isCommandInstalled("command_that_does_not_exist_12345");
        
        // Then
        assertThat(isNonExistentCommandInstalled).isFalse();
    }
    
    @Test
    void shouldExecuteCommandSuccessfully() throws IOException, InterruptedException {
        // When
        String output = CommandExecutor.executeCommand("echo 'Hello, World!'");
        
        // Then
        assertThat(output).contains("Hello, World!");
    }
    
    @Test
    void shouldThrowExceptionForFailingCommand() {
        // Then
        assertThatThrownBy(() -> CommandExecutor.executeCommand("exit 1"))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("Command exited with code: 1");
    }
    
    @Test
    void instanceMethodsShouldDelegateToStaticMethods() throws IOException, InterruptedException {
        // Given
        CommandExecutor executor = CommandExecutor.getInstance();
        
        // When & Then
        assertThat(executor.checkCommandInstalled("echo")).isEqualTo(CommandExecutor.isCommandInstalled("echo"));
        assertThat(executor.executeCommandInstance("echo 'Testing'")).isEqualTo(CommandExecutor.executeCommand("echo 'Testing'"));
    }
    
    @Test
    void shouldHandleCommandWithInvalidExitCode(@TempDir Path tempDir) throws IOException {
        // Given
        String scriptName = System.getProperty("os.name").toLowerCase().contains("win") ? "test.bat" : "test.sh";
        Path scriptPath = tempDir.resolve(scriptName);
        
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Files.writeString(scriptPath, "@echo off\necho This command will fail\nexit 1");
        } else {
            Files.writeString(scriptPath, "#!/bin/sh\necho This command will fail\nexit 1");
            
            // Make script executable
            Set<PosixFilePermission> permissions = new HashSet<>();
            permissions.add(PosixFilePermission.OWNER_READ);
            permissions.add(PosixFilePermission.OWNER_WRITE);
            permissions.add(PosixFilePermission.OWNER_EXECUTE);
            try {
                Files.setPosixFilePermissions(scriptPath, permissions);
            } catch (UnsupportedOperationException e) {
                // Skip setting permissions if not supported (e.g., on Windows)
            }
        }
        
        // When & Then
        String script = scriptPath.toAbsolutePath().toString();
        assertThatThrownBy(() -> CommandExecutor.executeCommand(script))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("Command exited with code: 1");
    }
} 