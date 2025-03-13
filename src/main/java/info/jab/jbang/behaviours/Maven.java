package info.jab.jbang.behaviours;

public class Maven implements Behaviour0 {
    
    @Override
    public void execute() {
        System.out.println("sdk install maven");
        System.out.println("mvn archetype:generate -DgroupId=info.jab.demo -DartifactId=maven-demo -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.5 -DinteractiveMode=false");
        System.out.println("mvn wrapper:wrapper");
        System.out.println("./mvnw clean verify");
    }
}
