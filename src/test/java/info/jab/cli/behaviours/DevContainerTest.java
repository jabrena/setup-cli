package info.jab.cli.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.nio.charset.StandardCharsets;

import info.jab.cli.io.CopyFiles;
import org.mockito.Mock;
import org.mockito.InjectMocks;

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
        // Setup expected paths and files
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedDevcontainerPath = currentPath.resolve(".devcontainer");
        List<String> expectedFiles = List.of( "devcontainer.json");
        String expectedSourceDir = "devcontainer/";

        // When
        devContainer.execute();

        // Then
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Devcontainer support added successfully");

        // Verify the copyFilesToDirectory method was called with correct arguments
        verify(copyFiles).copyFilesToDirectory(expectedFiles, expectedSourceDir, expectedDevcontainerPath);
    }
}
