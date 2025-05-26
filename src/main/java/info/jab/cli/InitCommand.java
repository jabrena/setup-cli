package info.jab.cli;

import org.jspecify.annotations.NonNull;

import info.jab.cli.behaviours.Cursor;
import info.jab.cli.behaviours.DevContainer;
import info.jab.cli.behaviours.EditorConfig;
import info.jab.cli.behaviours.GithubAction;
import info.jab.cli.behaviours.JMC;
import info.jab.cli.behaviours.Maven;
import info.jab.cli.behaviours.QuarkusCli;
import info.jab.cli.behaviours.Sdkman;
import info.jab.cli.behaviours.SpringCli;
import info.jab.cli.behaviours.Visualvm;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "init",
    description = "Initialize a new repository with some useful features for Developers.",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true
)
public class InitCommand implements Runnable {

    @Option(
        names = {"-dc", "--devcontainer"},
        description = "Add Devcontainer support for Java.")
    private boolean devcontainerOption = false;

    @Option(
        names = {"-c", "--cursor"},
        description = "Add cursor rules for: ${COMPLETION-CANDIDATES}.",
        completionCandidates = CursorOptions.class)
    private String cursorOption = "NA";

    @Option(
        names = {"-m", "--maven"},
        description = "Show how to use Maven to create a new project.")
    private boolean mavenOption = false;

    @Option(
        names = {"-sc", "--spring-cli"},
        description = "Show how to use Spring CLI to create a new project.")
    private boolean springCliOption = false;

    @Option(
        names = {"-qc", "--quarkus-cli"},
        description = "Show how to use Quarkus CLI to create a new project.")
    private boolean quarkusCliOption = false;

    @Option(
        names = {"-ga", "--github-action"},
        description = "Add an initial GitHub Actions workflow for Maven.")
    private boolean githubActionOption = false;

    @Option(
        names = {"-ec", "--editorconfig"},
        description = "Add an initial EditorConfig file.")
    private boolean editorConfigOption = false;

    @Option(
        names = {"-s", "--sdkman"},
        description = "Add an initial SDKMAN Init file.")
    private boolean sdkmanOption = false;

    @Option(
        names = {"-vv", "--visualvm"},
        description = "Run VisualVM to monitor the application.")
    private boolean visualvmOption = false;

    @Option(
        names = {"-j", "--jmc"},
        description = "Run JMC to monitor the application.",
        hidden = true)
    private boolean jmcOption = false;

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
        @NonNull JMC jmc) {
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
    }

    public String runInitFeature() {

        if(!CursorOptions.isValidOption(cursorOption) &&
            !mavenOption &&
            !springCliOption &&
            !quarkusCliOption &&
            !devcontainerOption &&
            !githubActionOption &&
            !editorConfigOption &&
            !sdkmanOption &&
            !visualvmOption &&
            !jmcOption) {
            return "type 'init --help' to see available options";
        }

        if(devcontainerOption) {
            devContainer.execute();
        }
        if(mavenOption) {
            maven.execute();
        }
        if(springCliOption) {
            springCli.execute();
        }
        if(quarkusCliOption) {
            quarkusCli.execute();
        }
        if(CursorOptions.isValidOption(cursorOption)) {
            cursor.execute(cursorOption);
        }
        if(githubActionOption) {
            githubAction.execute();
        }
        if(editorConfigOption) {
            editorConfig.execute();
        }
        if(sdkmanOption) {
            sdkman.execute();
        }
        if(visualvmOption) {
            visualvm.execute();
        }
        if(jmcOption) {
            jmc.execute();
        }
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