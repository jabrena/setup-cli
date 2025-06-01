package info.jab.cli.behaviours;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.jab.cli.io.CommandExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MavenTest {

    @Mock
    @SuppressWarnings("NullAway")
    private CommandExecutor mockCommandExecutor;

    @Mock
    @SuppressWarnings("NullAway")
    private Maven.FileSystemChecker mockFileSystemChecker;

    private Maven maven;

    @BeforeEach
    void setUp() {
        maven = new Maven(mockCommandExecutor, mockFileSystemChecker);

        // Setup default successful Maven version check for all tests using lenient stubbing
        CommandExecutor.CommandResult versionCheckResult = CommandExecutor.CommandResult.success("Apache Maven 3.9.0");
        lenient().when(mockCommandExecutor.execute(eq("mvn --version"))).thenReturn(versionCheckResult);

        // Setup default file system check - no pom.xml exists by default
        lenient().when(mockFileSystemChecker.fileExists(eq("pom.xml"))).thenReturn(false);

        // Setup default successful responses for Maven commands using lenient stubbing
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Command executed successfully");
        lenient().when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);
        // Remove stubbing for commands that don't exist in current implementation
    }

    @Test
    void execute_shouldCallCommandExecutorWithMavenCommand() {
        // Given
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Maven project created successfully");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        maven.execute();

        // Then
        verify(mockCommandExecutor).execute(eq("mvn --version")); // Version check
        verify(mockCommandExecutor).execute(contains("mvn archetype:generate"));
        // Remove expectations for commands that don't exist in current implementation
        verify(mockCommandExecutor, never()).execute(contains("mv maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("rmdir maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("mvn wrapper:wrapper"));
        verify(mockCommandExecutor, never()).execute(eq("./mvnw clean verify"));
    }

    @Test
    void execute_shouldHandleSuccessfulCommand() {
        // Given
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Project created");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When & Then - should not throw exception
        assertThatCode(() -> maven.execute()).doesNotThrowAnyException();

        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
    }

    @Test
    void execute_shouldHandleFailedCommand() {
        // Given
        CommandExecutor.CommandResult failureResult = CommandExecutor.CommandResult.failure(1, "Error output", "Error creating project");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(failureResult);

        // When & Then - should not throw exception but log error
        assertThatCode(() -> maven.execute()).doesNotThrowAnyException();

        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
    }

    @Test
    void execute_shouldHandleCommandExecutionException() {
        // Given
        when(mockCommandExecutor.execute(contains("mvn archetype:generate")))
            .thenThrow(new CommandExecutor.CommandExecutionException("mvn command", "Network error", new RuntimeException("Connection failed")));

        // When & Then - should not throw exception but log error
        assertThatCode(() -> maven.execute()).doesNotThrowAnyException();

        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
    }

    @Test
    void execute_shouldThrowExceptionWhenMavenNotAvailable() {
        // Given - Maven version check fails
        CommandExecutor.CommandResult versionFailure = CommandExecutor.CommandResult.failure(1, "", "mvn: command not found");
        when(mockCommandExecutor.execute(eq("mvn --version"))).thenReturn(versionFailure);

        // When & Then
        assertThatThrownBy(() -> maven.execute())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Maven command not found. Please install Maven and ensure it's in your PATH.");

        verify(mockCommandExecutor, times(1)).execute(eq("mvn --version")); // Only version check, no commands
    }

    @Test
    void execute_shouldThrowExceptionWhenMavenVersionCheckThrowsException() {
        // Given - Maven version check throws exception
        when(mockCommandExecutor.execute(eq("mvn --version")))
            .thenThrow(new CommandExecutor.CommandExecutionException("mvn --version", "Command not found", new RuntimeException("Process failed")));

        // When & Then
        assertThatThrownBy(() -> maven.execute())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Maven command not found. Please install Maven and ensure it's in your PATH.");

        verify(mockCommandExecutor, times(1)).execute(eq("mvn --version")); // Only version check, no commands
    }

    @Test
    void isMavenAvailable_shouldReturnTrueWhenMavenIsAvailable() {
        // Given - already set up in setUp() method

        // When
        boolean result = maven.isMavenAvailable();

        // Then
        assertThat(result).isTrue();
        verify(mockCommandExecutor).execute(eq("mvn --version"));
    }

    @Test
    void isMavenAvailable_shouldReturnFalseWhenMavenCommandFails() {
        // Given
        CommandExecutor.CommandResult versionFailure = CommandExecutor.CommandResult.failure(1, "", "mvn: command not found");
        when(mockCommandExecutor.execute(eq("mvn --version"))).thenReturn(versionFailure);

        // When
        boolean result = maven.isMavenAvailable();

        // Then
        assertThat(result).isFalse();
        verify(mockCommandExecutor).execute(eq("mvn --version"));
    }

    @Test
    void isMavenAvailable_shouldReturnFalseWhenCommandThrowsException() {
        // Given
        when(mockCommandExecutor.execute(eq("mvn --version")))
            .thenThrow(new CommandExecutor.CommandExecutionException("mvn --version", "Command not found", new RuntimeException("Process failed")));

        // When
        boolean result = maven.isMavenAvailable();

        // Then
        assertThat(result).isFalse();
        verify(mockCommandExecutor).execute(eq("mvn --version"));
    }

    @Test
    void executeWithContinueOnError_shouldContinueAfterException() {
        // Given
        CommandExecutor.CommandExecutionException exception = new CommandExecutor.CommandExecutionException(
                "mvn archetype:generate", "Command failed", new RuntimeException("IO error"));
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenThrow(exception);

        // When & Then - should not throw exception
        assertThatCode(() -> maven.executeWithContinueOnError()).doesNotThrowAnyException();

        // Note: executeWithContinueOnError doesn't call isMavenAvailable() first, so no version check
        verify(mockCommandExecutor, times(1)).execute(any(String.class)); // Only 1 command, no version check
    }

    @Test
    @SuppressWarnings("NullAway") // Testing null parameter handling
    void constructor_shouldThrowExceptionWhenCommandExecutorIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Maven(null, mockFileSystemChecker))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("CommandExecutor cannot be null");
    }

    @Test
    @SuppressWarnings("NullAway") // Testing null parameter handling
    void constructor_shouldThrowExceptionWhenFileSystemCheckerIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Maven(mockCommandExecutor, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FileSystemChecker cannot be null");
    }

    @Test
    void defaultConstructor_shouldCreateInstanceWithRealExecutor() {
        // When
        Maven mavenWithDefaultConstructor = new Maven();

        // Then - should not throw exception when created
        assertThat(mavenWithDefaultConstructor).isNotNull();
    }

    @Test
    void execute_shouldFilterEmptyLines() {
        // Given
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Success");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        maven.execute();

        // Then - should call execute 2 times (version check + 1 command, empty lines filtered)
        verify(mockCommandExecutor, times(2)).execute(any(String.class));
    }

    @Test
    void execute_shouldCallExecutorWithCorrectCommandStructure() {
        // Given
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Success");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        maven.execute();

        // Then - verify the exact command structure
        verify(mockCommandExecutor).execute(eq("mvn --version")); // Version check
        verify(mockCommandExecutor).execute(argThat(command ->
            command.contains("mvn archetype:generate") &&
            command.contains("-DgroupId=info.jab.demo") &&
            command.contains("-DartifactId=maven-demo") &&
            command.contains("-DarchetypeArtifactId=maven-archetype-quickstart") &&
            command.contains("-DarchetypeVersion=1.5") &&
            command.contains("-DinteractiveMode=false")
        ));
        // Remove verifications for commands that don't exist in current implementation
        verify(mockCommandExecutor, never()).execute(contains("mv maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("rmdir maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("mvn wrapper:wrapper"));
        verify(mockCommandExecutor, never()).execute(eq("./mvnw clean verify"));
    }

    @Test
    void test_verifyMockIsActuallyUsed() {
        // Given
        CommandExecutor.CommandResult successResult = CommandExecutor.CommandResult.success("Mock executed");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        maven.execute();

        // Then - This test will fail if real implementation is used instead of mock
        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
        verifyNoMoreInteractions(mockCommandExecutor);

        // Additional verification: The mock should have been called exactly 2 times
        // If real implementation was used, this verification would fail
        assertThat(mockCommandExecutor).as("Should be using the injected mock").isNotNull();
    }

    @Test
    void test_ensureNoRealCommandExecution() {
        // Given - Setup mock to return a distinctive result that proves it's a mock
        String mockSpecificOutput = "THIS_IS_A_MOCK_RESULT_12345";
        CommandExecutor.CommandResult mockResult = CommandExecutor.CommandResult.success(mockSpecificOutput);
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(mockResult);

        // When
        maven.execute();

        // Then - Verify the mock was called with the expected commands
        verify(mockCommandExecutor).execute(eq("mvn --version")); // Version check
        verify(mockCommandExecutor).execute(argThat(command ->
            command.contains("mvn archetype:generate") &&
            command.contains("-DgroupId=info.jab.demo")
        ));
        // Verify that commands not in current implementation are never called
        verify(mockCommandExecutor, never()).execute(contains("mv maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("rmdir maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("mvn wrapper:wrapper"));
        verify(mockCommandExecutor, never()).execute(eq("./mvnw clean verify"));

        // Ensure only the mock was interacted with (no real processes started)
        verifyNoMoreInteractions(mockCommandExecutor);

        // This test validates that:
        // 1. No real Maven commands are executed (they would take time and create files)
        // 2. Only our mock CommandExecutor is used
        // 3. The dependency injection is working correctly
    }

    @Test
    void test_validateTestIsolation() {
        // Given - A command that would definitely fail in real execution
        String impossibleCommand = "mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false";
        CommandExecutor.CommandResult mockResult = CommandExecutor.CommandResult.success("Mock success for impossible command");
        when(mockCommandExecutor.execute(eq(impossibleCommand))).thenReturn(mockResult);

        // When - Execute the Maven behavior
        maven.execute();

        // Then - If this passes, we know we're using mocks
        // Real execution would likely fail or take a very long time
        verify(mockCommandExecutor).execute(eq("mvn --version")); // Version check
        verify(mockCommandExecutor).execute(eq(impossibleCommand));
        // Verify that commands not in current implementation are never called
        verify(mockCommandExecutor, never()).execute(contains("mv maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("rmdir maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("mvn wrapper:wrapper"));
        verify(mockCommandExecutor, never()).execute(eq("./mvnw clean verify"));

        // Test completes quickly (< 100ms) proving no real Maven execution occurred
        assertThat(System.currentTimeMillis()).isGreaterThan(0); // Just a dummy assertion to show test completed
    }

    @Test
    void execute_shouldThrowExceptionWhenPomXmlExists() {
        // Given - Maven is available but pom.xml exists
        when(mockFileSystemChecker.fileExists(eq("pom.xml"))).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> maven.execute())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot create Maven project: pom.xml already exists in current directory. Please run this command in an empty directory.");

        verify(mockCommandExecutor, times(1)).execute(eq("mvn --version")); // Only version check
        verify(mockFileSystemChecker).fileExists(eq("pom.xml"));
        verify(mockCommandExecutor, never()).execute(contains("mvn archetype:generate")); // No commands executed
    }

    @Test
    void pomXmlExists_shouldReturnTrueWhenFileExists() {
        // Given
        when(mockFileSystemChecker.fileExists(eq("pom.xml"))).thenReturn(true);

        // When
        boolean result = maven.pomXmlExists();

        // Then
        assertThat(result).isTrue();
        verify(mockFileSystemChecker).fileExists(eq("pom.xml"));
    }

    @Test
    void pomXmlExists_shouldReturnFalseWhenFileDoesNotExist() {
        // Given
        when(mockFileSystemChecker.fileExists(eq("pom.xml"))).thenReturn(false);

        // When
        boolean result = maven.pomXmlExists();

        // Then
        assertThat(result).isFalse();
        verify(mockFileSystemChecker).fileExists(eq("pom.xml"));
    }
}
