package info.jab.cli.behaviours;

import io.vavr.control.Either;

public class JMC implements Behaviour0 {

    private String commands = """
            sdk install jmc
            sdk install java 21.0.2-graalce
            sdk default java 21.0.2-graalce
            jmc
            """;

    @Override
    public Either<String, String> execute() {
        commands.lines().forEach(System.out::println);
        return Either.right("JMC command completed successfully");
    }
}
