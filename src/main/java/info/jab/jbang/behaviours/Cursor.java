package info.jab.jbang.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import info.jab.jbang.CursorOptions;
import info.jab.jbang.io.CopyFiles;

import java.util.Objects;
import java.util.ArrayList;

public class Cursor implements Behaviour1 {

    private static final String CURSOR_RULES_JAVA_PATH = "/cursor-rules-java/";
    private static final String CURSOR_RULES_TASKS_PATH = "/cursor-rules-tasks/";

    // Instantiate CopyFiles
    private final CopyFiles copyFiles;

    public Cursor() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    Cursor(CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

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
            Path currentPath = Paths.get(System.getProperty("user.dir"));
            Path cursorPath = currentPath.resolve(".cursor");
            Path rulesPath = cursorPath.resolve("rules");

            if(parameter.equals("tasks")) {
                copyFiles.copyFilesToDirectory(ruleProcessesFiles, CURSOR_RULES_TASKS_PATH, rulesPath);
            } else {
                copyFiles.copyFilesToDirectory(ruleJavaFiles, CURSOR_RULES_JAVA_PATH, rulesPath);
            }
            System.out.println("Cursor rules added successfully");
        }
    }
}
