package info.jab.cli.behaviours;

import io.vavr.control.Either;

public class JMC implements Behaviour0 {

    private String commands = """
            sdk install jmc
            jmc
            """;

    @Override
    public Either<String, String> execute() {
        commands.lines().forEach(System.out::println);
        return Either.right("JMC command completed successfully");
    }
}
