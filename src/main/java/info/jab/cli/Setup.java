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
        // Execute the feature, ignore return value as Runnable.run is void
        initCommand.runInitFeature();
    }

    private static void printBanner() {
        try {
            System.out.println();
            String asciiArt = FigletFont.convertOneLine("Setup CLI");
            System.out.println(colorize(asciiArt, Attribute.GREEN_TEXT()));
            GitInfoPrinter.printGitInfo();
        } catch (IOException e) {
            System.out.println("Error printing banner: " + e.getMessage());
        }
    }

    // Refactored method to contain the core CLI logic for easier testing
    public static int runCLI(String[] args) {
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