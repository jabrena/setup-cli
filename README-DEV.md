# Developer notes

## How to build in local

```bash
sdk env install

# Setup cli
./mvnw dependency:tree
./mvnw dependency:resolve
./mvnw clean validate -U
./mvnw buildplan:list-phase
./mvnw license:third-party-report
jwebserver -p 8001 -d "$(pwd)/target/reports/"
./mvnw clean verify
./mvnw clean verify -DENABLE_INTEGRATION_TESTS=true
jwebserver -p 8001 -d "$(pwd)/target/timeline/"
./mvnw clean test surefire-report:report -Dmaven.test.failure.ignore=true
jwebserver -p 8004 -d "$(pwd)/target/reports"
./mvnw clean verify jacoco:report -Pjacoco
jwebserver -p 8004 -d "$(pwd)/target/site/jacoco"
./mvnw clean verify -Ppitest
./mvnw clean verify -Psecurity

./mvnw versions:display-property-updates
./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates

./mvnw versions:set -DnewVersion=0.11.0
./mvnw versions:commit
```

## How to use the CLI

```bash
./mvnw clean package
java -jar ./target/setup-0.11.0.jar
java -jar ./target/setup-0.11.0.jar --help
java -jar ./target/setup-0.11.0.jar init
java -jar ./target/setup-0.11.0.jar init --help
java -jar ./target/setup-0.11.0.jar init --devcontainer
java -jar ./target/setup-0.11.0.jar init --cursor https://github.com/jabrena/cursor-rules-java
java -jar ./target/setup-0.11.0.jar init --cursor https://github.com/jabrena/cursor-rules-agile
java -jar ./target/setup-0.11.0.jar init --cursor https://github.com/snarktank/ai-dev-tasks .
java -jar ./target/setup-0.11.0.jar init --maven
java -jar ./target/setup-0.11.0.jar init --spring-boot
java -jar ./target/setup-0.11.0.jar init --quarkus
java -jar ./target/setup-0.11.0.jar init --github-action
java -jar ./target/setup-0.11.0.jar init --editorconfig
java -jar ./target/setup-0.11.0.jar init --sdkman
java -jar ./target/setup-0.11.0.jar init --visualvm
java -jar ./target/setup-0.11.0.jar init --jmc
java -jar ./target/setup-0.11.0.jar init --gitignore
jar tf ./target/setup-0.11.0.jar
```

## Release process

```bash
git tag -l --sort=-creatordate --format='%(refname:short) - %(creatordate:format:%d/%m/%Y)'
```

## Developer references

- https://keepachangelog.com/en/1.1.0/
- https://picocli.info/
- https://www.eclemma.org/jacoco/trunk/doc/maven.html
- https://github.com/zeroturnaround/zt-exec
- https://projects.eclipse.org/projects/technology.jgit