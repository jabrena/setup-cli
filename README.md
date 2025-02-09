# Setup

A JVM app designed to provided **Cursor rules** to your project.

## How to build in local

```bash
./mvnw clean verify
./mvnw clean package

./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates
```

## How to use the CLI

```bash
java -jar ./target/setup-0.3.0.jar
java -jar ./target/setup-0.3.0.jar --help
java -jar ./target/setup-0.3.0.jar init --cursor java
java -jar ./target/setup-0.3.0.jar init --cursor java-spring-boot
```

## References

- https://www.cursor.com/
- https://docs.cursor.com/context/rules-for-ai
- https://github.com/jabrena/java-cursor-rules
