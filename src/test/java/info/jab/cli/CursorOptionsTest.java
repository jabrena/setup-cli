package info.jab.cli;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class CursorOptionsTest {

    @Test
    void testIterator() {
        // Given
        CursorOptions cursorOptions = new CursorOptions();
        List<String> expectedValues = List.of("java", "java-spring-boot", "java-quarkus", "tasks");

        // When
        Iterator<String> iterator = cursorOptions.iterator();

        // Then
        List<String> values = new ArrayList<>();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }
        assertThat(values).containsExactlyInAnyOrderElementsOf(expectedValues);
    }

    @Test
    void testIsValidOption() {
        // Given
        String validJava = "java";
        String validSpringBoot = "java-spring-boot";
        String validQuarkus = "java-quarkus";
        String validTasks = "tasks";
        String invalidOption = "invalid-option";
        String emptyOption = "";

        // When
        //  Then
        assertThat(CursorOptions.isValidOption(validJava)).isTrue();
        assertThat(CursorOptions.isValidOption(validSpringBoot)).isTrue();
        assertThat(CursorOptions.isValidOption(validQuarkus)).isTrue();
        assertThat(CursorOptions.isValidOption(validTasks)).isTrue();
        assertThat(CursorOptions.isValidOption(invalidOption)).isFalse();
        assertThat(CursorOptions.isValidOption(emptyOption)).isFalse();
    }
}