package info.jab.cli.behaviours;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CommandExecutor;
import info.jab.cli.io.FileSystemChecker;
import io.vavr.control.Either;

public class QuarkusCli implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(QuarkusCli.class);

    private final CommandExecutor commandExecutor;
    private final FileSystemChecker fileSystemChecker;

    private String commands = """
            quarkus create app quarkus-demo
            """;

    public QuarkusCli(CommandExecutor commandExecutor, FileSystemChecker fileSystemChecker) {
        if (Objects.isNull(commandExecutor)) {
            throw new IllegalArgumentException("CommandExecutor cannot be null");
        }
        if (Objects.isNull(fileSystemChecker)) {
            throw new IllegalArgumentException("FileSystemChecker cannot be null");
        }
        this.commandExecutor = commandExecutor;
        this.fileSystemChecker = fileSystemChecker;
    }

    // Default constructor for production use
    public QuarkusCli() {
        this(new CommandExecutor(), new FileSystemChecker());
    }

    @Override
    public Either<String, String> execute() {

        //Preconditions
        if (!isQuarkusCliAvailable()) {
            logger.error("Quarkus command is not available on this system");
            logger.error("Please install Quarkus and ensure it's in your PATH.");
            logger.error("sdk install quarkus");
            return Either.left("Quarkus command not found. Please install Quarkus and ensure it's in your PATH.");
        }

        if (pomXmlExists()) {
            logger.error("A pom.xml file already exists in the current directory");
            return Either.left("Cannot create Maven project: pom.xml already exists in current directory. Please run this command in an empty directory.");
        }

        //TODO: Review in the future how to execute multiple commands
        return commands.lines()
                .filter(line -> !line.trim().isEmpty())
                .findFirst()
                .map(this::executeCommand)
                .orElse(Either.left("No Quarkus commands found to execute"));
    }

    private Either<String, String> executeCommand(String command) {
        logger.info("Executing Quarkus command: {}", command);
        Either<String, String> result = commandExecutor.execute(command);

        if (result.isRight()) {
            return Either.right("Quarkus command completed successfully");
        }
        return Either.left("Quarkus command failed");
    }

    /**
     * Checks if Quarkus CLI command is available on the system
     * @return true if Quarkus CLI is available, false otherwise
     */
    public boolean isQuarkusCliAvailable() {
        logger.debug("Checking if Quarkus CLI is available...");
        Either<String, String> result = commandExecutor.execute("quarkus --version");

        if (result.isRight()) {
            logger.info("Quarkus is available: {}", result.get().lines().findFirst().orElse("Version info not available"));
            return true;
        } else {
            logger.warn("Quarkus version check failed with exit code: {}", result.getLeft());
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
