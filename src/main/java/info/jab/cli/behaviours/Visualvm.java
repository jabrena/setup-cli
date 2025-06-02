package info.jab.cli.behaviours;

import io.vavr.control.Either;

public class Visualvm implements Behaviour0 {

    private String commands = """
            sdk install visualvm
            visualvm
            """;

    @Override
    public Either<String, String> execute() {
        commands.lines().forEach(System.out::println);
        return Either.right("VisualVM command completed successfully");
    }
}
