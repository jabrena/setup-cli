package info.jab.jbang.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

/**
 * Utility class for executing system commands and checking command availability.
 */
public class CommandExecutor {
    
    private static final CommandExecutor INSTANCE = new CommandExecutor();
    
    public static CommandExecutor getInstance() {
        return INSTANCE;
    }
    
    /**
     * Checks if a command is installed and available in the system path.
     * 
     * @param command the command to check (e.g., "mvn", "git")
     * @return true if the command is installed, false otherwise
     */
    public static boolean isCommandInstalled(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // Set the command based on the OS
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder.command("cmd.exe", "/c", command + " -v");
            } else {
                processBuilder.command("sh", "-c", command + " -v");
            }
            
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
    
    /**
     * Executes a system command using ProcessBuilder.
     * 
     * @param command the command to execute
     * @return the output of the command
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static String executeCommand(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        
        // Set the command based on the OS
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("sh", "-c", command);
        }
        
        // Redirect error stream to output stream
        processBuilder.redirectErrorStream(true);
        
        Process process = processBuilder.start();
        
        // Capture the output of the process
        String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        
        // Wait for the process to finish
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command exited with code: " + exitCode);
        }
        
        return output;
    }
    
    // Instance methods that delegate to static methods for backward compatibility
    
    public boolean checkCommandInstalled(String command) {
        return isCommandInstalled(command);
    }
    
    public String executeCommandInstance(String command) throws IOException, InterruptedException {
        return executeCommand(command);
    }
} 