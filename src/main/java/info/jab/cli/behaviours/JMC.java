package info.jab.cli.behaviours;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Either;

public class JMC implements Behaviour0 {

    private static final Logger logger = LoggerFactory.getLogger(JMC.class);

    private String commands = """
            sdk install jmc
            sdk install java 21.0.2-graalce
            sdk default java 21.0.2-graalce
            jmc
            """;

    @Override
    public Either<String, String> execute() {

        logger.info("Executing command to add JMC support");
        logger.info("2025-06-02: SDKMAN only supports a JMC version for Java 21.0.2-graalce");
        commands.lines().forEach(System.out::println);

        return Either.right("Command execution completed successfully");
    }
}
