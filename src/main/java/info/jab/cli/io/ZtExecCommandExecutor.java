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
 * Implementation of CommandExecutor using zt-exec library.
 */
public class ZtExecCommandExecutor implements CommandExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ZtExecCommandExecutor.class);
    private static final int DEFAULT_TIMEOUT_MINUTES = 10;

    @Override
    public CommandResult execute(String command) {
        File workingDirectory = new File(System.getProperty("user.dir"));
        return execute(command, workingDirectory, DEFAULT_TIMEOUT_MINUTES);
    }

    @Override
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

    @Override
    public CompletableFuture<CommandResult> executeAsync(String command) {
        return CompletableFuture.supplyAsync(() -> execute(command));
    }
}
