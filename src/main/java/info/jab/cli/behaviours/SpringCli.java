package info.jab.cli.behaviours;

public class SpringCli implements Behaviour0 {

    private String commands = """
            sdk install springboot
            spring init -d=web,actuator,devtools --build=maven --force ./
            ./mvnw clean verify
            """;

    @Override
    public void execute() {
        commands.lines().forEach(System.out::println);
    }
}
