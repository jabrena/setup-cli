package info.jab.cli.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;
import info.jab.cli.io.CommandExecutor;
import info.jab.cli.io.FileSystemChecker;
import io.vavr.control.Either;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NullAway")
class SpringCliTest {

    @Mock
    private CommandExecutor mockCommandExecutor;

    @Mock
    private FileSystemChecker mockFileSystemChecker;

    private SpringCli springCli;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream errorStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errorStreamCaptor));
        springCli = new SpringCli(mockCommandExecutor, mockFileSystemChecker);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void shouldPrintCommandsWhenSpringCliIsNotInstalled() throws IOException, InterruptedException {
        // Given
        when(mockCommandExecutor.execute("spring --version")).thenReturn(Either.left("Command not found"));

        // When
        Either<String, String> result = springCli.execute();

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).contains("Command execution failed");
    }

    @Test
    void shouldExecuteCommandWhenSpringCliIsInstalled() throws IOException, InterruptedException {
        // Given
        when(mockFileSystemChecker.fileExists("pom.xml")).thenReturn(false);
        when(mockCommandExecutor.execute("spring --version")).thenReturn(Either.right("Spring CLI v3.2.0"));
        when(mockCommandExecutor.execute("spring init -d=web,actuator,devtools --build=maven --force ./"))
            .thenReturn(Either.right("Project created successfully"));

        // When
        Either<String, String> result = springCli.execute();

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).contains("Command execution completed successfully");
    }

    @Test
    void shouldHandleExceptionWhenExecutingCommand() throws IOException, InterruptedException {
        // Given
        when(mockFileSystemChecker.fileExists("pom.xml")).thenReturn(false);
        when(mockCommandExecutor.execute("spring --version")).thenReturn(Either.right("Spring CLI v3.2.0"));
        when(mockCommandExecutor.execute("spring init -d=web,actuator,devtools --build=maven --force ./"))
            .thenReturn(Either.left("Command failed"));

        // When
        Either<String, String> result = springCli.execute();

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).contains("Command execution failed");
    }

    @Test
    void shouldCreateSpringCliWithDefaultExecutor() {
        // When
        SpringCli springCli = new SpringCli();

        // Then
        assertThat(springCli).isNotNull();
    }

    @Test
    void testExecute() {
        // Given
        when(mockFileSystemChecker.fileExists("pom.xml")).thenReturn(false);
        when(mockCommandExecutor.execute("spring --version")).thenReturn(Either.right("Spring CLI v3.2.0"));
        when(mockCommandExecutor.execute("spring init -d=web,actuator,devtools --build=maven --force ./"))
            .thenReturn(Either.right("Project created successfully"));

        // When
        Either<String, String> result = springCli.execute();

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).contains("Command execution completed successfully");
    }

    @Test
    void shouldFailWhenPomXmlExists() {
        // Given
        when(mockFileSystemChecker.fileExists("pom.xml")).thenReturn(true);
        when(mockCommandExecutor.execute("spring --version")).thenReturn(Either.right("Spring CLI v3.5.0"));

        // When
        Either<String, String> result = springCli.execute();

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).contains("Command execution failed");
    }
}
