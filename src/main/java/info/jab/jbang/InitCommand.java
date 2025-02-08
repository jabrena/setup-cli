package info.jab.jbang;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "init", 
    description = "Initialize a new repository with some useful features for Developers",
    mixinStandardHelpOptions = true
)
class InitCommand implements Runnable {
    
    @Option(
        names = {"-c", "--cursor"}, 
        description = "Add cursor rules in a new repository (available: java, java-spring-boot)", 
        defaultValue = "java")
    private String cursor = "java";

    @Option(
        names = {"-d", "--debug"}, 
        description = "Developer feature", 
        defaultValue = "false")
    private boolean debug = false;

    private static final String RULES_FILE = "rules.properties";
    private static final String RULES_PREFIX = "rules.file.";
    
    //Load the rules files from the properties file
    private List<String> getProperties() {
        Properties properties = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(RULES_FILE)) {
            properties.load(input);
            return properties.stringPropertyNames().stream()
                    .filter(key -> key.startsWith(RULES_PREFIX))
                    .map(properties::getProperty)
                    .collect(Collectors.toList());//Mutable list
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties", e);
        }
    }

    public String runInitFeature() {

        //Cursor feature
        if (!cursor.equals("java") && !cursor.equals("java-spring-boot")) {
            throw new IllegalArgumentException("Error: Cursor type must be 'java' or 'java-spring-boot'. Use --help to see available options.");
        }

        List<String> ruleFiles = getProperties();
        if(cursor.equals("java-spring-boot")) {
            ruleFiles.add("06-spring-boot.md");
        }
        //TODO Add quarkus support in the future (Max`s help)

        if (!debug) {
            
            CopyRules copyRules = new CopyRules();
            copyRules.copyCursorRulesToDirectory(ruleFiles);
            return "Adding cursor rules";
        }

        return "Debug mode: Skipping file copy";
    }

    @Override
    public void run() {
        System.out.println(runInitFeature());
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new InitCommand()).execute(args);
        System.exit(exitCode);
    }
} 