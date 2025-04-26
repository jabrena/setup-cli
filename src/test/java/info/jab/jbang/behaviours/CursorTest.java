package info.jab.jbang.behaviours;

import info.jab.jbang.io.CopyFiles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
class CursorTest {

    @Mock
    private CopyFiles mockCopyFiles;

    private Cursor cursor;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        cursor = new Cursor(mockCopyFiles);
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    void testExecuteWithValidJavaParam() {
        Mockito.doNothing().when(mockCopyFiles).copyFilesToDirectory(any(), anyString(), any(Path.class));

        cursor.execute("java");

        assertThat(outputStreamCaptor.toString().trim())
            .contains("Cursor rules added successfully");

        Mockito.verify(mockCopyFiles).copyFilesToDirectory(any(), eq("cursor-rules-java/"), any(Path.class));
    }
    
    @Test
    void testExecuteWithInvalidParam() {
        cursor.execute("invalid-option");

        assertThat(outputStreamCaptor.toString().trim()).isEmpty();

        Mockito.verify(mockCopyFiles, never()).copyFilesToDirectory(any(), anyString(), any(Path.class));
    }
    
    @Test
    void testExecuteWithJavaSpringBootParam() {
        Mockito.doNothing().when(mockCopyFiles).copyFilesToDirectory(any(), anyString(), any(Path.class));

        cursor.execute("java-spring-boot");

        assertThat(outputStreamCaptor.toString().trim())
            .contains("Cursor rules added successfully");

        Mockito.verify(mockCopyFiles).copyFilesToDirectory(any(), eq("cursor-rules-java/"), any(Path.class));
    }
    
    @Test
    void testExecuteWithJavaQuarkusParam() {
        Mockito.doNothing().when(mockCopyFiles).copyFilesToDirectory(any(), anyString(), any(Path.class));

        cursor.execute("java-quarkus");

        assertThat(outputStreamCaptor.toString().trim())
            .contains("Cursor rules added successfully");

        Mockito.verify(mockCopyFiles).copyFilesToDirectory(any(), eq("cursor-rules-java/"), any(Path.class));
    }
    
    @Test
    void testExecuteWithNullParameter() {
        cursor.execute(null);

        assertThat(outputStreamCaptor.toString().trim()).isEmpty();
        Mockito.verify(mockCopyFiles, never()).copyFilesToDirectory(any(), anyString(), any(Path.class));
    }
} 