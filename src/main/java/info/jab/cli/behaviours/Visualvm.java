package info.jab.cli.behaviours;

public class Visualvm implements Behaviour0 {

    private String commands = """
            sdk install visualvm
            sdk install java 21.0.2-graalce
            sdk default java 21.0.2-graalce
            visualvm
            """;

    @Override
    public void execute() {
        commands.lines().forEach(System.out::println);
    }
}
