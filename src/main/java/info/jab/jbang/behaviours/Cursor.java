package info.jab.jbang.behaviours;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.io.FileUtils;
import info.jab.jbang.CursorOptions;
import java.util.Objects;
import java.util.ArrayList;

public class Cursor implements Behaviour1 {

    private static final String CURSOR_RULES_JAVA_PATH = "/cursor-rules-java/";
    private static final String CURSOR_RULES_TASKS_PATH = "/cursor-rules-tasks/";

    @Override
    public void execute(String parameter) {
        if (Objects.isNull(parameter)) {
            return;
        }
        
        //TODO: not maintain the list of files.
        List<String> ruleJavaFiles = new ArrayList<>(List.of(
            "100-java-general.mdc",
            "101-java-concurrency.mdc",
            "102-java-functional-programming.mdc",
            "103-java-data-oriented-programming.mdc",
            "104-java-logging.mdc",
            "105-java-modern-features.mdc",
            "201-book-effective-java.mdc",
            "202-book-pragmatic-unit-testing.mdc",
            "203-book-refactoring.mdc"
        ));
        List<String> ruleProcessesFiles = List.of(
            "1000-create-prd.mdc",
            "1001-generate-tasks-from-prd.mdc",
            "1002-task-list.mdc");

        //Spring Boot support (Alpha)
        if(parameter.equals("java-spring-boot")) {
            ruleJavaFiles.add("301-framework-spring-boot.mdc");
        }
        //Quarkus support (Max`s help)
        if(parameter.equals("java-quarkus")) {
            ruleJavaFiles.add("401-framework-quarkus.mdc");
        }

        if(CursorOptions.isValidOption(parameter)) {
            if(parameter.equals("tasks")) {
                copyCursorRulesToDirectory(ruleProcessesFiles, CURSOR_RULES_TASKS_PATH);
            } else {
                copyCursorRulesToDirectory(ruleJavaFiles, CURSOR_RULES_JAVA_PATH);
            }
            System.out.println("Cursor rules added successfully");
        }
    }

    protected void copyCursorRulesToDirectory(List<String> ruleFiles, String resourceBasePath) {
        try {
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path cursorPath = currentPath.resolve(".cursor");
            Path rulesPath = cursorPath.resolve("rules");

            // Create rules directory if it doesn't exist
            FileUtils.forceMkdir(rulesPath.toFile());

            // Copy rule files to the rules directory
            for (String fileName : ruleFiles) {
                String resourcePath = resourceBasePath + fileName;
                try (InputStream resourceStream = getClass().getResourceAsStream(resourcePath)) {
                    if (Objects.isNull(resourceStream)) {
                        throw new IOException("Resource not found at " + resourcePath);
                    }
                    FileUtils.copyInputStreamToFile(resourceStream, rulesPath.resolve(fileName).toFile());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error copying rules files", e);
        }
    }
}
