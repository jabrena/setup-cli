package info.jab.cli.behaviours;

import io.vavr.control.Either;

public class SpringCli implements Behaviour0 {

    private String commands = """
            sdk install springboot
            spring init -d=web,actuator,devtools --build=maven --force ./
            ./mvnw clean verify
            """;

    @Override
    public Either<String, String> execute() {
        commands.lines().forEach(System.out::println);
        return Either.right("Spring CLI command completed successfully");
    }
}
