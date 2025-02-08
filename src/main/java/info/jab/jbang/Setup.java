package info.jab.jbang;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "setup",
    subcommands = {InitCommand.class},
    description = "Setup CLI to help developers when they want to begin a new repository",
    mixinStandardHelpOptions = true
)
public class Setup implements Runnable {
    
    @Override
    public void run() { }

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("Please specify a subcommand. Use --help to see available options.");
            System.exit(0);
        }
        int exitCode = new CommandLine(new Setup()).execute(args);
        System.exit(exitCode);
    }
}