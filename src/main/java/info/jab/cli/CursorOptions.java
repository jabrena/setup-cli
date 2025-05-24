package info.jab.cli;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

public class CursorOptions implements Iterable<String>{

    public enum CursorOption {
        JAVA("java"),
        JAVA_SPRING_BOOT("java-spring-boot"),
        JAVA_QUARKUS("java-quarkus"),
        TASKS("tasks"),
        AGILE("agile");

        private final String value;

        CursorOption(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static List<String> getOptionValues() {
            return Arrays.stream(CursorOption.values())
                    .map(CursorOption::getValue)
                    .toList();
        }

        public static Optional<CursorOption> fromString(String text) {
            return Arrays.stream(CursorOption.values())
                    .filter(option -> option.value.equalsIgnoreCase(text))
                    .findFirst();
        }
    }

    @Override
    public Iterator<String> iterator() {
        return CursorOption.getOptionValues().iterator();
    }

    /**
     * Checks if the provided parameter is a valid option.
     *
     * @param parameter the parameter to check
     * @return true if the parameter is a valid option, false otherwise
     */
    public static boolean isValidOption(@NonNull String parameter) {
        if (Objects.isNull(parameter)) {
            return false;
        }
        return CursorOption.fromString(parameter).isPresent();
    }

    public static List<String> getOptions() {
        return CursorOption.getOptionValues();
    }
}
