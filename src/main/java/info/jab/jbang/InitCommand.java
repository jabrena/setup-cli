package info.jab.jbang;

import info.jab.jbang.behaviours.Cursor;
import info.jab.jbang.behaviours.DevContainer;
import info.jab.jbang.behaviours.GithubAction;
import info.jab.jbang.behaviours.Maven;
import info.jab.jbang.behaviours.QuarkusCli;
import info.jab.jbang.behaviours.SpringCli;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "init", 
    description = "Initialize a new repository with some useful features for Developers.",
    mixinStandardHelpOptions = true
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
    
    private DevContainer devContainer;
    private Maven maven;
    private SpringCli springCli;
    private QuarkusCli quarkusCli;
    private Cursor cursor;
    private GithubAction githubAction;

    public InitCommand() {
        this.devContainer = new DevContainer();
        this.maven = new Maven();
        this.cursor = new Cursor();
        this.springCli = new SpringCli();
        this.quarkusCli = new QuarkusCli();
        this.githubAction = new GithubAction();
    }

    public InitCommand(
        DevContainer devContainer, 
        Maven maven, 
        SpringCli springCli, 
        QuarkusCli quarkusCli,
        Cursor cursor,
        GithubAction githubAction) {
        this.devContainer = devContainer;
        this.maven = maven;
        this.cursor = cursor;
        this.springCli = springCli;
        this.quarkusCli = quarkusCli;
        this.githubAction = githubAction;
    }

    public String runInitFeature() {

        if(cursorOption.equals("NA") && 
            !mavenOption && 
            !springCliOption && 
            !quarkusCliOption && 
            !devcontainerOption && 
            !githubActionOption) {
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