package info.jab.cli.behaviours;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import info.jab.cli.io.CopyFiles;
import io.vavr.control.Either;

class DependabotTest {

    @Test
    void shouldExecuteSuccessfully() {
        // Given
        CopyFiles mockCopyFiles = mock(CopyFiles.class);
        doNothing().when(mockCopyFiles).copyClasspathFolder(any(String.class), any(Path.class));
        Dependabot dependabot = new Dependabot(mockCopyFiles);

        // When
        Either<String, String> result = dependabot.execute();

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Command execution completed successfully");
    }

    @Test
    void shouldCallCopyFilesWithCorrectParameters() {
        // Given
        CopyFiles mockCopyFiles = mock(CopyFiles.class);
        doNothing().when(mockCopyFiles).copyClasspathFolder(any(String.class), any(Path.class));
        Dependabot dependabot = new Dependabot(mockCopyFiles);
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedGithubPath = currentPath.resolve(".github");

        // When
        dependabot.execute();

        // Then
        verify(mockCopyFiles).copyClasspathFolder(eq("dependabot-template/"), eq(expectedGithubPath));
    }

    @Test
    void shouldCreateDependabotWithDefaultConstructor() {
        // When
        Dependabot defaultDependabot = new Dependabot();

        // Then
        assertThat(defaultDependabot).isNotNull();

        // Verify it can execute without throwing exceptions
        Either<String, String> result = defaultDependabot.execute();
        assertThat(result.isRight()).isTrue();
    }

    @Test
    void shouldReturnRightEitherWithSuccessMessage() {
        // Given
        CopyFiles mockCopyFiles = mock(CopyFiles.class);
        doNothing().when(mockCopyFiles).copyClasspathFolder(any(String.class), any(Path.class));
        Dependabot dependabot = new Dependabot(mockCopyFiles);

        // When
        Either<String, String> result = dependabot.execute();

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Command execution completed successfully");
        assertThat(result.isLeft()).isFalse();
    }

    @Test
    void shouldResolveCorrectGithubPath() {
        // Given
        CopyFiles mockCopyFiles = mock(CopyFiles.class);
        doNothing().when(mockCopyFiles).copyClasspathFolder(any(String.class), any(Path.class));
        Dependabot dependabot = new Dependabot(mockCopyFiles);

        // When
        dependabot.execute();

        // Then
        verify(mockCopyFiles).copyClasspathFolder(eq("dependabot-template/"), any(Path.class));

        // Verify the path resolution logic by checking the actual path passed
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedPath = currentPath.resolve(".github");
        verify(mockCopyFiles).copyClasspathFolder(eq("dependabot-template/"), eq(expectedPath));
    }
}
