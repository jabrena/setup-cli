package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import info.jab.jbang.io.CopyFiles;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.nio.charset.StandardCharsets;

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
        List<String> expectedFiles = List.of(".editorconfig");
        String expectedResourcePath = "editorconfig/";
        doNothing().when(copyFilesMock).copyFilesToDirectory(expectedFiles, expectedResourcePath, currentPath);

        // When
        editorConfig.execute();

        // Then
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("EditorConfig support added successfully");

        // Verify the copyFilesToDirectory method was called with correct arguments
        verify(copyFilesMock).copyFilesToDirectory(expectedFiles, expectedResourcePath, currentPath);
    }

    @Test
    void testExecuteWithCopyException() {
        // Given
        Path currentPath = Paths.get(System.getProperty("user.dir"));
        List<String> expectedFiles = List.of(".editorconfig");
        String expectedResourcePath = "editorconfig/";
        doThrow(new RuntimeException("Error copying files")).when(copyFilesMock).copyFilesToDirectory(expectedFiles, expectedResourcePath, currentPath);

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
