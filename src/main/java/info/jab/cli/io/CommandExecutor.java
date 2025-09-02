package info.jab.cli.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import io.vavr.control.Either;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * CommandExecutor implementation using zt-exec library.
 */
public class CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    private static final int DEFAULT_TIMEOUT_MINUTES = 20;

    /**
     * Executes a command synchronously.
     *
     * @param command the command to execute
     * @return Either<String, String> the result of command execution
     */
    public Either<String, String> execute(String command) {
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
    public Either<String, String> execute(String command, File workingDirectory, int timeoutMinutes) {
        try {
            logger.info("Executing command: {} in directory: {}", command, workingDirectory.getAbsolutePath());

            if (command == null || command.trim().isEmpty()) {
                logger.error("Command cannot be null or empty");
                return Either.left("Command cannot be null or empty");
            }

            List<String> commandParts = parseCommand(command.trim());

            if (commandParts.isEmpty()) {
                logger.error("No valid command parts found after parsing: {}", command);
                return Either.left("No valid command parts found");
            }

            ProcessResult result = new ProcessExecutor()
                    .command(commandParts)
                    .directory(workingDirectory)
                    .readOutput(true)
                    .timeout(timeoutMinutes, TimeUnit.MINUTES)
                    .execute();

            boolean success = result.getExitValue() == 0;
            String output = result.outputUTF8();

            //TODO Refactor in the future.
            if (success) {
                logger.info("Command executed successfully");
                if (logger.isDebugEnabled()) {
                    logger.debug("Command output: {}", output);
                }
                return Either.right(output);
            } else {
                logger.error("Command failed with exit code: {}", result.getExitValue());
                logger.error("Command output: {}", output);
                return Either.left(output);
            }

        } catch (IOException e) {
            logger.error("IO error executing command '{}': {}", command, e.getMessage());
            return Either.left(e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Command execution interrupted '{}': {}", command, e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupt status
            return Either.left(e.getMessage());
        } catch (TimeoutException e) {
            logger.error("Command execution timed out after {} minutes for '{}': {}",
                        timeoutMinutes, command, e.getMessage(), e);
            return Either.left(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error executing command '{}': {}", command, e.getMessage(), e);
            return Either.left(e.getMessage());
        }
    }

    /**
     * Parses a command string into a list of arguments, properly handling quoted strings.
     * This method handles both single and double quotes.
     *
     * @param command the command string to parse
     * @return a list of command arguments
     */
    private List<String> parseCommand(String command) {
        List<String> result = new ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;

        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);

            if (!inQuotes && (c == '\'' || c == '"')) {
                // Start of quoted string
                inQuotes = true;
                quoteChar = c;
            } else if (inQuotes && c == quoteChar) {
                // End of quoted string
                inQuotes = false;
                quoteChar = 0;
            } else if (!inQuotes && Character.isWhitespace(c)) {
                // Whitespace outside quotes - end current argument
                if (currentArg.length() > 0) {
                    result.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            } else {
                // Regular character or whitespace inside quotes
                currentArg.append(c);
            }
        }

        // Add the last argument if any
        if (currentArg.length() > 0) {
            result.add(currentArg.toString());
        }

        // Fallback to simple split if result is empty (shouldn't happen with valid input)
        if (result.isEmpty() && !command.trim().isEmpty()) {
            return Arrays.asList(command.trim().split("\\s+"));
        }

        return result;
    }
}
