package info.jab.jbang.behaviours;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import info.jab.jbang.CursorOptions;
import java.util.Objects;

public class Cursor implements Behaviour1 {

    @Override
    public void execute(String parameter) {
        if (Objects.isNull(parameter)) {
            return;
        }
        
        List<String> ruleJavaFiles = getProperties();
        //Spring Boot support (Alpha)
        if(parameter.equals("java-spring-boot")) {
            ruleJavaFiles.add("301-framework-spring-boot.mdc");
        }
        //Quarkus support (Max`s help)
        if(parameter.equals("java-quarkus")) {
            ruleJavaFiles.add("401-framework-quarkus.mdc");
        }

        if(CursorOptions.isValidOption(parameter)) {
            if(parameter.equals("processes")) {
                List<String> ruleProcessesFiles = List.of(
                    "1000-create-prd.mdc",
                    "1001-generate-tasks-from-prd.mdc",
                    "1002-task-list.mdc");
                copyProcessesCursorRulesToDirectory(ruleProcessesFiles);
            } else {
                copyJavaCursorRulesToDirectory(ruleJavaFiles);
            }
            System.out.println("Cursor rules added successfully");
        }
    }

    //Load the rules files from the properties file
    List<String> getProperties() {
        final String rulesProperties = "rules.properties";
        final String keyPrefix = "rules.file.";
        
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(rulesProperties)) {
            properties.load(input);
            return properties.stringPropertyNames().stream()
                    .filter(key -> key.startsWith(keyPrefix))
                    .map(properties::getProperty)
                    .collect(Collectors.toList());//Mutable list
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties", e);
        }
    }

    void copyJavaCursorRulesToDirectory(List<String> ruleFiles) {
        try {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path cursorPath = currentPath.resolve(".cursor");
            Path rulesPath = cursorPath.resolve("rules");

            // Create rules directory if it doesn't exist
            FileUtils.forceMkdir(rulesPath.toFile());

            // Copy rule files to the rules directory
            for (String fileName : ruleFiles) {
                // Use the correct path found in the JAR, starting from root
                try (InputStream resourceStream = getClass().getResourceAsStream("/cursor-rules-java/.cursor/rules/" + fileName)) {
                    if (resourceStream == null) {
                        throw new IOException("Resource not found at /cursor-rules-java/.cursor/rules/: " + fileName);
                    }
                    FileUtils.copyInputStreamToFile(resourceStream, rulesPath.resolve(fileName).toFile());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying rules files", e);
        }
    }

    void copyProcessesCursorRulesToDirectory(List<String> ruleFiles) {
        try {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path cursorPath = currentPath.resolve(".cursor");
            Path rulesPath = cursorPath.resolve("rules");

            // Create rules directory if it doesn't exist
            FileUtils.forceMkdir(rulesPath.toFile());

            // Copy rule files to the rules directory
            for (String fileName : ruleFiles) {
                try (InputStream resourceStream = getClass().getResourceAsStream("/cursor-rules-processes/.cursor/rules/" + fileName)) {
                    if (resourceStream == null) {
                        throw new IOException("Resource not found: cursor-rules-processes/.cursor/rules/" + fileName);
                    }
                    FileUtils.copyInputStreamToFile(resourceStream, rulesPath.resolve(fileName).toFile());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying rules files", e);
        }
    }
}
