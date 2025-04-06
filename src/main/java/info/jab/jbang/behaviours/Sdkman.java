package info.jab.jbang.behaviours;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class Sdkman implements Behaviour0 {

    @Override
    public void execute() {
        copySdkmanFiles();
        System.out.println("SDKMAN support added successfully");
    }
    
    void copySdkmanFiles() {
        try {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path sdkmanFile = currentPath.resolve(".sdkmanrc");
            
            // Copy .sdkmanrc from resources
            try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("sdkman/.sdkmanrc")) {
                if (resourceStream == null) {
                    throw new IOException("Resource not found: sdkman/.sdkmanrc");
                }
                FileUtils.copyInputStreamToFile(resourceStream, sdkmanFile.toFile());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying SDKMAN file", e);
        }
    }
}
