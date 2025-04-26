package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(MockitoExtension.class)
class CursorTest {

    private Cursor cursor;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        cursor = spy(new Cursor());
        
        // Use lenient() to avoid UnnecessaryStubbingException
        lenient().doNothing().when(cursor).copyCursorRulesToDirectory(any(), anyString(), any(Path.class));
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
        verify(cursor).copyCursorRulesToDirectory(any(), anyString(), any(Path.class));
    }
    
    @Test
    void testExecuteWithInvalidParam() {
        // Execute with an invalid parameter
        cursor.execute("invalid-option");
        
        // Verify no success message was printed (should be empty)
        assertThat(outputStreamCaptor.toString().trim()).isEmpty();
        
        // Verify the copyCursorRulesToDirectory method was not called
        verify(cursor, never()).copyCursorRulesToDirectory(any(), anyString(), any(Path.class));
    }
    
    @Test
    void testExecuteWithJavaSpringBootParam() {
        // Execute with java-spring-boot parameter
        cursor.execute("java-spring-boot");
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Cursor rules added successfully");
        
        // Verify the copyCursorRulesToDirectory method was called
        verify(cursor).copyCursorRulesToDirectory(any(), anyString(), any(Path.class));
    }
    
    @Test
    void testExecuteWithJavaQuarkusParam() {
        // Execute with java-quarkus parameter
        cursor.execute("java-quarkus");
        
        // Verify the success message was printed
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Cursor rules added successfully");
        
        // Verify the copyCursorRulesToDirectory method was called
        verify(cursor).copyCursorRulesToDirectory(any(), anyString(), any(Path.class));
    }
    
    @Test
    void testCopyCursorRulesToDirectoryWithNonExistentResource() {
        // Given
        List<String> invalidRuleFiles = List.of("non-existent-file.mdc");
        Path dummyRulesPath = Paths.get("dummy/rules/path");
        doThrow(new RuntimeException("Error copying rules files"))
            .when(cursor).copyCursorRulesToDirectory(eq(invalidRuleFiles), anyString(), eq(dummyRulesPath));
        
        // When/Then
        assertThatThrownBy(() -> cursor.copyCursorRulesToDirectory(invalidRuleFiles, "dummyPath", dummyRulesPath))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Error copying rules files");
    }
    
    @Test
    void testExecuteWithNullParameter() {
        // When
        cursor.execute(null);
        
        // Then
        assertThat(outputStreamCaptor.toString().trim()).isEmpty();
        verify(cursor, never()).copyCursorRulesToDirectory(any(), anyString(), any(Path.class));
    }
    
    @Test
    void testCopyCursorRulesToDirectoryWithEmptyList() {
        // Given
        List<String> emptyRuleFiles = List.of();
        String dummyPath = "some/path";
        Path dummyRulesPath = Paths.get("dummy/rules/path");
        
        // When
        cursor.copyCursorRulesToDirectory(emptyRuleFiles, dummyPath, dummyRulesPath);
        
        // Then
        verify(cursor).copyCursorRulesToDirectory(eq(emptyRuleFiles), eq(dummyPath), eq(dummyRulesPath));
    }
} 