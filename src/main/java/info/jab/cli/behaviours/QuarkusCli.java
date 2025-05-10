package info.jab.cli.behaviours;

public class QuarkusCli implements Behaviour0 {

    @Override
    public void execute() {
        System.out.println("sdk install quarkus");
        System.out.println("quarkus create app");
        System.out.println("./mvnw clean verify");
    }
}
