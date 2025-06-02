# Setup CLI

Setup is a command line utility designed to help developers when initializing new projects using Maven.

[![CI Builds](https://github.com/jabrena/setup-cli/actions/workflows/maven.yaml/badge.svg)](https://github.com/jabrena/setup-cli/actions/workflows/maven.yaml)

![](./docs/setup-cli-screenshot.png)

## Motivation

**Time is precious in software development**, and setting up a new repository shouldn't consume hours of your productive time. Every new project requires the same tedious setup tasks: configuring build systems, setting up CI/CD pipelines, adding development environment configurations, choosing Java versions, and more.

When starting a new repository, you face familiar challenges: creating a Maven project (but who remembers the complete archetype command?), revisiting the classic [Maven in 5 minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) guide, or memorizing framework-specific options for [Spring Boot](https://docs.spring.io/spring-boot/cli/index.html) or [Quarkus](https://quarkus.io/blog/quarkus-cli/). Why not start with sensible defaults and customize the **pom.xml** later?

These setup tasks aren't complex, but they are genuinely tedious. Whether you build from scratch or copy from previous projects, you typically lose at least an hour during repository onboarding.

This tool streamlines the process by providing battle-tested defaults for Devcontainers, GitHub Actions, `.editorconfig`, `.sdkmanrc`, and more—all ready to use with a simple `jbang setup@jabrena init` command. You might discover configurations you didn't even know you needed.

**If you value efficient project onboarding**, Setup CLI is designed for developers like you.

---

**WIP:** Currently, I am working in some features that every Software engineer doesn´t need to do in the beginning but they are good practice to avoid other issues later like Memory leaks. For that purpose, the tool is working to add value around tools like [Java Mission Control, JMC](https://www.oracle.com/java/technologies/jdk-mission-control.html) & [VisualVM](https://visualvm.github.io/).

## Getting started

This command line tool was designed to be executed in combination **Jbang** a nice way to develop small programs like this. To use Setup, you will need to have installed JBang first, so how to install JBang?

```bash
sdk install jbang
```

Once you have instaled **JBang**, I recommend to clear the cache to use latest version of setup:

```bash
jbang cache clear
jbang catalog list jabrena
```

After this quick operation, it is your turn!

```