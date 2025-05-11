package info.jab.cli.behaviours;

import info.jab.cli.io.CopyFiles;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jspecify.annotations.NonNull;

public class GitIgnore implements Behaviour0 {

    private final CopyFiles copyFiles;

    public GitIgnore() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    GitIgnore(@NonNull CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public void execute() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        List<String> files = List.of(".gitignore");

        copyFiles.copyFilesToDirectory(files, "gitignore/", currentPath);
        System.out.println("Gitignore support added successfully");
    }
}
