package info.jab.jbang;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * This class is used to copy the Cursor AI rules files to the repository
 */
public class CopyRules {

    public void copyCursorRulesToDirectory(List<String> ruleFiles) {
        try {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path cursorPath = currentPath.resolve(".cursor");
            Path rulesPath = cursorPath.resolve("rules");
            
            Files.createDirectories(rulesPath);
                        
            for (String fileName : ruleFiles) {
                try (InputStream fileIs = getClass().getResourceAsStream("/java/.cursor/rules/" + fileName)) {
                    if (fileIs == null) {
                        throw new IOException("File not found: " + fileName);
                    }
                    Files.copy(fileIs, rulesPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying rules files", e);
        }
    }

}