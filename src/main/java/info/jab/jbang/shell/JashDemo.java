package info.jab.jbang.shell;

import static dev.jbang.jash.Jash.*;

public class JashDemo {
 
    private final String commands = """
        mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false
        """;

    public void execute() {
        commands.lines().flatMap(command -> $(command).stream()).forEach(System.out::println);
    }

}
