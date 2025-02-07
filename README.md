# Setup

## How to build in local

```bash
./mvnw clean verify
./mvnw clean package
java -jar ./target/setup-0.1.0.jar init --cursor java
```