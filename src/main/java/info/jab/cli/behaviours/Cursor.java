package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

import info.jab.cli.CursorOptions;
import info.jab.cli.CursorOptions.CursorOption;
import info.jab.cli.io.CopyFiles;

public class Cursor implements Behaviour1 {

    // Resource base paths within the JAR/classpath
    private static final String CURSOR_RULES_JAVA_BASE_PATH = "cursor-rules-java/.cursor/rules/";
    private static final String CURSOR_RULES_TASKS_BASE_PATH = "cursor-rules-tasks/";
    private static final String CURSOR_RULES_AGILE_BASE_PATH = "cursor-rules-agile/";

    // File lists for each rule set
    private static final List<String> ALL_JAVA_RULES_FILES = List.of(
            "100-java-maven-best-practices.mdc",
            "110-java-acceptance-criteria.mdc",
            "111-java-object-oriented-design.mdc",
            "112-java-type-design.mdc",
            "113-java-general-guidelines.mdc",
            "114-java-secure-coding.mdc",
            "115-java-concurrency.mdc",
            "116-java-logging.mdc",
            "121-java-unit-testing.mdc",
            "122-java-integration-testing.mdc",
            "131-java-refactoring-with-modern-features.mdc",
            "132-java-functional-programming.mdc",
            "133-java-data-oriented-programming.mdc",
            "201-book-effective-java.mdc",
            "202-book-pragmatic-unit-testing.mdc",
            "203-book-refactoring.mdc",
            "500-sql.mdc"
    );

    private static final List<String> QUARKUS_SPECIFIC_FILES = List.of("401-framework-quarkus.mdc");
    private static final List<String> SPRING_BOOT_SPECIFIC_FILES = List.of("301-framework-spring-boot.mdc", "304-java-rest-api-design.mdc");

    private static final List<String> JAVA_RULES_FOR_SPRING_BOOT = Stream.of(
        ALL_JAVA_RULES_FILES,
        SPRING_BOOT_SPECIFIC_FILES)
        .flatMap(List::stream)
        .toList();

    private static final List<String> JAVA_RULES_FOR_QUARKUS = Stream.of(
        ALL_JAVA_RULES_FILES,
        QUARKUS_SPECIFIC_FILES)
        .flatMap(List::stream)
        .toList();

    private static final List<String> TASKS_FILES = List.of(
            "1000-create-prd.mdc",
            "1001-generate-tasks-from-prd.mdc",
            "1002-task-list.mdc"
    );

    private static final List<String> AGILE_FILES = List.of(
            "2000-agile-user-story.mdc"
    );

    private final CopyFiles copyFiles;

    public Cursor() {
        this.copyFiles = new CopyFiles();
    }

    Cursor(@NonNull CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public void execute(@NonNull String parameter) {
        if (Objects.isNull(parameter)) {
            return;
        }
        if (!CursorOptions.isValidOption(parameter)) {
            throw new IllegalArgumentException("Invalid parameter: " + parameter);
        }

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path cursorPath = currentPath.resolve(".cursor");
        Path rulesPath = cursorPath.resolve("rules");

        CursorOption option = CursorOption.fromString(parameter)
            .orElseThrow(() -> new IllegalStateException(
                "Internal error: Parameter '" + parameter + "' was validated but fromString returned empty."));

        switch (option) {
            case JAVA -> copyFiles.copyFilesToDirectory(ALL_JAVA_RULES_FILES, CURSOR_RULES_JAVA_BASE_PATH, rulesPath);
            case JAVA_SPRING_BOOT -> copyFiles.copyFilesToDirectory(JAVA_RULES_FOR_SPRING_BOOT, CURSOR_RULES_JAVA_BASE_PATH, rulesPath);
            case JAVA_QUARKUS -> copyFiles.copyFilesToDirectory(JAVA_RULES_FOR_QUARKUS, CURSOR_RULES_JAVA_BASE_PATH, rulesPath);
            case TASKS -> copyFiles.copyFilesToDirectory(TASKS_FILES, CURSOR_RULES_TASKS_BASE_PATH, rulesPath);
            case AGILE -> copyFiles.copyFilesToDirectory(AGILE_FILES, CURSOR_RULES_AGILE_BASE_PATH, rulesPath);
        }

        System.out.println("Cursor rules added successfully");
    }
}
