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

    private static final List<String> QUARKUS_SPECIFIC_FILES = List.of("401-framework-quarkus.mdc");
    private static final List<String> SPRING_BOOT_SPECIFIC_FILES = List.of("301-framework-spring-boot.mdc", "304-java-rest-api-design.mdc");

    private static final List<String> JVM_FRAMEWORKS_SPECIFIC_FILES = Stream.of(
            SPRING_BOOT_SPECIFIC_FILES,
            QUARKUS_SPECIFIC_FILES)
            .flatMap(List::stream)
            .toList();

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
            case JAVA -> copyFiles.copyClasspathFolderExcludingFiles(CURSOR_RULES_JAVA_BASE_PATH, rulesPath, JVM_FRAMEWORKS_SPECIFIC_FILES);
            case SPRING_BOOT -> copyFiles.copyClasspathFolderExcludingFiles(CURSOR_RULES_JAVA_BASE_PATH, rulesPath, QUARKUS_SPECIFIC_FILES);
            case QUARKUS -> copyFiles.copyClasspathFolderExcludingFiles(CURSOR_RULES_JAVA_BASE_PATH, rulesPath, SPRING_BOOT_SPECIFIC_FILES);
            case TASKS -> copyFiles.copyClasspathFolder(CURSOR_RULES_TASKS_BASE_PATH, rulesPath);
            case AGILE -> copyFiles.copyClasspathFolder(CURSOR_RULES_AGILE_BASE_PATH, rulesPath);
        }

        System.out.println("Cursor rules added successfully");
    }
}
