package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

public class Sdkman implements Behaviour0 {

    private final CopyFiles copyFiles;

    public Sdkman() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    Sdkman(CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public Either<String, String> execute() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        String resourcePath = "sdkman/";
        copyFiles.copyClasspathFolder(resourcePath, currentPath);

        return Either.right("SDKMAN support added successfully");
    }
}
