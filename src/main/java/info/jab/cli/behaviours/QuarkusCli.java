package info.jab.cli.behaviours;

import io.vavr.control.Either;

public class QuarkusCli implements Behaviour0 {

    private String commands = """
            sdk install quarkus
            quarkus create app
            ./mvnw clean verify
            """;

    @Override
    public Either<String, String> execute() {
        commands.lines().forEach(System.out::println);
        return Either.right("Quarkus CLI command completed successfully");
    }
}
