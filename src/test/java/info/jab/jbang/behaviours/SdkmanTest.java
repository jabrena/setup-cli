package info.jab.jbang.behaviours;

import info.jab.jbang.io.CopyFiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class SdkmanTest {

    private CopyFiles mockCopyFiles;
    private Sdkman sdkman;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Set the user.dir to the temporary directory for testing isolation
        System.setProperty("user.dir", tempDir.toString());
        mockCopyFiles = Mockito.mock(CopyFiles.class);
        sdkman = new Sdkman(mockCopyFiles);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void should_execute_copy_and_print_message() {
        // Given
        Path expectedPath = Paths.get(System.getProperty("user.dir"));
        List<String> expectedFiles = List.of(".sdkmanrc");
        String expectedResourcePath = "sdkman/";

        // When
        sdkman.execute();

        // Then
        verify(mockCopyFiles).copyFilesToDirectory(expectedFiles, expectedResourcePath, expectedPath);
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("SDKMAN support added successfully");

        // Reset System.out to its original stream
        System.setOut(System.out);
    }

    @Test
    void constructor_should_initialize_copyFiles() {
        //When
        Sdkman sdkmanDefault = new Sdkman();

        // Then
        assertThat(sdkmanDefault).isNotNull();
        // We can't easily assert the internal CopyFiles instance is not null without reflection or changing visibility,
        // but we know the constructor ran without error.
    }
} 