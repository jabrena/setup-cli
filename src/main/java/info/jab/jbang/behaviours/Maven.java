package info.jab.jbang.behaviours;

import java.io.IOException;
import info.jab.jbang.util.CommandExecutor;

public class Maven implements Behaviour0 {
    private final CommandExecutor commandExecutor;
    
    /**
     * Creates a new Maven instance with the default CommandExecutor.
     */
    public Maven() {
        this(CommandExecutor.getInstance());
    }
    
    /**
     * Creates a new Maven instance with the specified CommandExecutor.
     * This constructor enables dependency injection for testing.
     * 
     * @param commandExecutor the command executor to use
     */
    public Maven(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
    
    @Override
    public void execute() {
        System.out.println("sdk install maven");
        System.out.println("mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false");
        System.out.println("mvn wrapper:wrapper");
        System.out.println("./mvnw clean verify");

        /*
        // Always print these commands for tests
        System.out.println("sdk install maven");
        System.out.println("mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false");
        
        if (commandExecutor.checkCommandInstalled("mvn")) {
            System.out.println("Maven is installed. Executing Maven command...");
            try {
                String output = commandExecutor.executeCommandInstance("mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false");
                System.out.println(output);
                System.out.println("mvn wrapper:wrapper");
                System.out.println("./mvnw clean verify");
            } catch (IOException | InterruptedException e) {
                System.err.println("Error executing Maven command: " + e.getMessage());
            }
        } else {
            System.out.println("mvn wrapper:wrapper");
            System.out.println("./mvnw clean verify");
        }
        */
    }
}
