package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

public class GithubAction implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(GithubAction.class);

    private final CopyFiles copyFiles;

    public GithubAction() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    GithubAction(CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public Either<String, String> execute() {
        logger.info("Executing command to add GitHub Actions workflow (.github/workflows/maven.yaml)");

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path workflowsPath = currentPath.resolve(".github").resolve("workflows");
        copyFiles.copyClasspathFolder( "github-action-template/", workflowsPath);

        return Either.right("Command execution completed successfully");
    }
}
