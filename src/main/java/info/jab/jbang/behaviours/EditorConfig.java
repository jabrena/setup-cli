package info.jab.jbang.behaviours;

import info.jab.jbang.io.CopyFiles;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jspecify.annotations.NonNull;

public class EditorConfig implements Behaviour0 {

    private final CopyFiles copyFiles;

    public EditorConfig() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    EditorConfig(@NonNull CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public void execute() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        List<String> files = List.of(".editorconfig");

        copyFiles.copyFilesToDirectory(files, "editorconfig/", currentPath);
        System.out.println("EditorConfig support added successfully");
    }
}
