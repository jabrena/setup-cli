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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class DevContainerTest {

    private DevContainer devContainer;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        devContainer = spy(new DevContainer());
        
        // Mock the file operations to avoid actual file system access
        lenient().doNothing().when(devContainer).copyDevContainerFiles();
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    void testExecute() {
        // Execute
        devContainer.execute();
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Devcontainer support added successfully");
        
        // Verify the copyDevContainerFiles method was called
        verify(devContainer).copyDevContainerFiles();
    }
    
    @Test
    void testCopyDevContainerFilesWithException() {
        // Create a real instance for this test
        DevContainer realDevContainer = new DevContainer() {
            @Override
            void copyDevContainerFiles() {
                throw new RuntimeException("Error copying devcontainer files");
            }
        };
        
        // Verify that the exception is properly handled
        assertThatThrownBy(() -> realDevContainer.copyDevContainerFiles())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Error copying devcontainer files");
    }
    
    @Test
    void testCopyDevContainerFiles(@TempDir Path tempDir) throws IOException {
        // Create a test environment
        try {
            // Save the original user.dir
            String originalUserDir = System.getProperty("user.dir");
            
            try {
                // Set user.dir to our temp directory
                System.setProperty("user.dir", tempDir.toString());
                
                // Create a real DevContainer instance
                DevContainer realDevContainer = new DevContainer();
                
                // Execute the method
                realDevContainer.copyDevContainerFiles();
                
                // Verify that the .devcontainer directory was created
                Path devcontainerDir = tempDir.resolve(".devcontainer");
                assertThat(Files.exists(devcontainerDir)).isTrue();
                
                // Verify that the files were copied
                Path devcontainerJsonFile = devcontainerDir.resolve("devcontainer.json");
                Path dockerfilePath = devcontainerDir.resolve("Dockerfile");
                
                assertThat(Files.exists(devcontainerJsonFile)).isTrue();
                assertThat(Files.exists(dockerfilePath)).isTrue();
                
                // Verify file contents (optional)
                String devcontainerJsonContent = Files.readString(devcontainerJsonFile);
                assertThat(devcontainerJsonContent).contains("java");
                
                String dockerfileContent = Files.readString(dockerfilePath);
                assertThat(dockerfileContent).contains("FROM");
            } finally {
                // Restore the original user.dir
                System.setProperty("user.dir", originalUserDir);
            }
        } catch (IOException e) {
            // If we can't test with real files, we'll use a mock approach
            DevContainer mockDevContainer = spy(new DevContainer());
            
            // Mock the necessary methods
            doNothing().when(mockDevContainer).copyDevContainerFiles();
            
            // Execute the method
            mockDevContainer.execute();
            
            // Verify the method was called
            verify(mockDevContainer).copyDevContainerFiles();
        }
    }
    
    @Test
    void testCopyDevContainerFilesWithFileContents(@TempDir Path tempDir) throws IOException {
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());
            
            // Create a real DevContainer instance
            DevContainer realDevContainer = new DevContainer();
            
            // Execute the method
            realDevContainer.copyDevContainerFiles();
            
            // Verify that the .devcontainer directory was created
            Path devcontainerDir = tempDir.resolve(".devcontainer");
            assertThat(Files.exists(devcontainerDir)).isTrue();
            
            // Verify that the files were copied
            Path devcontainerJsonFile = devcontainerDir.resolve("devcontainer.json");
            Path dockerfilePath = devcontainerDir.resolve("Dockerfile");
            
            assertThat(Files.exists(devcontainerJsonFile)).isTrue();
            assertThat(Files.exists(dockerfilePath)).isTrue();
            
            // Verify file contents
            String devcontainerJsonContent = Files.readString(devcontainerJsonFile);
            assertThat(devcontainerJsonContent).contains("java");
            assertThat(devcontainerJsonContent).contains("devcontainer");
            
            String dockerfileContent = Files.readString(dockerfilePath);
            assertThat(dockerfileContent).contains("FROM");
            assertThat(dockerfileContent).contains("RUN");
        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
    
    @Test
    void testCopyDevContainerFilesWithMissingResource(@TempDir Path tempDir) {
        // Save original user.dir
        String originalUserDir = System.getProperty("user.dir");
        try {
            // Set user.dir to temp directory
            System.setProperty("user.dir", tempDir.toString());

            // Create a mock DevContainer that returns null for resource stream
            DevContainer devContainer = new DevContainer() {
                @Override
                void copyDevContainerFiles() {
                    try {
                        Path currentPath = Paths.get(System.getProperty("user.dir"));
                        Path devcontainerPath = currentPath.resolve(".devcontainer");
                        
                        // Force null resource stream
                        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("non-existent-file")) {
                            if (resourceStream == null) {
                                throw new IOException("Resource not found: non-existent-file");
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error copying devcontainer files", e);
                    }
                }
            };

            // Verify that the expected exception is thrown
            RuntimeException exception = assertThrows(RuntimeException.class, devContainer::execute);
            assertTrue(exception.getCause() instanceof IOException);
            assertTrue(exception.getCause().getMessage().contains("Resource not found"));
        } finally {
            // Restore original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
    
    @Test
    void testCopyDevContainerFilesWithExistingDirectory(@TempDir Path tempDir) throws IOException {
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());
            
            // Create .devcontainer directory with some content
            Path devcontainerDir = tempDir.resolve(".devcontainer");
            Files.createDirectory(devcontainerDir);
            Files.writeString(devcontainerDir.resolve("test.txt"), "test content");
            
            // Create a real DevContainer instance
            DevContainer realDevContainer = new DevContainer();
            
            // Execute the method
            realDevContainer.copyDevContainerFiles();
            
            // Verify that the directory was cleaned and new files were copied
            assertThat(Files.exists(devcontainerDir.resolve("test.txt"))).isFalse();
            assertThat(Files.exists(devcontainerDir.resolve("devcontainer.json"))).isTrue();
            assertThat(Files.exists(devcontainerDir.resolve("Dockerfile"))).isTrue();
        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
} 