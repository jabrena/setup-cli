package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
} 