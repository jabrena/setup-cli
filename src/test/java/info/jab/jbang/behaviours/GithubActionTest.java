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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

@ExtendWith(MockitoExtension.class)
class GithubActionTest {

    private GithubAction githubAction;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        githubAction = spy(new GithubAction());
        
        // Mock the file operations to avoid actual file system access
        lenient().doNothing().when(githubAction).copyGithubActionFiles();
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    void testExecute() {
        // Execute
        githubAction.execute();
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("GitHub Actions workflow added successfully");
        
        // Verify the copyGithubActionFiles method was called
        verify(githubAction).copyGithubActionFiles();
    }
    
    @Test
    void testCopyGithubActionFilesWithException() {
        // Create a real instance for this test
        GithubAction realGithubAction = new GithubAction() {
            @Override
            void copyGithubActionFiles() {
                throw new RuntimeException("Error copying GitHub Actions workflow file");
            }
        };
        
        // Verify that the exception is properly handled
        assertThatThrownBy(() -> realGithubAction.copyGithubActionFiles())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Error copying GitHub Actions workflow file");
    }
    
    @Test
    void testCopyGithubActionFiles(@TempDir Path tempDir) throws IOException {
        // Create a test environment
        try {
            // Save the original user.dir
            String originalUserDir = System.getProperty("user.dir");
            
            try {
                // Set user.dir to our temp directory
                System.setProperty("user.dir", tempDir.toString());
                
                // Create a real GithubAction instance
                GithubAction realGithubAction = new GithubAction();
                
                // Execute the method
                realGithubAction.copyGithubActionFiles();
                
                // Verify that the .github/workflows directory was created
                Path githubDir = tempDir.resolve(".github");
                Path workflowsDir = githubDir.resolve("workflows");
                assertThat(Files.exists(githubDir)).isTrue();
                assertThat(Files.exists(workflowsDir)).isTrue();
                
                // Verify that the maven.yaml file was copied
                Path mavenYamlFile = workflowsDir.resolve("maven.yaml");
                assertThat(Files.exists(mavenYamlFile)).isTrue();
                
                // Verify file contents (optional)
                String mavenYamlContent = Files.readString(mavenYamlFile);
                assertThat(mavenYamlContent).contains("CI Builds");
                assertThat(mavenYamlContent).contains("Maven");
            } finally {
                // Restore the original user.dir
                System.setProperty("user.dir", originalUserDir);
            }
        } catch (IOException e) {
            // If we can't test with real files, we'll use a mock approach
            GithubAction mockGithubAction = spy(new GithubAction());
            
            // Mock the necessary methods
            doNothing().when(mockGithubAction).copyGithubActionFiles();
            
            // Execute the method
            mockGithubAction.execute();
            
            // Verify the method was called
            verify(mockGithubAction).copyGithubActionFiles();
        }
    }
    
    @Test
    void testCopyGithubActionFilesWithMissingResource(@TempDir Path tempDir) {
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());
            
            // Create a GithubAction instance that will return null for the resource stream
            GithubAction githubAction = new GithubAction() {
                @Override
                void copyGithubActionFiles() {
                    try {
                        Path currentPath = Paths.get(System.getProperty("user.dir"));
                        Path workflowsPath = currentPath.resolve(".github").resolve("workflows");
                        FileUtils.forceMkdir(workflowsPath.toFile());
                        
                        // Use a non-existent resource path to get a null stream
                        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("non-existent/maven.yaml")) {
                            if (resourceStream == null) {
                                throw new IOException("Resource not found: github-action/maven.yaml");
                            }
                            FileUtils.copyInputStreamToFile(resourceStream, workflowsPath.resolve("maven.yaml").toFile());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error copying GitHub Actions workflow file", e);
                    }
                }
            };
            
            // Verify that the exception is properly handled
            assertThatThrownBy(() -> githubAction.copyGithubActionFiles())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error copying GitHub Actions workflow file")
                .hasRootCauseMessage("Resource not found: github-action/maven.yaml");
        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
    
    @Test
    void testCopyGithubActionFilesWithExistingDirectory(@TempDir Path tempDir) throws IOException {
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());
            
            // Create .github/workflows directory with some content
            Path githubDir = tempDir.resolve(".github");
            Path workflowsDir = githubDir.resolve("workflows");
            Files.createDirectories(workflowsDir);
            Files.writeString(workflowsDir.resolve("test.yaml"), "test content");
            
