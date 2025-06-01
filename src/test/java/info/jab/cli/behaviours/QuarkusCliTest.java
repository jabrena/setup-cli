package info.jab.cli.behaviours;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import info.jab.cli.io.CommandExecutor;
import info.jab.cli.io.FileSystemChecker;
import io.vavr.control.Either;

@ExtendWith(MockitoExtension.class)
class QuarkusCliTest {

    @Mock
    private CommandExecutor mockCommandExecutor;

    @Mock
    private FileSystemChecker mockFileSystemChecker;

    private QuarkusCli quarkusCli;

    @BeforeEach
    @SuppressWarnings("NullAway") // Mock fields are guaranteed to be initialized by Mockito before @BeforeEach
    void setUp() {
        // Ensure mocks are non-null for NullAway
        Objects.requireNonNull(mockCommandExecutor, "mockCommandExecutor should be initialized by Mockito");
        Objects.requireNonNull(mockFileSystemChecker, "mockFileSystemChecker should be initialized by Mockito");
        quarkusCli = new QuarkusCli(mockCommandExecutor, mockFileSystemChecker);
    }

    @Test
    @SuppressWarnings("NullAway") // Intentionally testing null validation
    void should_throwException_when_commandExecutorIsNull() {
        // Given - When - Then
        assertThatThrownBy(() -> new QuarkusCli(null, mockFileSystemChecker))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("CommandExecutor cannot be null");
    }

    @Test
    @SuppressWarnings("NullAway") // Intentionally testing null validation
    void should_throwException_when_fileSystemCheckerIsNull() {
        // Given - When - Then
        assertThatThrownBy(() -> new QuarkusCli(mockCommandExecutor, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("FileSystemChecker cannot be null");
    }

    @Test
    void should_executeSuccessfully_when_quarkusCliAvailableAndNoPomXml() {
        // Given
        when(mockCommandExecutor.execute("quarkus --version"))
            .thenReturn(Either.right("Quarkus CLI version 3.0.0"));
        when(mockFileSystemChecker.fileExists("pom.xml"))
            .thenReturn(false);
        when(mockCommandExecutor.execute("quarkus create app quarkus-demo"))
            .thenReturn(Either.right("Project created successfully"));

        // When
        Either<String, String> result = quarkusCli.execute();

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Quarkus command completed successfully");
        verify(mockCommandExecutor).execute("quarkus --version");
        verify(mockFileSystemChecker).fileExists("pom.xml");
        verify(mockCommandExecutor).execute("quarkus create app quarkus-demo");
    }

    @Test
    void should_returnLeft_when_quarkusCliNotAvailable() {
        // Given
        when(mockCommandExecutor.execute("quarkus --version"))
            .thenReturn(Either.left("Command not found"));

        // When
        Either<String, String> result = quarkusCli.execute();

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Quarkus command not found. Please install Quarkus and ensure it's in your PATH.");
        verify(mockCommandExecutor).execute("quarkus --version");
    }

    @Test
    void should_returnLeft_when_pomXmlExists() {
        // Given
        when(mockCommandExecutor.execute("quarkus --version"))
            .thenReturn(Either.right("Quarkus CLI version 3.0.0"));
        when(mockFileSystemChecker.fileExists("pom.xml"))
            .thenReturn(true);

        // When
        Either<String, String> result = quarkusCli.execute();

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Cannot create Maven project: pom.xml already exists in current directory. Please run this command in an empty directory.");
        verify(mockCommandExecutor).execute("quarkus --version");
        verify(mockFileSystemChecker).fileExists("pom.xml");
    }

    @Test
    void should_returnLeft_when_quarkusCommandFails() {
        // Given
        when(mockCommandExecutor.execute("quarkus --version"))
            .thenReturn(Either.right("Quarkus CLI version 3.0.0"));
        when(mockFileSystemChecker.fileExists("pom.xml"))
            .thenReturn(false);
        when(mockCommandExecutor.execute("quarkus create app quarkus-demo"))
            .thenReturn(Either.left("Command failed"));

        // When
        Either<String, String> result = quarkusCli.execute();

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Quarkus command failed");
        verify(mockCommandExecutor).execute("quarkus --version");
        verify(mockFileSystemChecker).fileExists("pom.xml");
        verify(mockCommandExecutor).execute("quarkus create app quarkus-demo");
    }

    @Test
    void should_returnTrue_when_quarkusCliIsAvailable() {
        // Given
        when(mockCommandExecutor.execute("quarkus --version"))
            .thenReturn(Either.right("Quarkus CLI version 3.0.0"));

        // When
        boolean result = quarkusCli.isQuarkusCliAvailable();

        // Then
        assertThat(result).isTrue();
        verify(mockCommandExecutor).execute("quarkus --version");
    }

    @Test
    void should_returnFalse_when_quarkusCliIsNotAvailable() {
        // Given
        when(mockCommandExecutor.execute("quarkus --version"))
            .thenReturn(Either.left("Command not found"));

        // When
        boolean result = quarkusCli.isQuarkusCliAvailable();

        // Then
        assertThat(result).isFalse();
        verify(mockCommandExecutor).execute("quarkus --version");
    }

    @Test
    void should_returnTrue_when_pomXmlExists() {
        // Given
        when(mockFileSystemChecker.fileExists("pom.xml")).thenReturn(true);

        // When
        boolean result = quarkusCli.pomXmlExists();

        // Then
        assertThat(result).isTrue();
        verify(mockFileSystemChecker).fileExists("pom.xml");
    }

    @Test
    void should_returnFalse_when_pomXmlDoesNotExist() {
        // Given
        when(mockFileSystemChecker.fileExists("pom.xml")).thenReturn(false);

        // When
        boolean result = quarkusCli.pomXmlExists();

        // Then
        assertThat(result).isFalse();
        verify(mockFileSystemChecker).fileExists("pom.xml");
    }
}
