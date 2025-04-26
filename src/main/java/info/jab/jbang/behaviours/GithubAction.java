package info.jab.jbang.behaviours;

import info.jab.jbang.io.CopyFiles;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    public void execute() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path workflowsPath = currentPath.resolve(".github").resolve("workflows");
        List<String> files = List.of("maven.yaml");

        copyFiles.copyFilesToDirectory(files, "github-action/", workflowsPath);
        System.out.println("GitHub Actions workflow added successfully");
    }
}
