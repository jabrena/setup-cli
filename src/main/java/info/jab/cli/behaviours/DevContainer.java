package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

public class DevContainer implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(DevContainer.class);

    private final CopyFiles copyFiles;

    public DevContainer() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    DevContainer(CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public Either<String, String> execute() {
        logger.info("Executing command to add Devcontainer support (.devcontainer/devcontainer.json)");

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path devcontainerPath = currentPath.resolve(".devcontainer");
        copyFiles.copyClasspathFolder( "templates/devcontainer/", devcontainerPath);

        return Either.right("Command execution completed successfully");
    }
}
