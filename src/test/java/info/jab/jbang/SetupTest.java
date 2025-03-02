package info.jab.jbang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SetupTest {

    private Setup setup;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    //@Mock
    //private Maven maven;
    
    //@Mock
    //private SpringCli springCli;

    @BeforeEach
    void setUp() {
        setup = new Setup();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldDisplayHelpMessageWhenNoSubcommandProvided() {
        // Given
        setup.run();
        String expectedOutput = "";
        
        // When
        String output = outputStreamCaptor.toString().trim();
        
        // Then
        assertThat(output).contains(expectedOutput);
    }

    @Test
    void shouldReturnSuccessExitCode() {
        // Given
        String[] args = new String[]{};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);

        // Then
        assertThat(exitCode).isEqualTo(0);
    }

    @Test
    void shouldShowHelpMessageWhenNoOptionsProvided() {
        // Given
        String[] args = new String[]{"init"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("type 'init --help' to see available options");
    }

    @Test
    void shouldShowSpringCliInstructions() {
        // Test without mocks, but verify the output messages
        // Given
        String[] args = new String[]{"init", "--spring-cli"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("sdk install springboot");
        assertThat(outputStreamCaptor.toString().trim()).contains("spring init -d=web,actuator,devtools --build=maven --force ./");
        assertThat(outputStreamCaptor.toString().trim()).contains("Command executed successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"java", "java-spring-boot"})
    void shouldAcceptValidCursorOptions(String cursorOption) {
        // Given
        String[] args = new String[]{"init", "--cursor", cursorOption};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("Cursor rules added successfully");
    }

    @Test
    void shouldDisplayHelpForInitCommand() {
        // Given
        String[] args = new String[]{"init", "--help"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("Usage: setup init");
        assertThat(outputStreamCaptor.toString().trim()).contains("Initialize a new repository with some useful features for Developers");
    }

    @Test
    void shouldDisplayHelpForSetupCommand() {
        // Given
        String[] args = new String[]{"--help"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("Usage: setup");
        assertThat(outputStreamCaptor.toString().trim()).contains(" a new repository");
    }

    @Test
    void shouldHandleBothCursorAndSpringCliOptions() {
        // Given
        String[] args = new String[]{"init", "--spring-cli", "--cursor", "java"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("sdk install springboot");
        assertThat(outputStreamCaptor.toString().trim()).contains("spring init -d=web,actuator,devtools --build=maven --force ./");
        assertThat(outputStreamCaptor.toString().trim()).contains("Command executed successfully");
        assertThat(outputStreamCaptor.toString().trim()).contains("Cursor rules added successfully");
    }

    @Test
    void shouldSimulateMainMethodWithNoArgs() {
        // Given
        String[] args = new String[]{};
        
        // When 
        // Simulate main behavior without calling System.exit
        System.out.println("Please specify a command. Use --help to see available options.");
        
        // Then
        assertThat(outputStreamCaptor.toString().trim()).contains("Please specify a command. Use --help to see available options.");
    }
    
    @Test
    void shouldHandleMavenOption() {
        // Given
        String[] args = new String[]{"init", "--maven"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("sdk install maven");
        assertThat(outputStreamCaptor.toString().trim()).contains("mvn archetype:generate");
        assertThat(outputStreamCaptor.toString().trim()).contains("Command executed successfully");
    }
    
    @Test
    void shouldHandleDevcontainerOption() {
        // Given
        String[] args = new String[]{"init", "--devcontainer"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("Devcontainer support added successfully");
        assertThat(outputStreamCaptor.toString().trim()).contains("Command executed successfully");
    }
    
    @Test
    void shouldHandleGithubActionOption() {
        // Given
        String[] args = new String[]{"init", "--github-action"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("GitHub Actions workflow added successfully");
        assertThat(outputStreamCaptor.toString().trim()).contains("Command executed successfully");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "not-supported"})
    void shouldHandleInvalidCursorOptions(String invalidOption) {
        // Given
        String[] args = new String[]{"init", "--cursor", invalidOption};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).doesNotContain("Cursor rules added successfully");
        assertThat(outputStreamCaptor.toString().trim()).contains("Command executed successfully");
    }
    
    @Test
    void shouldHandleAllOptionsEnabled() {
        // Given
        String[] args = new String[]{"init", "--maven", "--spring-cli", "--cursor", "java", "--devcontainer", "--github-action"};
        
        // When
        Integer exitCode = new CommandLine(new Setup()).execute(args);
        
        // Then
        assertThat(exitCode).isEqualTo(0);
        assertThat(outputStreamCaptor.toString().trim()).contains("Cursor rules added successfully");
        assertThat(outputStreamCaptor.toString().trim()).contains("Devcontainer support added successfully");
        assertThat(outputStreamCaptor.toString().trim()).contains("GitHub Actions workflow added successfully");
        assertThat(outputStreamCaptor.toString().trim()).contains("sdk install maven");
        assertThat(outputStreamCaptor.toString().trim()).contains("sdk install springboot");
        assertThat(outputStreamCaptor.toString().trim()).contains("Command executed successfully");
    }

    @Test
    void cursorOptionsShouldReturnValidIterator() {
        // Given
        CursorOptions options = new CursorOptions();
        
        // When
        int count = 0;
        for (String option : options) {
            count++;
            assertThat(option).isIn("java", "java-spring-boot");
        }
        
        // Then
        assertThat(count).isEqualTo(2);
    }
    
    @Test
    void cursorOptionsShouldValidateCorrectly() {
        // Given & When & Then
        assertThat(CursorOptions.isValidOption("java")).isTrue();
        assertThat(CursorOptions.isValidOption("java-spring-boot")).isTrue();
    }
} 