package info.jab.cli.behaviours;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import info.jab.cli.io.CopyFiles;

@ExtendWith(MockitoExtension.class)
class EditorConfigTest {

    @Mock
    private CopyFiles copyFilesMock;

    private EditorConfig editorConfig;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        // Use the constructor that accepts the mock
        editorConfig = new EditorConfig(copyFilesMock);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testExecute() {
        // Given
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        String expectedResourcePath = "templates/editorconfig-template/";
        doNothing().when(copyFilesMock).copyClasspathFolder(expectedResourcePath, currentPath);

        // When
        editorConfig.execute();

        // Then
        // Verify the copyClasspathFolder method was called with correct arguments
        verify(copyFilesMock).copyClasspathFolder(expectedResourcePath, currentPath);
    }

    @Test
    void testExecuteWithCopyException() {
        // Given
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        String expectedResourcePath = "templates/editorconfig-template/";
        doThrow(new RuntimeException("Error copying files")).when(copyFilesMock).copyClasspathFolder(expectedResourcePath, currentPath);

        // When / Then
        // Verify that the exception is properly handled
        assertThatThrownBy(() -> editorConfig.execute())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Error copying files");
    }

    @Test
    void testCopyEditorConfigFiles(@TempDir Path tempDir) throws IOException {
        // Given
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");

        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());

            // Create an EditorConfig instance with a real CopyFiles
            EditorConfig realEditorConfig = new EditorConfig();

            // When
            realEditorConfig.execute();

            // Then
            // Verify that the .editorconfig file was created
            Path editorconfigFile = tempDir.resolve(".editorconfig");
            assertThat(Files.exists(editorconfigFile)).isTrue();

        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
}
