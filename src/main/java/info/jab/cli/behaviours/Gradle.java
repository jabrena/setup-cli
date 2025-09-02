package info.jab.cli.behaviours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CommandExecutor;
import info.jab.cli.io.FileSystemChecker;
import io.vavr.control.Either;

import java.util.Objects;

public class Gradle implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(Gradle.class);

    private final CommandExecutor commandExecutor;
    private final FileSystemChecker fileSystemChecker;

    // Gradle init command to create a basic Java project
    private final String commands = """
            gradle init --type java-application --dsl groovy --test-framework junit-jupiter --package info.jab.demo --project-name gradle-demo --no-incubating
            """;

    /**
     * Constructor for Gradle with dependency injection.
     * @param commandExecutor the command executor to use
     * @param fileSystemChecker the file system checker to use
     * @throws IllegalArgumentException if either parameter is null
     */
    public Gradle(CommandExecutor commandExecutor, FileSystemChecker fileSystemChecker) {
        if (Objects.isNull(commandExecutor)) {
            throw new IllegalArgumentException("CommandExecutor cannot be null");
        }
        if (Objects.isNull(fileSystemChecker)) {
            throw new IllegalArgumentException("FileSystemChecker cannot be null");
        }
        this.commandExecutor = commandExecutor;
        this.fileSystemChecker = fileSystemChecker;
    }

    // Constructor injection with default FileSystemChecker
    public Gradle(CommandExecutor commandExecutor) {
        this(commandExecutor, new FileSystemChecker());
    }

    // Default constructor for production use
    public Gradle() {
        this(new CommandExecutor(), new FileSystemChecker());
    }

    @Override
    public Either<String, String> execute() {
        //Preconditions
        if (!isGradleAvailable()) {
            String message = "Gradle command not found. Please install Gradle with 'sdk install gradle' and ensure it's in your PATH.";
            logger.error(message);
            return Either.left("Command execution failed");
        }

        if (buildGradleExists()) {
            String message = "A build.gradle file already exists in the current directory";
            logger.error(message);
            return Either.left("Command execution failed");
        }

        return commands.lines()
                .filter(line -> !line.trim().isEmpty())
                .findFirst()
                .map(this::executeCommand)
                .orElse(Either.left("No commands found to execute"));
    }

    private Either<String, String> executeCommand(String command) {
        logger.info("Executing Gradle command: {}", command);
        Either<String, String> result = commandExecutor.execute(command);

        if (result.isRight()) {
            return Either.right("Command execution completed successfully");
        }
        return Either.left("Command execution failed");
    }

    /**
     * Checks if Gradle command is available on the system
     * @return true if Gradle is available, false otherwise
     */
    public boolean isGradleAvailable() {
        logger.debug("Checking if Gradle is available...");
        Either<String, String> result = commandExecutor.execute("gradle --version");

        if (result.isRight()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a build.gradle file exists in the current directory
     * @return true if build.gradle exists, false otherwise
     */
    public boolean buildGradleExists() {
        boolean exists = fileSystemChecker.fileExists("build.gradle") || 
                        fileSystemChecker.fileExists("build.gradle.kts");

        if (exists) {
            logger.debug("Found existing build.gradle file in current directory");
        } else {
            logger.debug("No build.gradle file found in current directory");
        }

        return exists;
    }
}