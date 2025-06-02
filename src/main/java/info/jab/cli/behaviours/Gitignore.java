package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

public class Gitignore implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(Gitignore.class);

    private final CopyFiles copyFiles;

    public Gitignore() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    Gitignore(CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public Either<String, String> execute() {
        logger.info("Executing command to add .gitignore file");

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path gitignoreFile = currentPath.resolve(".gitignore");
        String resourcePath = "templates/gitignore/gitignore.template";
        copyFiles.copyClasspathFileWithRename(resourcePath, gitignoreFile);

        return Either.right("Command execution completed successfully");
    }
}
