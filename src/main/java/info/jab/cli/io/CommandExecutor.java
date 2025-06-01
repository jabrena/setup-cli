package info.jab.cli.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * CommandExecutor implementation using zt-exec library.
 */
public class CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
    private static final int DEFAULT_TIMEOUT_MINUTES = 10;

    /**
     * Executes a command synchronously.
     *
     * @param command the command to execute
     * @return the result of command execution
     * @throws CommandExecutionException if command execution fails
     */
    public CommandResult execute(String command) {
        File workingDirectory = new File(System.getProperty("user.dir"));
        return execute(command, workingDirectory, DEFAULT_TIMEOUT_MINUTES);
    }

    /**
     * Executes a command synchronously with custom working directory and timeout.
     *
     * @param command the command to execute
     * @param workingDirectory the directory to execute the command in
     * @param timeoutMinutes timeout in minutes
     * @return the result of command execution
     * @throws CommandExecutionException if command execution fails
     */
    public CommandResult execute(String command, File workingDirectory, int timeoutMinutes) {
        try {
            logger.info("Executing command: {} in directory: {}", command, workingDirectory.getAbsolutePath());

            List<String> commandParts = Arrays.asList(command.trim().split("\\s+"));

            ProcessResult result = new ProcessExecutor()
                    .command(commandParts)
                    .directory(workingDirectory)
                    .readOutput(true)
                    .timeout(timeoutMinutes, TimeUnit.MINUTES)
                    .execute();

            boolean success = result.getExitValue() == 0;
            String output = result.outputUTF8();

            if (success) {
                logger.info("Command executed successfully");
                if (logger.isDebugEnabled()) {
                    logger.debug("Command output: {}", output);
                }
                return CommandResult.success(output);
            } else {
                logger.error("Command failed with exit code: {}", result.getExitValue());
                logger.error("Command output: {}", output);
                return CommandResult.failure(result.getExitValue(), output, "");
            }

        } catch (IOException e) {
            logger.error("IO error executing command '{}': {}", command, e.getMessage(), e);
            throw new CommandExecutionException(command, "IO error: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("Command execution interrupted '{}': {}", command, e.getMessage(), e);
            Thread.currentThread().interrupt(); // Restore interrupt status
            throw new CommandExecutionException(command, "Execution interrupted: " + e.getMessage(), e);
        } catch (TimeoutException e) {
            logger.error("Command execution timed out after {} minutes for '{}': {}",
                        timeoutMinutes, command, e.getMessage(), e);
            throw new CommandExecutionException(command,
                    String.format("Execution timed out after %d minutes", timeoutMinutes), e);
        } catch (Exception e) {
            logger.error("Unexpected error executing command '{}': {}", command, e.getMessage(), e);
            throw new CommandExecutionException(command, "Unexpected error: " + e.getMessage(), e);
        }
    }

    /**
     * Executes a command asynchronously.
     *
     * @param command the command to execute
     * @return a future containing the result of command execution
     */
    public CompletableFuture<CommandResult> executeAsync(String command) {
        return CompletableFuture.supplyAsync(() -> execute(command));
    }

    /**
     * Represents the result of a command execution.
     */
    public record CommandResult(
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
    public static class CommandExecutionException extends RuntimeException {
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
