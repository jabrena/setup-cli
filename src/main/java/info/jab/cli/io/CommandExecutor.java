package info.jab.cli.io;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for executing system commands.
 * This abstraction allows for easy mocking in tests and different implementations.
 */
public interface CommandExecutor {

    /**
     * Executes a command synchronously.
     *
     * @param command the command to execute
     * @return the result of command execution
     * @throws CommandExecutionException if command execution fails
     */
    CommandResult execute(String command);

    /**
     * Executes a command synchronously with custom working directory and timeout.
     *
     * @param command the command to execute
     * @param workingDirectory the directory to execute the command in
     * @param timeoutMinutes timeout in minutes
     * @return the result of command execution
     * @throws CommandExecutionException if command execution fails
     */
    CommandResult execute(String command, File workingDirectory, int timeoutMinutes);

    /**
     * Executes a command asynchronously.
     *
     * @param command the command to execute
     * @return a future containing the result of command execution
     */
    CompletableFuture<CommandResult> executeAsync(String command);

    /**
     * Represents the result of a command execution.
     */
    record CommandResult(
        int exitCode,
        String output,
        String errorOutput,
        boolean success
    ) {
        public static CommandResult success(String output) {
            return new CommandResult(0, output, "", true);
        }

        public static CommandResult failure(int exitCode, String output, String errorOutput) {
            return new CommandResult(exitCode, output, errorOutput, false);
        }
    }

    /**
     * Exception thrown when command execution fails.
     */
    class CommandExecutionException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private final String command;
        private final int exitCode;

        public CommandExecutionException(String command, int exitCode, String message) {
            super(message);
            this.command = command;
            this.exitCode = exitCode;
        }

        public CommandExecutionException(String command, String message, Throwable cause) {
            super(message, cause);
            this.command = command;
            this.exitCode = -1;
        }

        public String getCommand() { return command; }
        public int getExitCode() { return exitCode; }
    }
}
