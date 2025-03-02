package info.jab.jbang;

import info.jab.jbang.behaviours.Cursor;
import info.jab.jbang.behaviours.DevContainer;
import info.jab.jbang.behaviours.GithubAction;
import info.jab.jbang.behaviours.Maven;
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
    private boolean devcontainer = false;

    @Option(
        names = {"-c", "--cursor"}, 
        description = "Add cursor rules for: ${COMPLETION-CANDIDATES}.", 
        completionCandidates = CursorOptions.class)
    private String cursor = "NA";

    @Option(
        names = {"-m", "--maven"}, 
        description = "Show how to use Maven to create a new project.")
    private boolean maven = false;

    @Option(
        names = {"-sc", "--spring-cli"}, 
        description = "Show how to use Spring CLI to create a new project.")
    private boolean springCli = false;

    @Option(
        names = {"-ga", "--github-action"}, 
        description = "Add an initial GitHub Actions workflow for Maven.")
    private boolean githubAction = false;
    
    public String runInitFeature() {

        if(cursor.equals("NA") && !maven && !springCli && !devcontainer && !githubAction) {
            return "type 'init --help' to see available options";
        }

        if(devcontainer) {
            new DevContainer().execute();
        }
        if(maven) {
            new Maven().execute();
        }
        if(springCli) {
            new SpringCli().execute();
        }
        if(CursorOptions.isValidOption(cursor)) {
            new Cursor().execute(cursor);
        }
        if(githubAction) {
            new GithubAction().execute();
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