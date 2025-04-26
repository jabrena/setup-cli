package info.jab.jbang.shell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class JashDemoTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }
    
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Disabled("TODO: Fix this test")
    @Test
    void shouldExecuteMavenCommand() {
        // Given
        JashDemo jashDemo = new JashDemo();

        // When
        jashDemo.execute();

        // Then
        assertThat(outputStreamCaptor.toString().trim()).isNotEmpty();
    }
} 