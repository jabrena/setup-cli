package info.jab.jbang;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CopyRules {
    private static final String RULES_FILE = "rules.properties";
    private static final String RULES_PREFIX = "rules.file.";
    
    public List<String> getRuleFiles() {
        List<String> ruleFiles = new ArrayList<>();
        Properties properties = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(RULES_FILE)) {
            properties.load(input);
            
            // Iterar sobre las propiedades numeradas
            int index = 1;
            String ruleFile;
            while ((ruleFile = properties.getProperty(RULES_PREFIX + index)) != null) {
                ruleFiles.add(ruleFile);
                index++;
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Error loading rules files", e);
        }
        
        return ruleFiles;
    }

    public void copyRulesToDirectory() throws IOException {
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path cursorPath = currentPath.resolve(".cursor");
        Path rulesPath = cursorPath.resolve("rules");
        
        // Crear directorios necesarios
        Files.createDirectories(rulesPath);
        
        // Obtener la lista de archivos de reglas usando getRuleFiles()
        List<String> ruleFiles = getRuleFiles();
        
        // Copiar cada archivo de reglas
        for (String fileName : ruleFiles) {
            try (InputStream fileIs = getClass().getResourceAsStream("/.cursor/rules/" + fileName)) {
                if (fileIs == null) {
                    throw new IOException("No se encontró el archivo: " + fileName);
                }
                Files.copy(fileIs, rulesPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

}