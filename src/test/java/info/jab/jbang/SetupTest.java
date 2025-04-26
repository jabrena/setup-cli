package info.jab.jbang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SetupTest {

    @Mock
    private InitCommand mockInitCommand;

    private Setup setup;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(outputStreamCaptor));
        // Inject the mocked InitCommand
        setup = new Setup(mockInitCommand);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Disabled("This test is not implemented yet")
    @Test
    void testWithMockedInitCommand() {
        // Set up expectations for the mock
        when(mockInitCommand.runInitFeature()).thenReturn("0");
        
        // Execute the method being tested
        setup.run();
        
        // Verify the mock was called exactly once
        verify(mockInitCommand, times(1)).runInitFeature();
        
        // Verify output contains expected text
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Setup is a CLI utility designed to help developers when they start working with a new repository.");
    }
    
    @Disabled("This test is not implemented yet")
    @Test
    void testDefaultConstructor() {
        // Create a setup with the default constructor
        Setup setupDefault = new Setup();
        
        // Execute the run method
        setupDefault.run();
        
        // Verify expected output
        assertThat(outputStreamCaptor.toString().trim())
            .contains("Setup is a CLI utility designed to help developers when they start working with a new repository.");
    }
} 