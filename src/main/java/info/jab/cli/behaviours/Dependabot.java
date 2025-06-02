package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

public class Dependabot implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(EditorConfig.class);

    private final CopyFiles copyFiles;

    public Dependabot() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    Dependabot(CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public Either<String, String> execute() {
        logger.info("Executing command to add dependabot.yml file");

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path githubPath = currentPath.resolve(".github");
        copyFiles.copyClasspathFolder( "templates/dependabot/", githubPath);
        return Either.right("Command execution completed successfully");
    }
}
