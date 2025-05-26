package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import info.jab.cli.io.CopyFiles;

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
    public void execute() {
        copySdkmanFiles();
        System.out.println("SDKMAN support added successfully");
    }

    void copySdkmanFiles() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        String resourcePath = "sdkman/";
        copyFiles.copyClasspathFolder(resourcePath, currentPath);
    }
}
