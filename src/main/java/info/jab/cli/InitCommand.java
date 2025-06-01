package info.jab.cli;

import info.jab.cli.behaviours.Cursor;
import info.jab.cli.behaviours.DevContainer;
import info.jab.cli.behaviours.EditorConfig;
import info.jab.cli.behaviours.GithubAction;
import info.jab.cli.behaviours.Gitignore;
import info.jab.cli.behaviours.JMC;
import info.jab.cli.behaviours.Maven;
import info.jab.cli.behaviours.QuarkusCli;
import info.jab.cli.behaviours.Sdkman;
import info.jab.cli.behaviours.SpringCli;
import info.jab.cli.behaviours.Visualvm;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ArgGroup;
import io.vavr.control.Either;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * CLI command for initializing project setup features.
 * This command supports various development tools and configurations.
 * Only one feature can be executed at a time to ensure proper initialization.
 */
@Command(
    name = "init",
    description = "Initialize a new repository with some useful features for Developers.",
    mixinStandardHelpOptions = true,
    sortOptions = false,
    usageHelpAutoWidth = true
)
public class InitCommand implements Runnable {

    // Mutually exclusive options - only one can be selected at a time
    @ArgGroup(exclusive = true, multiplicity = "1")
    @Nullable
    ExclusiveOptions exclusiveOptions;

    static class ExclusiveOptions {

        @Option(
            names = {"-dc", "--devcontainer"},
            description = "Add an initial Devcontainer support for Java.",
            order = 9)
        boolean devcontainerOption;

        @Option(
            names = {"-m", "--maven"},
            description = "Create a new Maven project.",
            order = 2)
        boolean mavenOption;

        @Option(
            names = {"-sc", "--spring-cli"},
            description = "Create a new Spring Boot project.",
            order = 3)
        boolean springCliOption;

        @Option(
            names = {"-qc", "--quarkus-cli"},
            description = "Create a new Quarkus project.",
            order = 4)
        boolean quarkusCliOption;

        @Option(
            names = {"-c", "--cursor"},
            description = "Add cursor rules for: ${COMPLETION-CANDIDATES}.",
            completionCandidates = CursorOptions.class,
            paramLabel = "<option>",
            order = 1)
        @Nullable
        String cursorOption;

        @Option(
            names = {"-ga", "--github-action"},
            description = "Add an initial GitHub Actions workflow for Maven.",
            order = 8)
        boolean githubActionOption;

        @Option(
            names = {"-ec", "--editorconfig"},
            description = "Add an initial .editorconfig file.",
            order = 6)
        boolean editorConfigOption;

        @Option(
            names = {"-s", "--sdkman"},
            description = "Add an initial .sdkmanrc file.",
            order = 5)
        boolean sdkmanOption;

        @Option(
            names = {"-vv", "--visualvm"},
            description = "Run VisualVM to monitor the application.",
            order = 10)
        boolean visualvmOption;

        @Option(
            names = {"-j", "--jmc"},
            description = "Run JMC to monitor the application.",
            order = 11)
        boolean jmcOption;

        @Option(
            names = {"-gi", "--gitignore"},
            description = "Add an initial .gitignore file.",
            order = 7)
        boolean gitignoreOption;
    }

    // Behavior instances
    private final DevContainer devContainer;
    private final Maven maven;
    private final Cursor cursor;
    private final SpringCli springCli;
    private final QuarkusCli quarkusCli;
    private final GithubAction githubAction;
    private final EditorConfig editorConfig;
    private final Sdkman sdkman;
    private final Visualvm visualvm;
    private final JMC jmc;
    private final Gitignore gitignore;

    public InitCommand() {
        super();
        this.devContainer = new DevContainer();
        this.maven = new Maven();
        this.cursor = new Cursor();
        this.springCli = new SpringCli();
        this.quarkusCli = new QuarkusCli();
        this.githubAction = new GithubAction();
        this.editorConfig = new EditorConfig();
        this.sdkman = new Sdkman();
        this.visualvm = new Visualvm();
        this.jmc = new JMC();
        this.gitignore = new Gitignore();
    }

    public InitCommand(
        DevContainer devContainer,
        Maven maven,
        SpringCli springCli,
        QuarkusCli quarkusCli,
        Cursor cursor,
        GithubAction githubAction,
        EditorConfig editorConfig,
        Sdkman sdkman,
        Visualvm visualvm,
        JMC jmc,
        Gitignore gitignore
    ) {
        super();
        this.devContainer = devContainer;
        this.maven = maven;
        this.springCli = springCli;
        this.quarkusCli = quarkusCli;
        this.cursor = cursor;
        this.githubAction = githubAction;
        this.editorConfig = editorConfig;
        this.sdkman = sdkman;
        this.visualvm = visualvm;
        this.jmc = jmc;
        this.gitignore = gitignore;
    }

    @Override
    public void run() {
        String result = runInitFeature();
        System.out.println(result);
    }

    @SuppressWarnings("NullAway") // CursorOptions.isValidOption handles null internally
    protected String runInitFeature() {
        if (exclusiveOptions == null) {
            return "No feature selected. Use --help to see available options.";
        }

        if (exclusiveOptions.devcontainerOption) {
            Either<String, String> result = devContainer.execute();
            return result.isLeft() ? "Failed to execute devcontainer: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.mavenOption) {
            Either<String, String> result = maven.execute();
            return result.isLeft() ? "Failed to execute maven: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.springCliOption) {
            Either<String, String> result = springCli.execute();
            return result.isLeft() ? "Failed to execute spring-cli: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.quarkusCliOption) {
            Either<String, String> result = quarkusCli.execute();
            return result.isLeft() ? "Failed to execute quarkus-cli: " + result.getLeft() : result.get();
        }

        if (CursorOptions.isValidOption(exclusiveOptions.cursorOption)) {
            Either<String, String> result = cursor.execute(exclusiveOptions.cursorOption);
            return result.isLeft() ? "Failed to execute cursor: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.githubActionOption) {
            Either<String, String> result = githubAction.execute();
            return result.isLeft() ? "Failed to execute github-action: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.editorConfigOption) {
            Either<String, String> result = editorConfig.execute();
            return result.isLeft() ? "Failed to execute editor-config: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.sdkmanOption) {
            Either<String, String> result = sdkman.execute();
            return result.isLeft() ? "Failed to execute sdkman: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.visualvmOption) {
            Either<String, String> result = visualvm.execute();
            return result.isLeft() ? "Failed to execute visualvm: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.jmcOption) {
            Either<String, String> result = jmc.execute();
            return result.isLeft() ? "Failed to execute jmc: " + result.getLeft() : result.get();
        }

        if (exclusiveOptions.gitignoreOption) {
            Either<String, String> result = gitignore.execute();
            return result.isLeft() ? "Failed to execute gitignore: " + result.getLeft() : result.get();
        }

        return "No valid feature option provided.";
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new InitCommand()).execute(args);
        System.exit(exitCode);
    }
}
