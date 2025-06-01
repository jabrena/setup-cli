package info.jab.cli;

import org.jspecify.annotations.NonNull;

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

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Command(
    name = "init",
    description = "Initialize a new repository with some useful features for Developers.",
    mixinStandardHelpOptions = true,
    sortOptions = false,
    usageHelpAutoWidth = true
)
public class InitCommand implements Runnable {

    @SuppressWarnings("UnusedVariable")
    @Option(names = "--version", versionHelp = true, order = 99)
    private boolean version; // Always second

    @SuppressWarnings("UnusedVariable")
    @Option(names = "--help", usageHelp = true, order = 100)
    private boolean help; // Always first

    @Option(
        names = {"-dc", "--devcontainer"},
        description = "Add Devcontainer support for Java.",
        order = 9)
    private boolean devcontainerOption = false;

    @Option(
        names = {"-c", "--cursor"},
        description = "Add cursor rules for: ${COMPLETION-CANDIDATES}.",
        completionCandidates = CursorOptions.class,
        order = 1)
    private String cursorOption = "NA";

    @Option(
        names = {"-m", "--maven"},
        description = "Show how to use Maven to create a new project.",
        order = 2)
    private boolean mavenOption = false;

    @Option(
        names = {"-sc", "--spring-cli"},
        description = "Show how to use Spring CLI to create a new project.",
        order = 3)
    private boolean springCliOption = false;

    @Option(
        names = {"-qc", "--quarkus-cli"},
        description = "Show how to use Quarkus CLI to create a new project.",
        order = 4)
    private boolean quarkusCliOption = false;

    @Option(
        names = {"-ga", "--github-action"},
        description = "Add an initial GitHub Actions workflow for Maven.",
        order = 8)
    private boolean githubActionOption = false;

    @Option(
        names = {"-ec", "--editorconfig"},
        description = "Add an initial EditorConfig file.",
        order = 6)
    private boolean editorConfigOption = false;

    @Option(
        names = {"-s", "--sdkman"},
        description = "Add an initial SDKMAN Init file.",
        order = 5)
    private boolean sdkmanOption = false;

    @Option(
        names = {"-vv", "--visualvm"},
        description = "Run VisualVM to monitor the application.",
        order = 10)
    private boolean visualvmOption = false;

    @Option(
        names = {"-j", "--jmc"},
        description = "Run JMC to monitor the application.",
        order = 11)
    private boolean jmcOption = false;

    @Option(
        names = {"-gi", "--gitignore"},
        description = "Add an initial .gitignore file.",
        order = 7)
    private boolean gitignoreOption = false;

    private final DevContainer devContainer;
    private final Maven maven;
    private final SpringCli springCli;
    private final QuarkusCli quarkusCli;
    private final Cursor cursor;
    private final GithubAction githubAction;
    private final EditorConfig editorConfig;
    private final Sdkman sdkman;
    private final Visualvm visualvm;
    private final JMC jmc;
    private final Gitignore gitignore;

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
        @NonNull DevContainer devContainer,
        @NonNull Maven maven,
        @NonNull SpringCli springCli,
        @NonNull QuarkusCli quarkusCli,
        @NonNull Cursor cursor,
        @NonNull GithubAction githubAction,
        @NonNull EditorConfig editorConfig,
        @NonNull Sdkman sdkman,
        @NonNull Visualvm visualvm,
        @NonNull JMC jmc,
        @NonNull Gitignore gitignore) {
        this.devContainer = devContainer;
        this.maven = maven;
        this.cursor = cursor;
        this.springCli = springCli;
        this.quarkusCli = quarkusCli;
        this.githubAction = githubAction;
        this.editorConfig = editorConfig;
        this.sdkman = sdkman;
        this.visualvm = visualvm;
        this.jmc = jmc;
        this.gitignore = gitignore;
    }

    // Functional approach using records for better type safety and extensibility
    private record FeatureConfig(String name, Supplier<Boolean> isEnabled, Runnable action) {}

    protected String runInitFeature() {
        // Define all features in a declarative way
        List<FeatureConfig> features = List.of(
            new FeatureConfig("devcontainer", () -> devcontainerOption, devContainer::execute),
            new FeatureConfig("maven", () -> mavenOption, maven::execute),
            new FeatureConfig("spring-cli", () -> springCliOption, springCli::execute),
            new FeatureConfig("quarkus-cli", () -> quarkusCliOption, quarkusCli::execute),
            new FeatureConfig("github-action", () -> githubActionOption, githubAction::execute),
            new FeatureConfig("editor-config", () -> editorConfigOption, editorConfig::execute),
            new FeatureConfig("sdkman", () -> sdkmanOption, sdkman::execute),
            new FeatureConfig("visualvm", () -> visualvmOption, visualvm::execute),
            new FeatureConfig("jmc", () -> jmcOption, jmc::execute),
            new FeatureConfig("gitignore", () -> gitignoreOption, gitignore::execute)
        );

        // Handle cursor option with its special parameter requirement
        Optional<FeatureConfig> cursorFeature = CursorOptions.isValidOption(cursorOption)
            ? Optional.of(new FeatureConfig("cursor", () -> true, () -> cursor.execute(cursorOption)))
            : Optional.empty();

        // Combine all features
        Stream<FeatureConfig> allFeatures = Stream.concat(
            features.stream(),
            cursorFeature.stream()
        );

        // Find enabled features
        List<FeatureConfig> enabledFeatures = allFeatures
            .filter(feature -> feature.isEnabled().get())
            .toList();

        // Check if any features are enabled
        if (enabledFeatures.isEmpty()) {
            return "type 'init --help' to see available options";
        }

        // Execute all enabled features
        enabledFeatures.forEach(feature -> {
            feature.action().run();
        });

        return "Command executed successfully";
    }

    @Override
    public void run() {
        String result = runInitFeature();
        System.out.println(result);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new InitCommand()).execute(args);
        System.exit(exitCode);
    }
}
