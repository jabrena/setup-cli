package info.jab.cli.behaviours;

public class SpringCli implements Behaviour0 {

    @Override
    public void execute() {
        System.out.println("sdk install springboot");
        System.out.println("spring init -d=web,actuator,devtools --build=maven --force ./");
        System.out.println("./mvnw clean verify");
    }
}
