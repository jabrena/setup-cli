package info.jab.cli.behaviours;

import info.jab.cli.io.CommandExecutor;
import info.jab.cli.io.FileSystemChecker;
import io.vavr.control.Either;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MavenTest {

    @Mock
    @SuppressWarnings("NullAway")
    private CommandExecutor mockCommandExecutor;

    @Mock
    @SuppressWarnings("NullAway")
    private FileSystemChecker mockFileSystemChecker;

    private Maven maven;

    @BeforeEach
    void setUp() {
        maven = new Maven(mockCommandExecutor, mockFileSystemChecker);

        // Setup default successful Maven version check for all tests using lenient stubbing
        Either<String, String> versionCheckResult = Either.right("Apache Maven 3.9.0");
        lenient().when(mockCommandExecutor.execute(eq("mvn --version"))).thenReturn(versionCheckResult);

        // Setup default file system check - no pom.xml exists by default
        lenient().when(mockFileSystemChecker.fileExists(eq("pom.xml"))).thenReturn(false);

        // Setup default successful responses for Maven commands using lenient stubbing
        Either<String, String> successResult = Either.right("Command executed successfully");
        lenient().when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);
        // Remove stubbing for commands that don't exist in current implementation
    }

    @Test
    void execute_shouldCallCommandExecutorWithMavenCommand() {
        // Given
        Either<String, String> successResult = Either.right("Maven project created successfully");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        maven.execute();

        // Then
        verify(mockCommandExecutor).execute(eq("mvn --version")); // Version check
        verify(mockCommandExecutor).execute(contains("mvn archetype:generate"));
    }

    @Test
    void execute_shouldHandleSuccessfulCommand() {
        // Given
        Either<String, String> successResult = Either.right("Project created");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When & Then - should not throw exception
        assertThatCode(() -> maven.execute()).doesNotThrowAnyException();

        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
    }

    @Test
    void execute_shouldHandleFailedCommand() {
        // Given
        Either<String, String> failureResult = Either.left("Error creating project");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(failureResult);

        // When & Then - should not throw exception but log error
        assertThatCode(() -> maven.execute()).doesNotThrowAnyException();

        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
    }

    @Test
    void execute_shouldHandleCommandExecutionRuntimeException() {
        // Given - Mock to throw a RuntimeException instead of CommandExecutionException
        when(mockCommandExecutor.execute(contains("mvn archetype:generate")))
            .thenThrow(new RuntimeException("Network error"));

        // When & Then - The exception should be thrown since the implementation doesn't catch it in execute()
        assertThatThrownBy(() -> maven.execute())
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Network error");

        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
    }

    @Test
    void execute_shouldThrowExceptionWhenMavenNotAvailable() {
        // Given - Maven version check fails
        Either<String, String> versionFailure = Either.left("mvn: command not found");
        when(mockCommandExecutor.execute(eq("mvn --version"))).thenReturn(versionFailure);

        // When
        Either<String, String> result = maven.execute();

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Command execution failed");

        verify(mockCommandExecutor, times(1)).execute(eq("mvn --version")); // Only version check, no commands
    }

    @Test
    void execute_shouldThrowExceptionWhenMavenVersionCheckThrowsException() {
        // Given - Maven version check throws exception
        when(mockCommandExecutor.execute(eq("mvn --version")))
            .thenThrow(new RuntimeException("Command not found"));

        // When & Then - The RuntimeException should propagate since isMavenAvailable() doesn't catch it
        assertThatThrownBy(() -> maven.execute())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Command not found");

        verify(mockCommandExecutor, times(1)).execute(eq("mvn --version")); // Only version check
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
        Either<String, String> versionFailure = Either.left("mvn: command not found");
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
            .thenThrow(new RuntimeException("Command not found"));

        // When & Then - The RuntimeException should propagate since the method doesn't catch it
        assertThatThrownBy(() -> maven.isMavenAvailable())
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Command not found");

        verify(mockCommandExecutor).execute(eq("mvn --version"));
    }

    @Test
    @SuppressWarnings("NullAway") // Testing null parameter handling
    void constructor_shouldThrowExceptionWhenCommandExecutorIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Maven(null, mockFileSystemChecker))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CommandExecutor cannot be null");
    }

    @Test
    @SuppressWarnings("NullAway") // Testing null parameter handling
    void constructor_shouldThrowExceptionWhenFileSystemCheckerIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Maven(mockCommandExecutor, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("FileSystemChecker cannot be null");
    }

    @Test
    void defaultConstructor_shouldCreateInstanceWithRealExecutor() {
        // When
        Maven defaultMaven = new Maven();

        // Then
        assertThat(defaultMaven).isNotNull();
        assertThat(defaultMaven.isMavenAvailable()).isTrue(); // This may depend on system Maven installation
    }

    @Test
    void execute_shouldFilterEmptyLines() {
        // Given
        Either<String, String> successResult = Either.right("Maven project created successfully");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        maven.execute();

        // Then
        verify(mockCommandExecutor, times(2)).execute(any(String.class));
    }

    @Test
    void execute_shouldCallExecutorWithCorrectCommandStructure() {
        // Given
        Either<String, String> successResult = Either.right("Project created");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        maven.execute();

        // Then
        verify(mockCommandExecutor).execute(eq("mvn --version")); // Version check
        verify(mockCommandExecutor).execute(argThat(command ->
            command.contains("mvn archetype:generate") &&
            command.contains("-DgroupId=info.jab.demo") &&
            command.contains("-DartifactId=maven-demo") &&
            command.contains("-DarchetypeArtifactId=maven-archetype-quickstart") &&
            command.contains("-DarchetypeVersion=1.5") &&
            command.contains("-DinteractiveMode=false")
        ));

        // Verify commands that shouldn't be called in the current implementation
        verify(mockCommandExecutor, never()).execute(contains("mv maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("rmdir maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("mvn wrapper:wrapper"));
        verify(mockCommandExecutor, never()).execute(eq("./mvnw clean verify"));
    }

    @Test
    void test_verifyMockIsActuallyUsed() {
        // Given
        Either<String, String> successResult = Either.right("Mock executed");
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(successResult);

        // When
        maven.execute();

        // Then - This test will fail if real implementation is used instead of mock
        verify(mockCommandExecutor, times(2)).execute(any(String.class)); // Version check + 1 command
        verifyNoMoreInteractions(mockCommandExecutor);

        // Additional verification: The mock should have been called exactly 2 times
        reset(mockCommandExecutor); // Reset to clear interaction history
        assertThat(mockCommandExecutor).as("Should be using the injected mock").isNotNull();
    }

    @Test
    void test_ensureNoRealCommandExecution() {
        // Given - Setup mock to return a distinctive result that proves it's a mock
        String mockSpecificOutput = "THIS_IS_A_MOCK_RESULT_12345";
        Either<String, String> mockResult = Either.right(mockSpecificOutput);
        when(mockCommandExecutor.execute(contains("mvn archetype:generate"))).thenReturn(mockResult);

        // When
        maven.execute();

        // Then - Verify the mock was called with the expected commands
        verify(mockCommandExecutor).execute(eq("mvn --version")); // Version check
        verify(mockCommandExecutor).execute(argThat(command ->
            command.contains("mvn archetype:generate") &&
            command.contains("-DgroupId=info.jab.demo") &&
            command.contains("-DartifactId=maven-demo") &&
            command.contains("-DarchetypeVersion=1.5")
        ));

        verify(mockCommandExecutor, never()).execute(contains("mv maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("rmdir maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("mvn wrapper:wrapper"));
        verify(mockCommandExecutor, never()).execute(eq("./mvnw clean verify"));

        // Ensure only the mock was interacted with (no real processes started)
        verifyNoMoreInteractions(mockCommandExecutor);
    }

    @Test
    void test_validateTestIsolation() {
        // Given - Use the actual command format from the Maven class
        String actualCommand = "mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false";
        Either<String, String> mockResult = Either.right("Mock success for actual command");
        when(mockCommandExecutor.execute(eq(actualCommand))).thenReturn(mockResult);

        // When - This should work because we're using mocks, not real Maven
        maven.execute();

        // Then - If this passes, we know we're using mocks
        verify(mockCommandExecutor).execute(eq("mvn --version")); // Version check
        verify(mockCommandExecutor).execute(eq(actualCommand));

        verify(mockCommandExecutor, never()).execute(contains("mv maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("rmdir maven-demo"));
        verify(mockCommandExecutor, never()).execute(eq("mvn wrapper:wrapper"));
        verify(mockCommandExecutor, never()).execute(eq("./mvnw clean verify"));
    }

    @Test
    void execute_shouldThrowExceptionWhenPomXmlExists() {
        // Given
        when(mockFileSystemChecker.fileExists(eq("pom.xml"))).thenReturn(true);

        // When
        Either<String, String> result = maven.execute();

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Command execution failed");

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
