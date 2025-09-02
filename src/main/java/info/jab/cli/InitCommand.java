package info.jab.cli;

import info.jab.cli.behaviours.Cursor;
import info.jab.cli.behaviours.Dependabot;
import info.jab.cli.behaviours.DevContainer;
import info.jab.cli.behaviours.EditorConfig;
import info.jab.cli.behaviours.GithubAction;
import info.jab.cli.behaviours.Gitignore;
import info.jab.cli.behaviours.Gradle;
import info.jab.cli.behaviours.JMC;
import info.jab.cli.behaviours.Maven;
import info.jab.cli.behaviours.QuarkusCli;
import info.jab.cli.behaviours.Sdkman;
import info.jab.cli.behaviours.SpringCli;
import info.jab.cli.behaviours.Visualvm;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ArgGroup;
import io.vavr.control.Either;
import org.jspecify.annotations.Nullable;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.util.Objects;

/**
 * CLI command for initializing project setup features.
 * This command supports various development tools and configurations.
 * Only one feature can be executed at a time to ensure proper initialization.
 */
@Command(
    name = "init",
    description = "Setup is a command line utility designed to help developers when initializing new projects using Maven.",
    mixinStandardHelpOptions = true,
    sortOptions = false,
    usageHelpAutoWidth = true
)
public class InitCommand implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(InitCommand.class);

    // Mutually exclusive options - only one can be selected at a time
    @ArgGroup(exclusive = true, multiplicity = "1")
    @Nullable
    ExclusiveOptions exclusiveOptions;

    static class ExclusiveOptions {

        @Option(
            names = {"-m", "--maven"},
            description = "Create a new Maven project.",
            order = 2)
        boolean mavenOption;

        @Option(
            names = {"-g", "--gradle"},
            description = "Create a new Gradle project.",
            order = 3)
        boolean gradleOption;

        @Option(
            names = {"-sb", "--spring-boot"},
            description = "Create a new Spring Boot project.",
            order = 4)
        boolean springCliOption;

        @Option(
            names = {"-q", "--quarkus"},
            description = "Create a new Quarkus project.",
            order = 5)
        boolean quarkusCliOption;

        @Option(
            names = {"-c", "--cursor"},
            description = "Download cursor rules from a Git repository. " +
                         "The option accepts 2 parameters, the first parameter requires a Https Git repository URL, " +
                         "the second parameter is optional and indicates the path where is located in the repository " +
                         "the cursor rules, by default ./cursor/rules.",
            arity = "1..2",
            order = 1)
        @Nullable
        @SuppressWarnings("NullAway") // Optional CLI parameter can be null
        String[] cursorParameters;

        @Option(
            names = {"-s", "--sdkman"},
            description = "Add an initial .sdkmanrc file.",
            order = 6)
        boolean sdkmanOption;

        @Option(
            names = {"-ec", "--editorconfig"},
            description = "Add an initial .editorconfig file.",
            order = 7)
        boolean editorConfigOption;

        @Option(
            names = {"-gi", "--gitignore"},
            description = "Add an initial .gitignore file.",
            order = 8)
        boolean gitignoreOption;

        @Option(
            names = {"-ga", "--github-action"},
            description = "Add an initial GitHub Actions workflow for Maven.",
            order = 9)
        boolean githubActionOption;

        @Option(
            names = {"-db", "--dependabot"},
            description = "Add an initial Dependabot configuration.",
            order = 10)
        boolean dependabotOption;

        @Option(
            names = {"-dc", "--devcontainer"},
            description = "Add an initial Devcontainer support for Java.",
            order = 11)
        boolean devcontainerOption;

        @Option(
            names = {"-vv", "--visualvm"},
            description = "Run VisualVM to monitor the application.",
            order = 12,
            hidden = true)
        boolean visualvmOption;

        @Option(
            names = {"-j", "--jmc"},
            description = "Run JMC to monitor the application.",
            order = 13,
            hidden = true)
        boolean jmcOption;
    }

    // Behavior instances
    private final Maven maven;
    private final Gradle gradle;
    private final SpringCli springCli;
    private final QuarkusCli quarkusCli;
    private final Cursor cursor;
    private final Sdkman sdkman;
    private final EditorConfig editorConfig;
    private final Gitignore gitignore;
    private final GithubAction githubAction;
    private final Dependabot dependabot;
    private final DevContainer devContainer;
    private final Visualvm visualvm;
    private final JMC jmc;

    public InitCommand() {
        this.maven = new Maven();
        this.gradle = new Gradle();
        this.springCli = new SpringCli();
        this.quarkusCli = new QuarkusCli();
        this.cursor = new Cursor();
        this.sdkman = new Sdkman();
        this.editorConfig = new EditorConfig();
        this.gitignore = new Gitignore();
        this.githubAction = new GithubAction();
        this.dependabot = new Dependabot();
        this.devContainer = new DevContainer();
        this.visualvm = new Visualvm();
        this.jmc = new JMC();
    }

    public InitCommand(
        Maven maven,
        Gradle gradle,
        SpringCli springCli,
        QuarkusCli quarkusCli,
        Cursor cursor,
        EditorConfig editorConfig,
        Sdkman sdkman,
        GithubAction githubAction,
        Gitignore gitignore,
        Dependabot dependabot,
        DevContainer devContainer,
        Visualvm visualvm,
        JMC jmc
    ) {
        this.maven = maven;
        this.gradle = gradle;
        this.springCli = springCli;
        this.quarkusCli = quarkusCli;
        this.cursor = cursor;
        this.editorConfig = editorConfig;
        this.sdkman = sdkman;
        this.gitignore = gitignore;
        this.githubAction = githubAction;
        this.dependabot = dependabot;
        this.devContainer = devContainer;
        this.visualvm = visualvm;
        this.jmc = jmc;
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

        if (exclusiveOptions.mavenOption) {
            return processResult(maven.execute());
        }

        if (exclusiveOptions.gradleOption) {
            return processResult(gradle.execute());
        }

        if (exclusiveOptions.springCliOption) {
            return processResult(springCli.execute());
        }

        if (exclusiveOptions.quarkusCliOption) {
            return processResult(quarkusCli.execute());
        }

        if (Objects.nonNull(exclusiveOptions.cursorParameters) && exclusiveOptions.cursorParameters.length > 0) {
            if (exclusiveOptions.cursorParameters.length == 1) {
                String gitRepoUrl = exclusiveOptions.cursorParameters[0];
                String destinationPath = ".cursor/rules";
                return processResult(cursor.execute(gitRepoUrl, destinationPath));
            } else {
                String gitRepoUrl = exclusiveOptions.cursorParameters[0];
                String destinationPath = exclusiveOptions.cursorParameters[1];
                return processResult(cursor.execute(gitRepoUrl, destinationPath));
            }
        }

        if (exclusiveOptions.editorConfigOption) {
            return processResult(editorConfig.execute());
        }

        if (exclusiveOptions.sdkmanOption) {
            return processResult(sdkman.execute());
        }

        if (exclusiveOptions.githubActionOption) {
            return processResult(githubAction.execute());
        }

        if (exclusiveOptions.gitignoreOption) {
            return processResult(gitignore.execute());
        }

        if (exclusiveOptions.devcontainerOption) {
            return processResult(devContainer.execute());
        }

        if (exclusiveOptions.dependabotOption) {
            return processResult(dependabot.execute());
        }

        if (exclusiveOptions.visualvmOption) {
            return processResult(visualvm.execute());
        }

        if (exclusiveOptions.jmcOption) {
            return processResult(jmc.execute());
        }

        return processResult(Either.left("No valid feature option provided."));
    }

    private Integer processResult(Either<String, String> result) {
        return result.fold(
            error -> { logger.error(error); return 1; },
            success -> { logger.info(success); return 0; }
        );
    }
}
