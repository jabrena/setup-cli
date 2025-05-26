package info.jab.cli.behaviours;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import info.jab.cli.io.CopyFiles;

@ExtendWith(MockitoExtension.class)
class CursorTest {

    @Mock
    private CopyFiles mockCopyFiles;

    private Cursor cursor;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        cursor = new Cursor(mockCopyFiles);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testExecuteWithInvalidParam() {
        // Given
        String invalidOption = "invalid-option";

        // When & Then
        assertThatThrownBy(() -> cursor.execute(invalidOption))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid parameter: " + invalidOption);
        verify(mockCopyFiles, never()).copyClasspathFolder(anyString(), any(Path.class));
        verify(mockCopyFiles, never()).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());
    }

    @Test
    @SuppressWarnings("NullAway")
    void testExecuteWithNullParam() {
        // Given
        String nullParameter = null;

        // When & Then
        assertThatThrownBy(() -> cursor.execute(nullParameter))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid parameter: null");

        // Should not call any copy methods when exception is thrown
        verify(mockCopyFiles, never()).copyClasspathFolder(anyString(), any(Path.class));
        verify(mockCopyFiles, never()).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());
    }

    @Test
    void testExecuteWithValidJavaParam() {
        // Given
        Mockito.doNothing().when(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());

        // When
        cursor.execute("java");

        // Then
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Cursor rules added successfully");
        verify(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());
    }

    @Test
    void testExecuteWithJavaSpringBootParam() {
        // Given
        Mockito.doNothing().when(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());

        // When
        cursor.execute("spring-boot");

        // Then
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Cursor rules added successfully");
        verify(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());
    }

    @Test
    void testExecuteWithJavaQuarkusParam() {
        // Given
        Mockito.doNothing().when(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());

        // When
        cursor.execute("quarkus");

        // Then
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Cursor rules added successfully");
        verify(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());
    }

    @Test
    void testExecuteWithTasksParam() {
        // Given
        Mockito.doNothing().when(mockCopyFiles).copyClasspathFolder(anyString(), any(Path.class));

        // When
        cursor.execute("tasks");

        // Then
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Cursor rules added successfully");
        verify(mockCopyFiles).copyClasspathFolder(anyString(), any(Path.class));
    }

    @Test
    void testExecuteWithAgileParam() {
        // Given
        Mockito.doNothing().when(mockCopyFiles).copyClasspathFolder(anyString(), any(Path.class));

        // When
        cursor.execute("agile");

        // Then
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Cursor rules added successfully");
        verify(mockCopyFiles).copyClasspathFolder(anyString(), any(Path.class));
    }

    @Test
    void testExecuteWithCopyFailureForJavaParam() {
        // Given
        doThrow(new RuntimeException("Copy operation failed"))
            .when(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());

        // When & Then
        assertThatThrownBy(() -> cursor.execute("java"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Copy operation failed");

        verify(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());

        // Should not print success message when operation fails
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        assertThat(output).doesNotContain("Cursor rules added successfully");
    }

    @Test
    void testExecuteWithCopyFailureForTasksParam() {
        // Given
        doThrow(new RuntimeException("Copy operation failed"))
            .when(mockCopyFiles).copyClasspathFolder(anyString(), any(Path.class));

        // When & Then
        assertThatThrownBy(() -> cursor.execute("tasks"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Copy operation failed");

        verify(mockCopyFiles).copyClasspathFolder(anyString(), any(Path.class));

        // Should not print success message when operation fails
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        assertThat(output).doesNotContain("Cursor rules added successfully");
    }

    @Test
    void testDefaultConstructor() {
        // When
        Cursor cursorWithDefaultConstructor = new Cursor();

        // Then
        assertThat(cursorWithDefaultConstructor).isNotNull();
        // The default constructor should create a Cursor instance that can be used
        // We can't easily test the internal CopyFiles instance, but we can verify
        // that the object is properly constructed
    }

    @Test
    void testExecuteWithCaseInsensitiveParam() {
        // Given
        Mockito.doNothing().when(mockCopyFiles).copyClasspathFolder(anyString(), any(Path.class));

        // When
        cursor.execute("AGILE"); // Test uppercase

        // Then
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Cursor rules added successfully");
        verify(mockCopyFiles).copyClasspathFolder(anyString(), any(Path.class));
    }

    @Test
    void testExecuteWithMixedCaseParam() {
        // Given
        Mockito.doNothing().when(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());

        // When
        cursor.execute("Spring-Boot"); // Test mixed case

        // Then
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim())
            .contains("Cursor rules added successfully");
        verify(mockCopyFiles).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());
    }

    @Test
    void testExecuteWithEmptyStringParam() {
        // Given
        String emptyParameter = "";

        // When & Then
        assertThatThrownBy(() -> cursor.execute(emptyParameter))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid parameter: " + emptyParameter);

        verify(mockCopyFiles, never()).copyClasspathFolder(anyString(), any(Path.class));
        verify(mockCopyFiles, never()).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());
    }

    @Test
    void testExecuteWithWhitespaceParam() {
        // Given
        String whitespaceParameter = "   ";

        // When & Then
        assertThatThrownBy(() -> cursor.execute(whitespaceParameter))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid parameter: " + whitespaceParameter);

        verify(mockCopyFiles, never()).copyClasspathFolder(anyString(), any(Path.class));
        verify(mockCopyFiles, never()).copyClasspathFolderExcludingFiles(anyString(), any(Path.class), anyList());
    }
}
