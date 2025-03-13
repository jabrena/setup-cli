package info.jab.jbang;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class CursorOptions implements Iterable<String> {
    private static final List<String> OPTIONS = List.of("java", "java-spring-boot");
    
    @Override
    public Iterator<String> iterator() {
        return OPTIONS.iterator();
    }
    
    /**
     * Checks if the provided parameter is a valid option.
     *
     * @param parameter the parameter to check
     * @return true if the parameter is a valid option, false otherwise
     */
    public static boolean isValidOption(String parameter) {
        if (Objects.isNull(parameter)) {
            return false;
        }
        return OPTIONS.contains(parameter);
    }
}
