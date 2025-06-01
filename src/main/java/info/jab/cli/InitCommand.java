package info.jab.cli;

import info.jab.cli.behaviours.Cursor;
import info.jab.cli.behaviours.DevContainer;
import info.jab.cli.behaviours.EditorConfig;
import info.jab.cli.behaviours.GithubAction;
import info.jab.cli.behaviours.Gitignore;
import info.jab.cli.behaviours.Maven;
import info.jab.cli.behaviours.Sdkman;
import info.jab.cli.io.CommandExecutor;
import info.jab.cli.io.FileSystemChecker;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Command(
    name = "init",
    description = "Initialize a new project with various configurations",
    mixinStandardHelpOptions = true
)
public class InitCommand implements Callable<Integer>, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(InitCommand.class);

    @Option(
        names = {"-m", "--maven"},
        description = "Initialize a Maven project"
    )
    private boolean maven = false;

    @Option(
        names = {"-g", "--gitignore"},
        description = "Add a .gitignore file"
    )
    private boolean gitignore = false;

    @Option(
        names = {"-e", "--editorconfig"},
        description = "Add an .editorconfig file"
    )
    private boolean editorconfig = false;

    @Option(
        names = {"-c", "--cursor"},
        description = "Add cursor rules for: ${COMPLETION-CANDIDATES}.",
        completionCandidates = CursorOptions.class,
        arity = "1"
    )
    private String cursorOption = "NA";

    @Option(
        names = {"-d", "--devcontainer"},
        description = "Add a .devcontainer configuration"
    )
    private boolean devcontainer = false;

    @Option(
        names = {"-a", "--github-action"},
        description = "Add a GitHub Action workflow"
    )
    private boolean githubAction = false;

    @Option(
        names = {"-s", "--sdkman"},
        description = "Add SDKMAN configuration"
    )
    private boolean sdkman = false;

    @Option(
        names = {"--all"},
        description = "Initialize with all available configurations"
    )
    private boolean all = false;

    // Dependencies for testing
    private final Maven mavenBehaviour;
    private final Gitignore gitignoreBehaviour;
    private final EditorConfig editorConfigBehaviour;
    private final DevContainer devContainerBehaviour;
    private final GithubAction githubActionBehaviour;
    private final Sdkman sdkmanBehaviour;
    private final Cursor cursor;

    // Default constructor for production use
    public InitCommand() {
        CommandExecutor commandExecutor = new CommandExecutor();
        FileSystemChecker fileSystemChecker = new FileSystemChecker();

        this.mavenBehaviour = new Maven(commandExecutor, fileSystemChecker);
        this.gitignoreBehaviour = new Gitignore();
        this.editorConfigBehaviour = new EditorConfig();
        this.devContainerBehaviour = new DevContainer();
        this.githubActionBehaviour = new GithubAction();
        this.sdkmanBehaviour = new Sdkman();
        this.cursor = new Cursor();
    }

    // Constructor for dependency injection (testing)
    public InitCommand(
        @NonNull Maven mavenBehaviour,
        @NonNull Gitignore gitignoreBehaviour,
        @NonNull EditorConfig editorConfigBehaviour,
        @NonNull DevContainer devContainerBehaviour,
        @NonNull GithubAction githubActionBehaviour,
        @NonNull Sdkman sdkmanBehaviour,
        @NonNull Cursor cursor,
        @NonNull CommandExecutor commandExecutor,
        @NonNull FileSystemChecker fileSystemChecker
    ) {
        this.mavenBehaviour = mavenBehaviour;
        this.gitignoreBehaviour = gitignoreBehaviour;
        this.editorConfigBehaviour = editorConfigBehaviour;
        this.devContainerBehaviour = devContainerBehaviour;
        this.githubActionBehaviour = githubActionBehaviour;
        this.sdkmanBehaviour = sdkmanBehaviour;
        this.cursor = cursor;
    }

    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            logger.error("Error during execution: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public String runInitFeature() {
        try {
            List<FeatureConfig> features = buildFeatureList();

            if (features.isEmpty()) {
                return "type 'init --help' to see available options";
            }

            executeFeatures(features);
            return "Command executed successfully";
        } catch (Exception e) {
            logger.error("Feature execution failed: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to execute features", e);
        }
    }

    @Override
    public Integer call() throws Exception {
        logger.info("Starting project initialization...");

        try {
            List<FeatureConfig> features = buildFeatureList();

            if (features.isEmpty()) {
                logger.warn("No features selected for initialization");
                System.out.println("type 'init --help' to see available options");
                return 1;
            }

            executeFeatures(features);

            logger.info("Project initialization completed successfully");
            System.out.println("Command executed successfully");
            return 0;

        } catch (Exception e) {
            logger.error("Project initialization failed: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    private List<FeatureConfig> buildFeatureList() {
        List<FeatureConfig> features = new ArrayList<>();

        // Handle cursor option with its special parameter requirement
        Optional<FeatureConfig> cursorFeature = CursorOptions.isValidOption(cursorOption)
            ? Optional.of(new FeatureConfig("cursor", () -> true, () -> cursor.execute(cursorOption)))
            : Optional.empty();

        // Add all selected features
        List<FeatureConfig> allFeatures = List.of(
            new FeatureConfig("maven", () -> maven || all, mavenBehaviour::execute),
            new FeatureConfig("gitignore", () -> gitignore || all, gitignoreBehaviour::execute),
            new FeatureConfig("editorconfig", () -> editorconfig || all, editorConfigBehaviour::execute),
            new FeatureConfig("devcontainer", () -> devcontainer || all, devContainerBehaviour::execute),
            new FeatureConfig("github-action", () -> githubAction || all, githubActionBehaviour::execute),
            new FeatureConfig("sdkman", () -> sdkman || all, sdkmanBehaviour::execute)
        );

        // Add cursor feature if valid
        cursorFeature.stream()
            .forEach(features::add);

        // Add other features based on flags
        allFeatures.stream()
            .filter(feature -> feature.condition().get())
            .forEach(features::add);

        return features;
    }

    private void executeFeatures(List<FeatureConfig> features) {
        for (FeatureConfig feature : features) {
            try {
                logger.info("Executing feature: {}", feature.name());
                feature.action().run();
                logger.info("Feature '{}' completed successfully", feature.name());
            } catch (Exception e) {
                logger.error("Feature '{}' failed: {}", feature.name(), e.getMessage(), e);
                throw new RuntimeException("Failed to execute feature: " + feature.name(), e);
            }
        }
    }

    private record FeatureConfig(
        String name,
        java.util.function.Supplier<Boolean> condition,
        Runnable action
    ) {}
}
