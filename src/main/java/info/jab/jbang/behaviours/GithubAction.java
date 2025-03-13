package info.jab.jbang.behaviours;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class GithubAction implements Behaviour0 {

    @Override
    public void execute() {
        copyGithubActionFiles();
        System.out.println("GitHub Actions workflow added successfully");
    }
    
    void copyGithubActionFiles() {
        try {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path workflowsPath = currentPath.resolve(".github").resolve("workflows");
            
            // Create .github/workflows directory if it doesn't exist
            FileUtils.forceMkdir(workflowsPath.toFile());
            
            // Copy maven.yaml from resources to .github/workflows
            try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("github-action/maven.yaml")) {
                if (resourceStream == null) {
                    throw new IOException("Resource not found: github-action/maven.yaml");
                }
                FileUtils.copyInputStreamToFile(resourceStream, workflowsPath.resolve("maven.yaml").toFile());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying GitHub Actions workflow file", e);
        }
    }
}
