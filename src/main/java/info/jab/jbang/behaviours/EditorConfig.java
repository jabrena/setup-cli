package info.jab.jbang.behaviours;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class EditorConfig implements Behaviour0 {

    @Override
    public void execute() {
        copyEditorConfigFiles();
        System.out.println("EditorConfig support added successfully");
    }
    
    void copyEditorConfigFiles() {
        try {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path editorConfigFile = currentPath.resolve(".editorconfig");
            
            // Copy .editorconfig from resources
            try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("editorconfig/.editorconfig")) {
                if (resourceStream == null) {
                    throw new IOException("Resource not found: editorconfig/.editorconfig");
                }
                FileUtils.copyInputStreamToFile(resourceStream, editorConfigFile.toFile());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying EditorConfig file", e);
        }
    }
}
