package info.jab.cli.behaviours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CommandExecutor;
import info.jab.cli.io.ZtExecCommandExecutor;

import java.io.File;
import java.util.Objects;

public class Maven implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(Maven.class);

    private final CommandExecutor commandExecutor;
    private final FileSystemChecker fileSystemChecker;

    /**
     * Interface for file system operations to enable dependency injection and testing
     */
    public interface FileSystemChecker {
        boolean fileExists(String filename);
    }

    /**
     * Default implementation using java.io.File
     */
    public static class DefaultFileSystemChecker implements FileSystemChecker {
        @Override
        public boolean fileExists(String filename) {
            return new File(filename).exists();
        }
    }

    //https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
    private final String commands = """
            mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false
            """;

    // Constructor injection for dependency inversion
    public Maven(CommandExecutor commandExecutor, FileSystemChecker fileSystemChecker) {
        this.commandExecutor = Objects.requireNonNull(commandExecutor, "CommandExecutor cannot be null");
        this.fileSystemChecker = Objects.requireNonNull(fileSystemChecker, "FileSystemChecker cannot be null");
    }

    // Constructor injection with default FileSystemChecker
    public Maven(CommandExecutor commandExecutor) {
        this(commandExecutor, new DefaultFileSystemChecker());
    }

    // Default constructor using the real implementations
    public Maven() {
        this(new ZtExecCommandExecutor(), new DefaultFileSystemChecker());
    }

    @Override
    public void execute() {
        if (!isMavenAvailable()) {
            logger.error("Maven (mvn) command is not available on this system");
            throw new IllegalStateException("Maven command not found. Please install Maven and ensure it's in your PATH.");
        }

        if (pomXmlExists()) {
            logger.error("A pom.xml file already exists in the current directory");
            throw new IllegalStateException("Cannot create Maven project: pom.xml already exists in current directory. Please run this command in an empty directory.");
        }

        commands.lines()
               .filter(line -> !line.trim().isEmpty())
               .forEach(this::executeCommand);
    }

    private void executeCommand(String command) {
        try {
            logger.info("Executing Maven command: {}", command);
            CommandExecutor.CommandResult result = commandExecutor.execute(command);

            if (result.success()) {
                logger.info("Maven command completed successfully");
            } else {
                logger.error("Maven command failed with exit code: {}", result.exitCode());
                logger.error("Command output: {}", result.output());
            }

        } catch (CommandExecutor.CommandExecutionException e) {
            logger.error("Failed to execute Maven command '{}': {}", command, e.getMessage(), e);
        }
    }

    /**
     * Alternative method that continues execution even if some commands fail
     */
    public void executeWithContinueOnError() {
        commands.lines()
               .filter(line -> !line.trim().isEmpty())
               .forEach(this::executeCommandSafely);
    }

    private void executeCommandSafely(String command) {
        try {
            executeCommand(command);
        } catch (Exception e) {
            logger.warn("Maven command failed but continuing: {}", command, e);
        }
    }

    /**
     * Checks if Maven command is available on the system
     * @return true if Maven is available, false otherwise
     */
    public boolean isMavenAvailable() {
        try {
            logger.debug("Checking if Maven is available...");
            CommandExecutor.CommandResult result = commandExecutor.execute("mvn --version");

            if (result.success()) {
                logger.info("Maven is available: {}", result.output().lines().findFirst().orElse("Version info not available"));
                return true;
            } else {
                logger.warn("Maven version check failed with exit code: {}", result.exitCode());
                return false;
            }

        } catch (CommandExecutor.CommandExecutionException e) {
            logger.warn("Maven availability check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a pom.xml file exists in the current directory
     * @return true if pom.xml exists, false otherwise
     */
    public boolean pomXmlExists() {
        boolean exists = fileSystemChecker.fileExists("pom.xml");

        if (exists) {
            logger.debug("Found existing pom.xml file in current directory");
        } else {
            logger.debug("No pom.xml file found in current directory");
        }

        return exists;
    }
}
