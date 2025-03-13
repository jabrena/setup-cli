package info.jab.jbang.behaviours;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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
} 