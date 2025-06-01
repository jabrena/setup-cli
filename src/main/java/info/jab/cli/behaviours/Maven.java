package info.jab.cli.behaviours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CommandExecutor;
import info.jab.cli.io.FileSystemChecker;
import io.vavr.control.Either;

import java.util.Objects;

public class Maven implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(Maven.class);

    private final CommandExecutor commandExecutor;
    private final FileSystemChecker fileSystemChecker;

    //https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
    private final String commands = """
            mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false
            """;

    // Full constructor for dependency injection
    /**
     * Constructor for Maven with dependency injection.
     * @param commandExecutor the command executor to use
     * @param fileSystemChecker the file system checker to use
     * @throws IllegalArgumentException if either parameter is null
     */
    public Maven(CommandExecutor commandExecutor, FileSystemChecker fileSystemChecker) {
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
    public Maven(CommandExecutor commandExecutor) {
        this(commandExecutor, new FileSystemChecker());
    }

    // Default constructor for production use
    public Maven() {
        this(new CommandExecutor(), new FileSystemChecker());
    }

    @Override
    public Either<String, String> execute() {
        //Preconditions
        if (!isMavenAvailable()) {
            logger.error("Maven (mvn) command is not available on this system");
            logger.error("Please install Maven and ensure it's in your PATH.");
            logger.error("sdkman install maven");
            return Either.left("Maven command not found. Please install Maven and ensure it's in your PATH.");
        }

        if (pomXmlExists()) {
            logger.error("A pom.xml file already exists in the current directory");
            return Either.left("Cannot create Maven project: pom.xml already exists in current directory. Please run this command in an empty directory.");
        }

        return commands.lines()
                .filter(line -> !line.trim().isEmpty())
                .findFirst()
                .map(this::executeCommand)
                .orElse(Either.left("No Maven commands found to execute"));
    }

    private Either<String, String> executeCommand(String command) {
        logger.info("Executing Maven command: {}", command);
        Either<String, String> result = commandExecutor.execute(command);

        if (result.isRight()) {
            return Either.right("Maven command completed successfully");
        }
        return Either.left("Maven command failed");
    }

    /**
     * Checks if Maven command is available on the system
     * @return true if Maven is available, false otherwise
     */
    public boolean isMavenAvailable() {
        logger.debug("Checking if Maven is available...");
        Either<String, String> result = commandExecutor.execute("mvn --version");

        if (result.isRight()) {
            logger.info("Maven is available: {}", result.get().lines().findFirst().orElse("Version info not available"));
            return true;
        } else {
            logger.warn("Maven version check failed with exit code: {}", result.getLeft());
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
