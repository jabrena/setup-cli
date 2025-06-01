package info.jab.cli;

import java.io.IOException;

import org.jspecify.annotations.NonNull;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import com.diogonunes.jcolor.Attribute;
import static com.diogonunes.jcolor.Ansi.colorize;

import com.github.lalyos.jfiglet.FigletFont;

@Command(
    name = "setup",
    subcommands = {InitCommand.class},
    description = "Setup is a CLI utility designed to help developers when they start working with a new repository.",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true
)
public class Setup implements Runnable {

    // Field for dependency injection in tests
    private final InitCommand initCommand;

    // Constructor that accepts an InitCommand (for testing)
    public Setup(@NonNull InitCommand initCommand) {
        this.initCommand = initCommand;
    }

    // Default constructor (used in production)
    public Setup() {
        this.initCommand = new InitCommand();
    }

    @Override
    public void run() {
        try {
            // Execute the InitCommand and handle the result
            Integer result = initCommand.call();
            if (result != 0) {
                System.exit(result);
            }
        } catch (Exception e) {
            System.err.println("Error executing init command: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printBanner() {
        try {
            System.out.println();
            String asciiArt = FigletFont.convertOneLine("Setup CLI");
            System.out.println(colorize(asciiArt, Attribute.GREEN_TEXT()));
            new GitInfoPrinter().printGitInfo();
        } catch (IOException e) {
            System.out.println("Error printing banner: " + e.getMessage());
        }
    }

    // Refactored method to contain the core CLI logic for easier testing
    protected static int runCLI(String[] args) {
        if(args.length == 0) {
            System.out.println("Please specify a command. Use --help to see available options.");
            return 0; // Return 0 as per original logic
        }
        printBanner();
        return new CommandLine(new Setup()).execute(args);
    }

    public static void main(String[] args) {
        int exitCode = runCLI(args);
        System.exit(exitCode);
    }
}
