# Setup CLI

Setup is a command line utility designed to help developers when initializing new projects using Maven.

[![CI Builds](https://github.com/jabrena/setup-cli/actions/workflows/maven.yaml/badge.svg)](https://github.com/jabrena/setup-cli/actions/workflows/maven.yaml)

![](./docs/setup-cli-screenshot.png)

## Motivation

**I hate wasting time in general**, but when you start working in a new repository you always need to do some boring and manual stuff to setup your developer environment like Build Systems, Pipelines, Cursor rules, What Java version I will use and others...

With this idea in mind, I developed Setup. When you begin with a new repository, what do you need? Create a Maven project, but you don´t remember the the whole maven archetype command or why I need to visit that I start with Non JVM framework developer and I have to read the classical article about [Maven in 5 minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html) or when you develop something with a JVM Framework like [Spring Boot](https://docs.spring.io/spring-boot/cli/index.html) or [Quarkus](https://quarkus.io/blog/quarkus-cli/), why I need to memorize all options? Lets begin and later touch the **pom.xml** to customize the behaviour.

For this kind of stuff that they are not complex, but they are tedious honestly, you could do from scratch or you could copy from a previous project but at the end, in that repository onboarding, you are losing in the best case, one hour.

Using the tool, you could add some interesting defaults like Devcontainers, Github Actions (If you use), .editorconfig, .sdkmanrc that the are ready to use with a simple `jbang setup@jabrena init` execution and maybe you know or maybe not, but the tool provides.

**So why not save some time in the onboarding?** This is the reason that I created Setup.

The tool is working in some features that every Software engineer doesn´t need to do in the beginning but they are good practice to avoid other issues like Memory leaks, for that purpose, the tool is working to offer some value around [Java Mission Control, JMC](https://www.oracle.com/java/technologies/jdk-mission-control.html) & [VisualVM](https://visualvm.github.io/). Verify your app before production.

## Getting started

Setup is Command line tool packaged using Jbang a nice way to develop small programs like this. To use Setup, you will need to have installed JBang first, so how to install JBang?

```bash
sdk install jbang
```

Once you have instaled **JBang**, you can start using this tool.

I recommend to clear the cache to use latest version of setup:

```bash
jbang cache clear
jbang catalog list jabrena
````

After this quick process, please use the tool:

```bash
jbang setup@jabrena init --help
jbang setup@jabrena init --cursor https://github.com/jabrena/cursor-rules-java
jbang setup@jabrena init --cursor https://github.com/jabrena/cursor-rules-agile
jbang setup@jabrena init --cursor https://github.com/snarktank/ai-dev-tasks .
jbang setup@jabrena init --maven
jbang setup@jabrena init --spring-boot
jbang setup@jabrena init --quarkus
jbang setup@jabrena init --editorconfig
jbang setup@jabrena init --sdkman
jbang setup@jabrena init --github-action
jbang setup@jabrena init --devcontainer
```

With any doubt, you can create an issue here: https://github.com/jabrena/setup-cli/issues

## How to contribute

Review this [document](./README-DEV.md)

## References

- https://maven.apache.org/guides/index.html
- https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html
- https://docs.spring.io/spring-boot/cli/index.html
- https://quarkus.io/blog/quarkus-cli/
- https://editorconfig.org/
- https://sdkman.io/
- https://sdkman.io/usage/#env-command
- https://sdkman.io/sdks/jmc
- https://sdkman.io/sdks/visualvm
- https://github.com/features/actions
- https://visualvm.github.io/
- https://www.oracle.com/java/technologies/jdk-mission-control.html
- https://containers.dev/

- https://www.cursor.com/
- https://docs.cursor.com/context/rules-for-ai
- ...
- https://github.com/jabrena/cursor-rules-methodology
- https://github.com/jabrena/cursor-rules-agile
- https://github.com/jabrena/cursor-rules-java
- https://github.com/jabrena/cursor-rules-examples
- https://github.com/jabrena/101-cursor
- https://github.com/jabrena/setup-cli
- https://github.com/jabrena/jbang-catalog

Powered by [Cursor](https://www.cursor.com/)
