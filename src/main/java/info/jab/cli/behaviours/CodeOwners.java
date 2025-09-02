package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

public class CodeOwners implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(CodeOwners.class);

    private final CopyFiles copyFiles;

    public CodeOwners() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    CodeOwners(CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public Either<String, String> execute() {
        logger.info("Executing command to add CODEOWNERS file");

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path githubDir = currentPath.resolve(".github");
        Path codeownersFile = githubDir.resolve("CODEOWNERS");
        String resourcePath = "templates/codeowners/CODEOWNERS.template";
        
        // Ensure .github directory exists
        if (!githubDir.toFile().exists()) {
            githubDir.toFile().mkdirs();
            logger.info("Created .github directory");
        }
        
        copyFiles.copyClasspathFileWithRename(resourcePath, codeownersFile);

        return Either.right("CODEOWNERS file created successfully in .github/CODEOWNERS");
    }
}