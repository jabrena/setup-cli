package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

public class GithubAction implements Behaviour0 {

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
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path workflowsPath = currentPath.resolve(".github").resolve("workflows");

        copyFiles.copyClasspathFolder( "github-action/", workflowsPath);
        return Either.right("GitHub Actions workflow added successfully");
    }
}
