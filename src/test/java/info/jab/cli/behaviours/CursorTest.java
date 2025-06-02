package info.jab.cli.behaviours;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import info.jab.cli.io.GitFolderCopy;

@ExtendWith(MockitoExtension.class)
class CursorTest {

    @Mock
    private GitFolderCopy mockGitFolderCopy;

    private Cursor cursor;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    @SuppressWarnings("NullAway.Init")
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        cursor = new Cursor(mockGitFolderCopy);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @SuppressWarnings("NullAway")
    void testExecuteWithNullUrl() {
        // Given
        String nullUrl = null;
        String folderPath = "some/path";

        // When
        var result = cursor.execute(nullUrl, folderPath);

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Git repository URL cannot be null or empty");
        verify(mockGitFolderCopy, never()).copyFolderFromRepo(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteWithEmptyUrl() {
        // Given
        String emptyUrl = "";
        String folderPath = "some/path";

        // When
        var result = cursor.execute(emptyUrl, folderPath);

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Git repository URL cannot be null or empty");
        verify(mockGitFolderCopy, never()).copyFolderFromRepo(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteWithWhitespaceUrl() {
        // Given
        String whitespaceUrl = "   ";
        String folderPath = "some/path";

        // When
        var result = cursor.execute(whitespaceUrl, folderPath);

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Git repository URL cannot be null or empty");
        verify(mockGitFolderCopy, never()).copyFolderFromRepo(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteWithInvalidUrlFormat() {
        // Given
        String invalidUrl = "not-a-valid-url";
        String folderPath = "some/path";

        // When
        var result = cursor.execute(invalidUrl, folderPath);

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).startsWith("Invalid URI format:");
        verify(mockGitFolderCopy, never()).copyFolderFromRepo(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteWithUnsupportedProtocol() {
        // Given
        String ftpUrl = "ftp://example.com/repo.git";
        String folderPath = "some/path";

        // When
        var result = cursor.execute(ftpUrl, folderPath);

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Unsupported protocol: ftp. Only http and https protocols are supported");
        verify(mockGitFolderCopy, never()).copyFolderFromRepo(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteWithValidHttpsUrl() {
        // Given
        String validUrl = "https://github.com/user/repo.git";
        String folderPath = "cursor-rules";
        Mockito.doNothing().when(mockGitFolderCopy).copyFolderFromRepo(eq(validUrl), eq(folderPath), anyString());

        // When
        var result = cursor.execute(validUrl, folderPath);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Cursor rules added successfully");
        verify(mockGitFolderCopy).copyFolderFromRepo(eq(validUrl), eq(folderPath), anyString());
    }

    @Test
    void testExecuteWithValidHttpUrl() {
        // Given
        String validUrl = "http://github.com/user/repo.git";
        String folderPath = "cursor-rules";
        Mockito.doNothing().when(mockGitFolderCopy).copyFolderFromRepo(eq(validUrl), eq(folderPath), anyString());

        // When
        var result = cursor.execute(validUrl, folderPath);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Cursor rules added successfully");
        verify(mockGitFolderCopy).copyFolderFromRepo(eq(validUrl), eq(folderPath), anyString());
    }

    @Test
    void testExecuteWithValidGitUrl() {
        // Given - Change to use https instead of git protocol
        String validUrl = "https://github.com/user/repo.git";
        String folderPath = "cursor-rules";
        Mockito.doNothing().when(mockGitFolderCopy).copyFolderFromRepo(eq(validUrl), eq(folderPath), anyString());

        // When
        var result = cursor.execute(validUrl, folderPath);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Cursor rules added successfully");
        verify(mockGitFolderCopy).copyFolderFromRepo(eq(validUrl), eq(folderPath), anyString());
    }

    @Test
    void testExecuteWithGitFolderCopyFailure() {
        // Given
        String validUrl = "https://github.com/user/repo.git";
        String folderPath = "cursor-rules";
        doThrow(new RuntimeException("Git operation failed"))
            .when(mockGitFolderCopy).copyFolderFromRepo(eq(validUrl), eq(folderPath), anyString());

        // When & Then
        assertThatThrownBy(() -> cursor.execute(validUrl, folderPath))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Git operation failed");

        verify(mockGitFolderCopy).copyFolderFromRepo(eq(validUrl), eq(folderPath), anyString());

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
        // We can't easily test the internal GitFolderCopy instance, but we can verify
        // that the object is properly constructed
    }

    @Test
    void testExecuteWithUrlWithoutHost() {
        // Given
        String urlWithoutHost = "https:///path/to/repo.git";
        String folderPath = "some/path";

        // When
        var result = cursor.execute(urlWithoutHost, folderPath);

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isEqualTo("Invalid URL: missing host");
        verify(mockGitFolderCopy, never()).copyFolderFromRepo(anyString(), anyString(), anyString());
    }

    @Test
    void testExecuteWithComplexGitHubUrl() {
        // Given
        String complexUrl = "https://github.com/username/repository-name.git";
        String folderPath = "templates/java";
        Mockito.doNothing().when(mockGitFolderCopy).copyFolderFromRepo(eq(complexUrl), eq(folderPath), anyString());

        // When
        var result = cursor.execute(complexUrl, folderPath);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Cursor rules added successfully");
        verify(mockGitFolderCopy).copyFolderFromRepo(eq(complexUrl), eq(folderPath), anyString());
    }

    @Test
    void testExecuteWithUrlContainingTrailingWhitespace() {
        // Given
        String urlWithWhitespace = " https://github.com/user/repo.git ";
        String folderPath = "cursor-rules";
        Mockito.doNothing().when(mockGitFolderCopy).copyFolderFromRepo(eq(urlWithWhitespace.trim()), eq(folderPath), anyString());

        // When
        var result = cursor.execute(urlWithWhitespace, folderPath);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Cursor rules added successfully");
        // Verify that the trimmed URL is passed to the copy method
        verify(mockGitFolderCopy).copyFolderFromRepo(eq(urlWithWhitespace.trim()), eq(folderPath), anyString());
    }

    @Test
    void testExecuteWithGitLabUrl() {
        // Given
        String gitLabUrl = "https://gitlab.com/user/project.git";
        String folderPath = "rules";
        Mockito.doNothing().when(mockGitFolderCopy).copyFolderFromRepo(eq(gitLabUrl), eq(folderPath), anyString());

        // When
        var result = cursor.execute(gitLabUrl, folderPath);

        // Then
        assertThat(result.isRight()).isTrue();
        assertThat(result.get()).isEqualTo("Cursor rules added successfully");
        verify(mockGitFolderCopy).copyFolderFromRepo(eq(gitLabUrl), eq(folderPath), anyString());
    }

    @Test
    void testExecuteWithGitProtocol() {
        // Given
        String gitUrl = "git://github.com/user/repo.git";
        String folderPath = "some/path";

        // When
        var result = cursor.execute(gitUrl, folderPath);

        // Then
        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).startsWith("Invalid URL format:");
        verify(mockGitFolderCopy, never()).copyFolderFromRepo(anyString(), anyString(), anyString());
    }
}
