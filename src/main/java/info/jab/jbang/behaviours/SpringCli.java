package info.jab.jbang.behaviours;

import java.io.IOException;
import info.jab.jbang.util.CommandExecutor;

public class SpringCli implements Behaviour0 {
    private final CommandExecutor commandExecutor;
    
    /**
     * Creates a new SpringCli instance with the default CommandExecutor.
     */
    public SpringCli() {
        this(CommandExecutor.getInstance());
    }
    
    /**
     * Creates a new SpringCli instance with the specified CommandExecutor.
     * This constructor enables dependency injection for testing.
     * 
     * @param commandExecutor the command executor to use
     */
    public SpringCli(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }
    
    @Override
    public void execute() {
        System.out.println("sdk install springboot");
        System.out.println("spring init -d=web,actuator,devtools --build=maven --force ./");
        System.out.println("./mvnw clean verify");
        /*
        // Check if Spring CLI is installed
        if (commandExecutor.checkCommandInstalled("spring")) {
            System.out.println("Spring CLI is installed. Executing Spring command...");
            try {
                // Generate Spring Boot project
                String output = commandExecutor.executeCommandInstance("spring init -d=web,actuator,devtools --build=maven --force ./");
                System.out.println(output);
                System.out.println("You can run: ./mvnw clean verify");
            } catch (IOException | InterruptedException e) {
                System.err.println("Error executing Spring command: " + e.getMessage());
            }
        } else {
            // If Spring CLI is not installed, just print the commands
            System.out.println("sdk install springboot");
            System.out.println("spring init -d=web,actuator,devtools --build=maven --force ./");
            System.out.println("./mvnw clean verify");
        }
        */
    }
}
