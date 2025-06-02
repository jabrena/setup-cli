package info.jab.cli.behaviours;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import info.jab.cli.io.CopyFiles;

@ExtendWith(MockitoExtension.class)
class DevContainerTest {

    @Mock
    private CopyFiles copyFiles;

    @InjectMocks
    private DevContainer devContainer;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void testExecute() {
        // Given
        // Setup expected paths
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedDevcontainerPath = currentPath.resolve(".devcontainer");
        String expectedSourceDir = "templates/devcontainer-template/";

        // When
        devContainer.execute();

        // Then
        // Verify the copyClasspathFolder method was called with correct arguments
        verify(copyFiles).copyClasspathFolder(expectedSourceDir, expectedDevcontainerPath);
    }
}
