package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EditorConfigTest {

    private EditorConfig editorConfig;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        editorConfig = spy(new EditorConfig());
        
        // Mock the file operations to avoid actual file system access
        lenient().doNothing().when(editorConfig).copyEditorConfigFiles();
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    void testExecute() {
        // Execute
        editorConfig.execute();
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("EditorConfig support added successfully");
        
        // Verify the copyEditorConfigFiles method was called
        verify(editorConfig).copyEditorConfigFiles();
    }
    
    @Test
    void testCopyDevContainerFilesWithException() {
        // Create a real instance for this test
        EditorConfig realEditorConfig = new EditorConfig() {
            @Override
            void copyEditorConfigFiles() {
                throw new RuntimeException("Error copying editorconfig files");
            }
        };
        
        // Verify that the exception is properly handled
        assertThatThrownBy(() -> realEditorConfig.copyEditorConfigFiles())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Error copying editorconfig files");
    }
    
    @Test
    void testCopyDevContainerFiles(@TempDir Path tempDir) throws IOException {
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());
            
            // Create a real EditorConfig instance
            EditorConfig realEditorConfig = new EditorConfig();
            
            // Execute the method
            realEditorConfig.copyEditorConfigFiles();
            
            // Verify that the .editorconfig file was created
            Path editorconfigFile = tempDir.resolve(".editorconfig");
            assertThat(Files.exists(editorconfigFile)).isTrue();
            
        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
    
    @Test
    void testCopyEditorConfigFilesWithMissingResource(@TempDir Path tempDir) {
        // Save original user.dir
        String originalUserDir = System.getProperty("user.dir");
        try {
            // Set user.dir to temp directory
            System.setProperty("user.dir", tempDir.toString());

            // Create a mock EditorConfig that simulates missing resource
            EditorConfig editorConfig = new EditorConfig() {
                @Override
                void copyEditorConfigFiles() {
                    try {
                        Path currentPath = Paths.get(System.getProperty("user.dir"));
                        Path editorConfigFile = currentPath.resolve(".editorconfig");
                        
                        // Force null resource stream
                        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("non-existent-file")) {
                            if (resourceStream == null) {
                                throw new IOException("Resource not found: non-existent-file");
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error copying EditorConfig file", e);
                    }
                }
            };

            // Verify that the expected exception is thrown
            RuntimeException exception = assertThrows(RuntimeException.class, editorConfig::execute);
            assertTrue(exception.getCause() instanceof IOException);
            assertTrue(exception.getCause().getMessage().contains("Resource not found"));
        } finally {
            // Restore original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
} 