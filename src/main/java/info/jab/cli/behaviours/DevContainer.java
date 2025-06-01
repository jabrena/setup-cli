package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jspecify.annotations.NonNull;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

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
    public Either<String, String> execute() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path devcontainerPath = currentPath.resolve(".devcontainer");

        copyFiles.copyClasspathFolder( "devcontainer/", devcontainerPath);

        return Either.right("Devcontainer support added successfully");
    }
}
