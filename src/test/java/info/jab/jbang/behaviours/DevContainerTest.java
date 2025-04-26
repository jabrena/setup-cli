package info.jab.jbang.behaviours;

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

import info.jab.jbang.io.CopyFiles;
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
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    void testExecute() {
        // Setup expected paths and files
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        Path expectedDevcontainerPath = currentPath.resolve(".devcontainer");
        List<String> expectedFiles = List.of("Dockerfile", "devcontainer.json");
        String expectedSourceDir = "devcontainer/";

        // Execute
        devContainer.execute();
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Devcontainer support added successfully");
        
        // Verify the copyFilesToDirectory method was called with correct arguments
        verify(copyFiles).copyFilesToDirectory(expectedFiles, expectedSourceDir, expectedDevcontainerPath);
    }
}