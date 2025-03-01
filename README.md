# Setup

A JVM app designed to provided **Cursor rules** to your project.

## How to build in local

```bash
./load-remove-git-submodules.sh c
./load-remove-git-submodules.sh r

./mvnw clean verify
./mvnw clean package
./mvnw clean package -DskipTests

./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates
./mvnw versions:display-property-updates
```

## How to use the CLI

```bash
java -jar ./target/setup-0.4.0.jar
java -jar ./target/setup-0.4.0.jar --help
java -jar ./target/setup-0.4.0.jar init
java -jar ./target/setup-0.4.0.jar init --help
java -jar ./target/setup-0.4.0.jar init --cursor java
java -jar ./target/setup-0.4.0.jar init --spring-cli true
jar tf ./target/setup-0.4.0.jar
```

## How to use from Jbang

```bash
jbang cache clear
jbang catalog list jabrena
jbang setup@jabrena
```

## References

- https://www.cursor.com/
- https://docs.cursor.com/context/rules-for-ai
- https://github.com/jabrena/java-cursor-rules
- https://github.com/jabrena/jbang-catalog

### Developer links

- https://picocli.info/
- https://sdkman.io/
- https://sdkman.io/usage/#env-command
- https://maven.apache.org/guides/index.html
- https://docs.spring.io/spring-boot/cli/index.html
- https://keepachangelog.com/en/1.1.0/