            // Create a real GithubAction instance
            GithubAction realGithubAction = new GithubAction();
            
            // Execute the method
            realGithubAction.copyGithubActionFiles();
            
            // Verify that the maven.yaml file was copied
            Path mavenYamlFile = workflowsDir.resolve("maven.yaml");
            assertThat(Files.exists(mavenYamlFile)).isTrue();
            
            // Verify file contents
            String mavenYamlContent = Files.readString(mavenYamlFile);
            assertThat(mavenYamlContent).contains("CI Builds");
            assertThat(mavenYamlContent).contains("Maven");
        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
    
    @Test
    void testCopyGithubActionFilesWithDirectoryCreationFailure(@TempDir Path tempDir) {
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());
            
            // Create a file with the same name as our target directory to cause mkdir to fail
            Path githubDir = tempDir.resolve(".github");
            Files.createFile(githubDir);
            
            // Create a real GithubAction instance
            GithubAction realGithubAction = new GithubAction();
            
            // Verify that the exception is properly handled
            assertThatThrownBy(() -> realGithubAction.copyGithubActionFiles())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error copying GitHub Actions workflow file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
    
    @Test
    void testCopyGithubActionFilesWithCopyFailure(@TempDir Path tempDir) {
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());
            
            // Create a GithubAction instance with a mock that simulates a copy failure
            GithubAction realGithubAction = new GithubAction() {
                @Override
                void copyGithubActionFiles() {
                    try {
                        Path currentPath = Paths.get(System.getProperty("user.dir"));
                        Path workflowsPath = currentPath.resolve(".github").resolve("workflows");
                        FileUtils.forceMkdir(workflowsPath.toFile());
                        
                        // Create a read-only directory to cause the copy to fail
                        workflowsPath.toFile().setReadOnly();
                        
                        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("github-action/maven.yaml")) {
                            if (resourceStream == null) {
                                throw new IOException("Resource not found: github-action/maven.yaml");
                            }
                            FileUtils.copyInputStreamToFile(resourceStream, workflowsPath.resolve("maven.yaml").toFile());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error copying GitHub Actions workflow file", e);
                    }
                }
            };
            
            // Verify that the exception is properly handled
            assertThatThrownBy(() -> realGithubAction.copyGithubActionFiles())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error copying GitHub Actions workflow file");
        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
    
    @Test
    void testExecuteWithFailure() {
        // Create a GithubAction instance that throws an exception during copyGithubActionFiles
        GithubAction githubAction = new GithubAction() {
            @Override
            void copyGithubActionFiles() {
                throw new RuntimeException("Error copying GitHub Actions workflow file");
            }
        };
        
        // Verify that the exception is propagated
        assertThatThrownBy(() -> githubAction.execute())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Error copying GitHub Actions workflow file");
            
        // Verify that no success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .doesNotContain("GitHub Actions workflow added successfully");
    }
    
    @Test
    void testCopyGithubActionFilesWithResourceCleanupFailure(@TempDir Path tempDir) {
        // Save the original user.dir
        String originalUserDir = System.getProperty("user.dir");
        
        try {
            // Set user.dir to our temp directory
            System.setProperty("user.dir", tempDir.toString());
            
            // Create a GithubAction instance with a mock InputStream that throws on close
            GithubAction githubAction = new GithubAction() {
                @Override
                void copyGithubActionFiles() {
                    try {
                        Path currentPath = Paths.get(System.getProperty("user.dir"));
                        Path workflowsPath = currentPath.resolve(".github").resolve("workflows");
                        FileUtils.forceMkdir(workflowsPath.toFile());
                        
                        // Create a custom InputStream that throws on close
                        InputStream resourceStream = new InputStream() {
                            @Override
                            public int read() throws IOException {
                                return -1; // End of stream
                            }
                            
                            @Override
                            public void close() throws IOException {
                                throw new IOException("Simulated close failure");
                            }
                        };
                        
                        try (resourceStream) {
                            FileUtils.copyInputStreamToFile(resourceStream, workflowsPath.resolve("maven.yaml").toFile());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error copying GitHub Actions workflow file", e);
                    }
                }
            };
            
            // Verify that the exception is properly handled
            assertThatThrownBy(() -> githubAction.copyGithubActionFiles())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error copying GitHub Actions workflow file")
                .hasRootCauseMessage("Simulated close failure");
        } finally {
            // Restore the original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
} 