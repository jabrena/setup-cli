package info.jab.cli;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CursorOptions implements Iterable<String>{

    public enum CursorOption {
        JAVA("java"),
        SPRING_BOOT("spring-boot"),
        QUARKUS("quarkus"),
        TASKS("tasks"),
        AGILE("agile");

        private final String value;

        // Static final list computed once for better performance
        private static final List<String> OPTION_VALUES = Arrays.stream(CursorOption.values())
                .map(CursorOption::getValue)
                .toList();

        CursorOption(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static List<String> getOptionValues() {
            return OPTION_VALUES;
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
    public static boolean isValidOption(@Nullable String parameter) {
        if (Objects.isNull(parameter)) {
            return false;
        }
        return CursorOption.fromString(parameter).isPresent();
    }

    public static List<String> getOptions() {
        return CursorOption.getOptionValues();
    }
}
