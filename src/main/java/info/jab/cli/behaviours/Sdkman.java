package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jspecify.annotations.NonNull;

import info.jab.cli.io.CopyFiles;

public class Sdkman implements Behaviour0 {

    private final CopyFiles copyFiles;

    public Sdkman() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    Sdkman(@NonNull CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public void execute() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        String resourcePath = "sdkman/";
        copyFiles.copyClasspathFolder(resourcePath, currentPath);

        System.out.println("SDKMAN support added successfully");
    }
}
