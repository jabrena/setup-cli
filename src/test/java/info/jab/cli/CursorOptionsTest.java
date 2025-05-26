package info.jab.cli;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class CursorOptionsTest {

    @Test
    void testIterator() {
        // Given
        CursorOptions cursorOptions = new CursorOptions();
        List<String> expectedValues = List.of("java", "spring-boot", "quarkus", "tasks", "agile");

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
        String validSpringBoot = "spring-boot";
        String validQuarkus = "quarkus";
        String validTasks = "tasks";
        String validAgile = "agile";
        String invalidOption = "invalid-option";
        String emptyOption = "";

        // When
        //  Then
        assertThat(CursorOptions.isValidOption(validJava)).isTrue();
        assertThat(CursorOptions.isValidOption(validSpringBoot)).isTrue();
        assertThat(CursorOptions.isValidOption(validQuarkus)).isTrue();
        assertThat(CursorOptions.isValidOption(validTasks)).isTrue();
        assertThat(CursorOptions.isValidOption(validAgile)).isTrue();
        assertThat(CursorOptions.isValidOption(invalidOption)).isFalse();
        assertThat(CursorOptions.isValidOption(emptyOption)).isFalse();
    }
}