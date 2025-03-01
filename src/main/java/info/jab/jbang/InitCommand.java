package info.jab.jbang;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import info.jab.jbang.utils.CopyRules;
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
        names = {"-sc", "--spring-cli"}, 
        description = "Show how to use spring cli",
        defaultValue = "false")
    private String springCli = "false";

    @Option(
        names = {"-c", "--cursor"}, 
        description = "Add cursor rules in a new repository (available: ${COMPLETION-CANDIDATES})", 
        completionCandidates = CursorOptions.class)
    private String cursor = "NA";

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

        //Add empty option
        var cursorOptions = new ArrayList<String>();
        cursorOptions.add("NA");
        cursorOptions.addAll(CursorOptions.OPTIONS);
        if (!cursorOptions.contains(cursor)) {
            throw new IllegalArgumentException("Invalid cursor option: " + cursor + ". Available options: " + String.join(", ", CursorOptions.OPTIONS));
        }

        List<String> ruleFiles = getProperties();
        //Alpha support.
        if(cursor.equals("java-spring-boot")) {
            ruleFiles.add("06-spring-boot.md");
        }
        //TODO Add quarkus support in the future (Max`s help)

        if (!debug) {
            if(cursor.equals("NA") && springCli.equals("false")) {
                return "type 'init --help' to see available options";
            }

            if(CursorOptions.OPTIONS.contains(cursor)) {
                CopyRules copyRules = new CopyRules();
                copyRules.copyCursorRulesToDirectory(ruleFiles);
                System.out.println("Cursor rules added successfully");
            }

            if(springCli.equals("true")) {
                System.out.println("");
                System.out.println("sdk install springboot");
                System.out.println("spring init -d=web,actuator,devtools --build=maven --force ./");
                System.out.println("");
            }

            return "Command executed successfully";
        } else {
            //Options
            System.out.println("spring-cli: " + springCli);
            System.out.println("cursor: " + cursor);    
        }

        return "Debug mode: Skipping file copy";
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
    
    static class CursorOptions implements Iterable<String> {
        static final List<String> OPTIONS = List.of("java", "java-spring-boot");
        
        @Override
        public Iterator<String> iterator() {
            return OPTIONS.iterator();
        }
    }
} 