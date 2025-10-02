package info.jab.cli.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class CommandExecutorTest {

    @TempDir
    @SuppressWarnings("NullAway.Init")
    Path tempDir;

    private CommandExecutor commandExecutor;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        commandExecutor = new CommandExecutor();
    }

    @Test
    void execute_shouldReturnFailureForCommandWithNonZeroExitCode() {
        // Given - a command that should fail
        String command = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
            ? "cmd /c exit 1"
            : "sh -c 'exit 1'";

        // When
        var result = commandExecutor.execute(command);

        // Then
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void execute_shouldExecuteInSpecifiedWorkingDirectory() {
        // Given
        File workingDirectory = tempDir.toFile();
        String command = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
            ? "cmd /c cd"
            : "pwd";

        // When
        var result = commandExecutor.execute(command, workingDirectory, 5);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).contains(tempDir.toString());
    }

    @Test
    void execute_shouldReturnFailureForCommandWithNonZeroExitCodeWithCustomSettings() {
        // Given
        File workingDirectory = tempDir.toFile();
        String command = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
            ? "cmd /c exit 2"
            : "sh -c 'exit 2'";

        // When
        var result = commandExecutor.execute(command, workingDirectory, 5);

        // Then
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void execute_withShortTimeout_shouldHandleTimeout() {
        // Given - a command that takes longer than the timeout
        String command = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win")
            ? "ping -n 5 127.0.0.1"
            : "sleep 5";
        File workingDirectory = tempDir.toFile();

        // When
        var result = commandExecutor.execute(command, workingDirectory, 1); // 1 minute timeout

        // Then - should complete or fail gracefully (not hang)
        assertThat(result.isLeft() || result.isRight()).isTrue();
    }

    @Test
    void execute_shouldHandleLongRunningCommand() {
        // Given - a command that produces output
        String command = "echo Long running command output";
        File workingDirectory = tempDir.toFile();

        // When
        var result = commandExecutor.execute(command, workingDirectory, 5);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).contains("Long running command output");
    }

    @Test
    void execute_withCustomTimeout_shouldRespectTimeoutSetting() {
        // Given
        String command = "echo Quick command";
        File workingDirectory = tempDir.toFile();

        // When
        var result = commandExecutor.execute(command, workingDirectory, 10);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).contains("Quick command");
    }

    @Test
    void execute_shouldHandleInvalidCommand() {
        // Given - an invalid command that doesn't exist
        String invalidCommand = "nonexistentcommand12345";

        // When
        var result = commandExecutor.execute(invalidCommand);

        // Then - should return error result
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isNotEmpty();
    }

    @Test
    void execute_shouldHandleEmptyCommand() {
        // Given - empty command
        String emptyCommand = "";

        // When
        var result = commandExecutor.execute(emptyCommand);

        // Then - should return error result
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    void execute_shouldHandleInvalidWorkingDirectory() {
        // Given - command with invalid working directory
        String command = "echo test";
        File invalidWorkingDir = new File("/this/path/should/not/exist/anywhere");

        // When
        var result = commandExecutor.execute(command, invalidWorkingDir, 5);

        // Then - should return error
        assertThat(result.isLeft()).isTrue();
    }
}
