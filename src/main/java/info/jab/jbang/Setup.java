package info.jab.jbang;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "setup",
    subcommands = {InitCommand.class},
    description = "Setup is a CLI utility designed to help developers when they start working with a new repository.",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true
)
public class Setup implements Runnable {
    
    // Field for dependency injection in tests
    private InitCommand initCommand;
    
    // Constructor that accepts an InitCommand (for testing)
    public Setup(InitCommand initCommand) {
        this.initCommand = initCommand;
    }
    
    // Default constructor (used in production)
    public Setup() {
        this.initCommand = new InitCommand();
    }
    
    @Override
    public void run() {
        System.out.println("Setup is a CLI utility designed to help developers when they start working with a new repository.");
        initCommand.runInitFeature();
    }

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Please specify a command. Use --help to see available options.");
            System.exit(0);
        }
        int exitCode = new CommandLine(new Setup()).execute(args);
        System.exit(exitCode);
    }
}