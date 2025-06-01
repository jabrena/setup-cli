package info.jab.cli.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import io.vavr.control.Either;

import java.io.File;
import java.io.IOException;
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

            List<String> commandParts = Arrays.asList(command.trim().split("\\s+"));

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
            logger.error("IO error executing command '{}': {}", command, e.getMessage(), e);
            return Either.left(e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Command execution interrupted '{}': {}", command, e.getMessage(), e);
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
}
