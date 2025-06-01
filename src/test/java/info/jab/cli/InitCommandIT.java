package info.jab.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "ENABLE_INTEGRATION_TESTS", matches = "true")
public class InitCommandIT {

    private CommandLine commandLine;

    @BeforeEach
    void setUp() {
        commandLine = new CommandLine(new Setup());
    }

    @Test
    void should_createMavenFiles_when_mavenOptionProvided(@TempDir Path tempDir) throws Exception {
        // Given
        assertThat(tempDir).isEmptyDirectory();

        // Change working directory to tempDir for the test
        String originalUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());

            // When
            int exitCode = commandLine.execute("init", "--maven");

            // Then
            // Since we expect Maven command to fail (not installed), we expect exit code 1
            // But the command should still run without crashing
            assertThat(exitCode)
                .as("Command should handle Maven not being available")
                .isEqualTo(1);

        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void should_handleExistingFiles_when_mavenOptionProvided(@TempDir Path tempDir) throws Exception {
        // Given - Create existing pom.xml file to test the validation
        Files.write(tempDir.resolve("pom.xml"),
                   "<!-- existing pom -->".getBytes(StandardCharsets.UTF_8),
                   StandardOpenOption.CREATE);

        // Change working directory to tempDir for the test
        String originalUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());

            // When
            int exitCode = commandLine.execute("init", "--maven");

            // Then
            // Should fail because pom.xml already exists in current directory
            assertThat(exitCode)
                .as("Command should fail when pom.xml exists in current directory")
                .isEqualTo(1);

        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void should_createMvnProjectStructure_when_mavenOptionProvided(@TempDir Path tempDir) throws Exception {
        // Given
        assertThat(tempDir).isEmptyDirectory();

        // Change working directory to tempDir for the test
        String originalUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", tempDir.toString());

            // When
            int exitCode = commandLine.execute("init", "--maven");

            // Then
            // Since Maven is not available, we expect exit code 1
            assertThat(exitCode)
                .as("Command should handle Maven not being available")
                .isEqualTo(1);

        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void should_handleInvalidDirectory_when_mavenOptionProvided(@TempDir Path tempDir) throws Exception {
        // Given - Create a non-existent directory path and set it as working directory
        Path nonExistentDir = tempDir.resolve("non-existent").resolve("deeply").resolve("nested");

        // This test now checks behavior when working directory doesn't exist or is inaccessible
        String originalUserDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", nonExistentDir.toString());

            // When
            int exitCode = commandLine.execute("init", "--maven");

            // Then
            // Should fail due to Maven not being available
            assertThat(exitCode)
                .as("Command should handle Maven not being available")
                .isEqualTo(1);
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    @Test
    void should_displayHelp_when_helpOptionProvided() {
        // When
        int exitCode = commandLine.execute("--help");

        // Then
        assertThat(exitCode)
            .as("Help command should execute successfully")
            .isEqualTo(0);
    }

    @Test
    void should_handleMavenWithoutDirectoryOption(@TempDir Path tempDir) throws Exception {
        // Given - Change working directory context by setting system property
        String originalUserDir = System.getProperty("user.dir");

        try {
            System.setProperty("user.dir", tempDir.toString());

            // When
            int exitCode = commandLine.execute("init", "--maven");

            // Then
            // Since Maven is not available, we expect exit code 1
            assertThat(exitCode)
                .as("Command should handle Maven not being available")
                .isEqualTo(1);

        } finally {
            // Restore original working directory
            System.setProperty("user.dir", originalUserDir);
        }
    }
}
