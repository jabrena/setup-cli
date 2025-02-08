package info.jab.jbang;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "setup",
    subcommands = {InitCommand.class},
    description = "Setup application"
)
public class Setup implements Runnable {
    
    @Override
    public void run() {
        System.out.println("Please specify a subcommand. Use --help to see available options.");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Setup()).execute(args);
        System.exit(exitCode);
    }
}