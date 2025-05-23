package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

        List<String> files = List.of("devcontainer.json");

        copyFiles.copyFilesToDirectory(files, "devcontainer/", devcontainerPath);

        System.out.println("Devcontainer support added successfully");
    }

    /*
    void copyDevContainerFiles() {
        try {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path devcontainerPath = currentPath.resolve(".devcontainer");

            // Clean existing .devcontainer directory if it exists
            if (Files.exists(devcontainerPath)) {
                FileUtils.cleanDirectory(devcontainerPath.toFile());
            }

            // Create .devcontainer directory if it doesn't exist
            FileUtils.forceMkdir(devcontainerPath.toFile());

            // Copy files from resources/devcontainer to .devcontainer
            String resourcePath = "devcontainer/";
            String[] files = {"Dockerfile", "devcontainer.json"};

            for (String fileName : files) {
                try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath + fileName)) {
                    if (resourceStream == null) {
                        throw new IOException("Resource not found: " + resourcePath + fileName);
                    }
                    FileUtils.copyInputStreamToFile(resourceStream, devcontainerPath.resolve(fileName).toFile());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying devcontainer files", e);
        }
    }
    */
}
