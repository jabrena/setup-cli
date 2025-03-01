package info.jab.jbang;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
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

    @Option(
        names = {"-d", "--debug"}, 
        description = "Developer feature.")
    private boolean debug = false;
    
    public String runInitFeature() {

        if (debug) {
            System.out.println("devcontainer: " + devcontainer);
            System.out.println("maven: " + maven);
            System.out.println("cursor: " + cursor);
            System.out.println("spring-cli: " + springCli);
            System.out.println("github-action: " + githubAction);
        } else {

            if(cursor.equals("NA") && !maven && !springCli && !devcontainer && !githubAction) {
                return "type 'init --help' to see available options";
            }

            executeDevcontainerFlag();
            executeMavenFlag();
            executeCursorFlag();
            executeSpringCliFlag();
            executeGithubActionFlag();
            return "Command executed successfully";
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
    
    //Load the rules files from the properties file
    private List<String> getProperties() {
        final String rulesProperties = "rules.properties";
        final String keyPrefix = "rules.file.";
        
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(rulesProperties)) {
            properties.load(input);
            return properties.stringPropertyNames().stream()
                    .filter(key -> key.startsWith(keyPrefix))
                    .map(properties::getProperty)
                    .collect(Collectors.toList());//Mutable list
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties", e);
        }
    }

    private void executeCursorFlag() {
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
            ruleFiles.add("301-framework-spring-boot.md");
        }
        //TODO Add Quarkus support in the future (Max`s help)

        if(CursorOptions.OPTIONS.contains(cursor)) {
            CopyRules copyRules = new CopyRules();
            copyRules.copyCursorRulesToDirectory(ruleFiles);
            System.out.println("Cursor rules added successfully");
        }
    }
    
    static class CursorOptions implements Iterable<String> {
        static final List<String> OPTIONS = List.of("java", "java-spring-boot");
        
        @Override
        public Iterator<String> iterator() {
            return OPTIONS.iterator();
        }
    }

    private void executeDevcontainerFlag() {
        if(devcontainer) {
            try {
                Path currentPath = Paths.get(System.getProperty("user.dir"));
                Path devcontainerPath = currentPath.resolve(".devcontainer");
                
                // Delete existing .devcontainer directory contents if it exists
                if (Files.exists(devcontainerPath)) {
                    Files.walkFileTree(devcontainerPath, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                        
                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            if (!dir.equals(devcontainerPath)) {
                                Files.delete(dir);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
                
                // Create .devcontainer directory if it doesn't exist
                Files.createDirectories(devcontainerPath);
                
                // Copy files from resources/devcontainer to .devcontainer
                String resourcePath = "/devcontainer/";
                String[] files = {"Dockerfile", "devcontainer.json"};
                
                for (String fileName : files) {
                    try (InputStream fileIs = getClass().getClassLoader().getResourceAsStream("devcontainer/" + fileName)) {
                        if (fileIs == null) {
                            throw new IOException("File not found: " + fileName);
                        }
                        Files.copy(fileIs, devcontainerPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                
                System.out.println("Devcontainer support added successfully");
            } catch (IOException e) {
                throw new RuntimeException("Error copying devcontainer files", e);
            }
        }
    }

    private void executeMavenFlag() {
        if(maven) {
            System.out.println("");
            System.out.println("sdk install maven");
            System.out.println("mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false");
            System.out.println("mvn wrapper:wrapper");
            System.out.println("./mvnw clean verify");
            System.out.println("");
        }
    }

    private void executeSpringCliFlag() {
        if(springCli) {
            System.out.println("");
            System.out.println("sdk install springboot");
            System.out.println("spring init -d=web,actuator,devtools --build=maven --force ./");
            System.out.println("");
        }
    }

    private void executeGithubActionFlag() {
        if(githubAction) {
            try {
                Path currentPath = Paths.get(System.getProperty("user.dir"));
                Path workflowsPath = currentPath.resolve(".github").resolve("workflows");
                
                // Create .github/workflows directory if it doesn't exist
                Files.createDirectories(workflowsPath);
                
                // Copy maven.yaml from resources to .github/workflows
                try (InputStream fileIs = getClass().getClassLoader().getResourceAsStream("github-action/maven.yaml")) {
                    if (fileIs == null) {
                        throw new IOException("File not found: maven.yaml");
                    }
                    Files.copy(fileIs, workflowsPath.resolve("maven.yaml"), StandardCopyOption.REPLACE_EXISTING);
                }
                
                System.out.println("GitHub Actions workflow added successfully");
            } catch (IOException e) {
                throw new RuntimeException("Error copying GitHub Actions workflow file", e);
            }
        }
    }
} 