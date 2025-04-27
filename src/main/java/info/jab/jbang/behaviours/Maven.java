package info.jab.jbang.behaviours;

public class Maven implements Behaviour0 {
    
    private String commands = """
            mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false
            mv maven-demo/* maven-demo/.* ./ 2>/dev/null || true
            rmdir maven-demo
            mvn wrapper:wrapper
            ./mvnw clean verify
            """;

    @Override
    public void execute() {
        commands.lines().forEach(System.out::println);
    }
}
