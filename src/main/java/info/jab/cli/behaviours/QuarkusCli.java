package info.jab.cli.behaviours;

public class QuarkusCli implements Behaviour0 {

    private String commands = """
            sdk install quarkus
            quarkus create app
            ./mvnw clean verify
            """;

    @Override
    public void execute() {
        commands.lines().forEach(System.out::println);
    }
}
