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
import java.util.Objects;

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
            names = {"-c", "--cursor"},
            description = "Add cursor rules for: ${COMPLETION-CANDIDATES}.",
            completionCandidates = CursorOptions.class,
            paramLabel = "<option>",
            order = 1)
        @Nullable
        String cursorOption;

        @Option(
            names = {"-m", "--maven"},
            description = "Create a new Maven project.",
            order = 2)
        boolean mavenOption;

        @Option(
            names = {"-sb", "--spring-boot"},
            description = "Create a new Spring Boot project.",
            order = 3)
        boolean springCliOption;

        @Option(
            names = {"-q", "--quarkus"},
            description = "Create a new Quarkus project.",
            order = 4)
        boolean quarkusCliOption;

        @Option(
            names = {"-s", "--sdkman"},
            description = "Add an initial .sdkmanrc file.",
            order = 5)
        boolean sdkmanOption;

        @Option(
            names = {"-ec", "--editorconfig"},
            description = "Add an initial .editorconfig file.",
            order = 6)
        boolean editorConfigOption;

        @Option(
            names = {"-gi", "--gitignore"},
            description = "Add an initial .gitignore file.",
            order = 7)
        boolean gitignoreOption;

        @Option(
            names = {"-ga", "--github-action"},
            description = "Add an initial GitHub Actions workflow for Maven.",
            order = 8)
        boolean githubActionOption;

        @Option(
            names = {"-dc", "--devcontainer"},
            description = "Add an initial Devcontainer support for Java.",
            order = 9)
        boolean devcontainerOption;

        @Option(
            names = {"-vv", "--visualvm"},
            description = "Run VisualVM to monitor the application.",
            order = 10,
            hidden = true)
        boolean visualvmOption;

        @Option(
            names = {"-j", "--jmc"},
            description = "Run JMC to monitor the application.",
            order = 11,
            hidden = true)
        boolean jmcOption;
    }

    // Behavior instances
    private final Cursor cursor;
    private final Maven maven;
    private final SpringCli springCli;
    private final QuarkusCli quarkusCli;
    private final GithubAction githubAction;
    private final Sdkman sdkman;
    private final EditorConfig editorConfig;
    private final Gitignore gitignore;
    private final DevContainer devContainer;
    private final Visualvm visualvm;
    private final JMC jmc;

    public InitCommand() {
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
        runInitFeature();
    }

    @SuppressWarnings("NullAway") // CursorOptions.isValidOption handles null internally
    protected Integer runInitFeature() {
        if (Objects.isNull(exclusiveOptions)) {
            return processResult(Either.left("No feature selected. Use --help to see available options."));
        }

        if (exclusiveOptions.devcontainerOption) {
            return processResult(devContainer.execute());
        }

        if (exclusiveOptions.mavenOption) {
            return processResult(maven.execute());
        }

        if (exclusiveOptions.springCliOption) {
            return processResult(springCli.execute());
        }

        if (exclusiveOptions.quarkusCliOption) {
            return processResult(quarkusCli.execute());
        }

        if (CursorOptions.isValidOption(exclusiveOptions.cursorOption)) {
            return processResult(cursor.execute(exclusiveOptions.cursorOption));
        }

        if (exclusiveOptions.githubActionOption) {
            return processResult(githubAction.execute());
        }

        if (exclusiveOptions.editorConfigOption) {
            return processResult(editorConfig.execute());
        }

        if (exclusiveOptions.sdkmanOption) {
            return processResult(sdkman.execute());
        }

        if (exclusiveOptions.visualvmOption) {
            return processResult(visualvm.execute());
        }

        if (exclusiveOptions.jmcOption) {
            return processResult(jmc.execute());
        }

        if (exclusiveOptions.gitignoreOption) {
            return processResult(gitignore.execute());
        }

        return processResult(Either.left("No valid feature option provided."));
    }

    private Integer processResult(Either<String, String> result) {
        return result.fold(
            error -> { System.out.println(error); return 1; },
            success -> { System.out.println(success); return 0; }
        );
    }
}
