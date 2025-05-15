package info.jab.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class GitInfoPrinterTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    @DisplayName("Should print 'git.properties not found' when resource stream is null")
    void should_printNotFound_when_gitPropertiesMissing() {
        // Given
        // Use the test-specific constructor to simulate the resource not being found
        GitInfoPrinter printer = new GitInfoPrinter(() -> null);

        // When
        printer.printGitInfo();

        // Then
        assertThat(outputStreamCaptor.toString(StandardCharsets.UTF_8).trim()).isEqualTo("git.properties not found");
    }

    @Test
    @DisplayName("Should print version and commit when git.properties is found")
    void should_printVersionAndCommit_when_gitPropertiesFound() {
        // Given
        // git.properties is expected to be in src/test/resources
        GitInfoPrinter printer = new GitInfoPrinter();

        // When
        printer.printGitInfo();

        // Then
        String expectedOutput = "Version: 1.0.0-test" + System.lineSeparator() +
                                "Commit: test123" + System.lineSeparator() +
                                System.lineSeparator();
        // JColor adds ANSI escape codes, so we need to check for contains, not exact match for the colored parts.
        // However, the actual property values should be exact.
        String actualOutput = outputStreamCaptor.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");

        assertThat(actualOutput).contains("Version: ");
        assertThat(actualOutput).contains("1.0.0-test");
        assertThat(actualOutput).contains("Commit: ");
        assertThat(actualOutput).contains("test123");

        // A more robust check for the plain string parts:
        String plainOutput = actualOutput.replaceAll("\\u001B\\[[;\\d]*m", "");
        assertThat(plainOutput).isEqualTo(expectedOutput);
    }
}
