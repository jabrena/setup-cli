package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jspecify.annotations.NonNull;

import info.jab.cli.io.CopyFiles;

public class DevContainer implements Behaviour0 {

    private final CopyFiles copyFiles;

    public DevContainer() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    DevContainer(@NonNull CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public void execute() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path devcontainerPath = currentPath.resolve(".devcontainer");

        copyFiles.copyClasspathFolder( "devcontainer/", devcontainerPath);

        System.out.println("Devcontainer support added successfully");
    }
}
