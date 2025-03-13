package info.jab.jbang.behaviours;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class DevContainer implements Behaviour0 {

    @Override
    public void execute() {
        copyDevContainerFiles();
        System.out.println("Devcontainer support added successfully");
    }
    
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
}
