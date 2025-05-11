package info.jab.cli.behaviours;

import info.jab.cli.io.CopyFiles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GitIgnoreTest {

    @Mock
    private CopyFiles mockCopyFiles;

    private GitIgnore gitIgnore;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        // Use the constructor that accepts the mock
        gitIgnore = new GitIgnore(mockCopyFiles);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldUseProvidedCopyFilesInstance() {
        //Given
        // Mocks are injected via @InjectMocks
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        List<String> expectedFiles = List.of(".gitignore");
        String expectedSourceDir = "gitignore/";

        //When
        gitIgnore.execute();

        //Then
        verify(mockCopyFiles).copyFilesToDirectory(expectedFiles, expectedSourceDir, currentPath);
    }

    @Test
    void executeShouldCallCopyFilesWithCorrectParameters() {
        // Given
        // Mocks are injected via @InjectMocks
        Path expectedPath = Paths.get(System.getProperty("user.dir"));
        List<String> expectedFiles = List.of(".gitignore");
        String expectedSourceDir = "gitignore/";

        // When
        gitIgnore.execute();

        // Then
        verify(mockCopyFiles).copyFilesToDirectory(expectedFiles, expectedSourceDir, expectedPath);
    }
}
