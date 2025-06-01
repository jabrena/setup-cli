package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.jspecify.annotations.NonNull;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

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
    public Either<String, String> execute() {
        Path currentPath = Paths.get(System.getProperty("user.dir"));

        copyFiles.copyClasspathFolder( "editorconfig/", currentPath);
        return Either.right("EditorConfig support added successfully");
    }
}
