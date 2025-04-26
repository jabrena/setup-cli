package info.jab.jbang.behaviours;

import info.jab.jbang.io.CopyFiles;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        List<String> files = List.of(".sdkmanrc");
        String resourcePath = "sdkman/";

        copyFiles.copyFilesToDirectory(files, resourcePath, currentPath);
    }
}
