package info.jab.jbang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
class SetupTest {

    @Mock
    private InitCommand mockInitCommand;

    private Setup setupWithMock;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
        setupWithMock = new Setup(mockInitCommand);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testRunWithMockedInitCommand() {
        setupWithMock.run();
        verify(mockInitCommand, times(1)).runInitFeature();
    }
    
    @Test
    void testRunCLINoArgs() {
        int exitCode = Setup.runCLI(new String[]{});
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        
        assertThat(output)
            .contains("Please specify a command. Use --help to see available options.");
        assertThat(exitCode).isZero(); 
    }

    @Test
    void testRunCLIWithInitNoOpts() {
        int exitCode = Setup.runCLI(new String[]{"init"});
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();

        // Verify banner is printed by checking for distinct Figlet text
        assertThat(output).contains("____       _"); 
        
        assertThat(output).contains("type 'init --help' to see available options");
        assertThat(exitCode).isZero(); 
    }

    @Test
    void testRunCLIWithInitValidOpt() {
        int exitCode = Setup.runCLI(new String[]{"init", "-ec"});
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        
        // Verify banner is printed by checking for distinct Figlet text
        assertThat(output).contains("____       _"); 
        
        assertThat(output).contains("EditorConfig support added successfully"); 
        assertThat(output).contains("Command executed successfully"); 
        assertThat(exitCode).isZero(); 
    }

    @Test
    void testRunCLIWithInitHelp() {
        int exitCode = Setup.runCLI(new String[]{"init", "--help"});
        String output = outputStreamCaptor.toString(StandardCharsets.UTF_8).trim();
        
        // Verify banner is printed by checking for distinct Figlet text
        assertThat(output).contains("____       _");
        
        assertThat(output).contains("Usage: setup init"); 
        assertThat(exitCode).isZero(); 
    }
} 