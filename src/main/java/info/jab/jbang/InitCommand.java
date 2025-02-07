package info.jab.jbang;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;

@Command(name = "init", description = "Greets the user")
public class InitCommand implements Runnable {
    
    @Option(names = {"-c", "--cursor"}, description = "Cursor type (java)", defaultValue = "java")
    private String cursor = "java";

    @Option(names = {"-d", "--debug"}, description = "Skip copying files", defaultValue = "false")
    private boolean debug = false;

    public String sayHello() {
        if (!cursor.equals("java")) {
            throw new IllegalArgumentException("Error: Cursor type must be 'java'. Use --help to see available options.");
        }

        try {
            if (!debug) {
                CopyRules copyRules = new CopyRules();
                copyRules.copyRulesToDirectory();
                return "Adding cursor rules";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Debug mode: Skipping file copy";
    }

    @Override
    public void run() {
        System.out.println(sayHello());
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new InitCommand()).execute(args);
        System.exit(exitCode);
    }
} 