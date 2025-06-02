package info.jab.cli.behaviours;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

public class Gitignore implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(Gitignore.class);

    private final CopyFiles copyFiles;

    //Maintain the content in file, because conflicts with maven resource plugin
    private static final String GITIGNORE_FILE = ".gitignore";
    private static final String GITIGNORE_CONTENT = """
        .DS_Store
        target/
        .idea/
        .vscode/
        .cursor/
        .flattened-pom.xml
        *.log
        .classpath
        """;

    public Gitignore() {
        this.copyFiles = new CopyFiles();
    }

    // Constructor for testing with a mock
    Gitignore(CopyFiles copyFiles) {
        this.copyFiles = copyFiles;
    }

    @Override
    public Either<String, String> execute() {
        logger.info("Executing command to add .gitignore file");

        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path gitignorePath = currentPath.resolve(GITIGNORE_FILE);
        copyFiles.copyContentToFile(GITIGNORE_CONTENT, gitignorePath);

        return Either.right("Command execution completed successfully");
    }
}
