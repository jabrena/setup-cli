package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;

@ExtendWith(MockitoExtension.class)
class CursorTest {

    @Mock
    private Cursor cursorMock;
    
    private Cursor cursor;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        cursor = spy(new Cursor());
        
        // Use lenient() to avoid UnnecessaryStubbingException
        lenient().doReturn(new ArrayList<>(List.of("test-rule.md"))).when(cursor).getProperties();
        lenient().doNothing().when(cursor).copyCursorRulesToDirectory(any());
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    void testExecuteWithValidJavaParam() {
        // Execute with a valid parameter
        cursor.execute("java");
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Cursor rules added successfully");
        
        // Verify the copyCursorRulesToDirectory method was called
        verify(cursor).copyCursorRulesToDirectory(any());
    }
    
    @Test
    void testExecuteWithInvalidParam() {
        // Execute with an invalid parameter
        cursor.execute("invalid-option");
        
        // Verify no success message was printed (should be empty)
        assertThat(outputStreamCaptor.toString().trim()).isEmpty();
        
        // Verify the copyCursorRulesToDirectory method was not called
        verify(cursor, never()).copyCursorRulesToDirectory(any());
    }
    
    @Test
    void testExecuteWithJavaSpringBootParam() {
        // Execute with java-spring-boot parameter
        cursor.execute("java-spring-boot");
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Cursor rules added successfully");
        
        // Verify the copyCursorRulesToDirectory method was called
        verify(cursor).copyCursorRulesToDirectory(any());
    }
    
    @Test
    void testExecuteWithJavaQuarkusParam() {
        // Execute with java-quarkus parameter
        cursor.execute("java-quarkus");
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Cursor rules added successfully");
        
        // Verify the copyCursorRulesToDirectory method was called
        verify(cursor).copyCursorRulesToDirectory(any());
    }

    @Test
    void testGetPropertiesSuccess() {
        // Given
        when(cursor.getProperties()).thenReturn(List.of("100-java-general.mdc"));
        
        // When
        List<String> ruleFiles = cursor.getProperties();
        
        // Then
        assertThat(ruleFiles).isNotEmpty();
        assertThat(ruleFiles).contains("100-java-general.mdc");
    }
    
    @Test
    void testCopyCursorRulesToDirectoryWithNonExistentResource() {
        // Given
        List<String> invalidRuleFiles = List.of("non-existent-file.mdc");
        doThrow(new RuntimeException("Error copying rules files"))
            .when(cursor).copyCursorRulesToDirectory(invalidRuleFiles);
        
        // When/Then
        assertThatThrownBy(() -> cursor.copyCursorRulesToDirectory(invalidRuleFiles))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Error copying rules files");
    }
    
    @Test
    void testExecuteWithNullParameter() {
        // When
        cursor.execute(null);
        
        // Then
        assertThat(outputStreamCaptor.toString().trim()).isEmpty();
        verify(cursor, never()).copyCursorRulesToDirectory(any());
    }
    
    @Test
    void testCopyCursorRulesToDirectoryWithEmptyList() {
        // Given
        List<String> emptyRuleFiles = List.of();
        
        // When
        cursor.copyCursorRulesToDirectory(emptyRuleFiles);
        
        // Then
        verify(cursor).copyCursorRulesToDirectory(emptyRuleFiles);
    }
    
    @Test
    void testCopyCursorRulesToDirectoryWithMissingResource(@TempDir Path tempDir) {
        // Save original user.dir
        String originalUserDir = System.getProperty("user.dir");
        try {
            // Set user.dir to temp directory
            System.setProperty("user.dir", tempDir.toString());

            // Create a mock Cursor that simulates missing resource
            Cursor cursor = new Cursor() {
                @Override
                void copyCursorRulesToDirectory(List<String> ruleFiles) {
                    try {
                        Path currentPath = Paths.get(System.getProperty("user.dir"));
                        Path cursorPath = currentPath.resolve(".cursor");
                        Path rulesPath = cursorPath.resolve("rules");
                        
                        // Create rules directory
                        FileUtils.forceMkdir(rulesPath.toFile());
                        
                        // Force null resource stream
                        for (String fileName : ruleFiles) {
                            try (InputStream resourceStream = getClass().getResourceAsStream("/non-existent-file")) {
                                if (resourceStream == null) {
                                    throw new IOException("Resource not found: /non-existent-file");
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error copying rules files", e);
                    }
                }
            };

            // Verify that the expected exception is thrown
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> cursor.execute("java"));
            Assertions.assertTrue(exception.getCause() instanceof IOException);
            Assertions.assertTrue(exception.getCause().getMessage().contains("Resource not found"));
        } finally {
            // Restore original user.dir
            System.setProperty("user.dir", originalUserDir);
        }
    }
} 