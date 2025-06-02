# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.11.0] 2025/06/03

### Added

- Dependabot support
- Added capacity to execute shell commands
- Maven option creates a maven project from scratch
- Spring-boot options create a maven project from scratch
- Quarkus options create a maven project from scratch

### Changed

- Cursor option changed the signature and now it is possible to fetch any Http Git repository, Example: jbang setup@jabrena init --cursor https://github.com/jabrena/cursor-rules-java
- Removed git submodules, mainly usedd for the previous approach for Cursor rules
- Refactored the approach for resources, now using the universal maven layout

## [0.10.0] 2025/05/30

### Added

- Added support for .gitignore
- Added support for JMC
- Added support for Visualvm
- Added JSpecify support
- Added PiTest support as Profile
- Added Depenency check as Profile
- Added Security pipeline

### Changed

- Better copy file operations with NIO
- Upgraded .sdkmanrc
- Renamed cursor options for Spring-Boot & Quarkus (Better for the users)
- Refactored submodule script
- Updated Cursor rules for Java
- Updated Cursor rules for Agile

## [0.9.0] 2025/05/13

### Added

- Added initial cursor rules for Agile.

## [0.8.0] 2025/05/12

### Added

- Added support for JSpecify
- Added flatten-maven-plugin in the build
- Added git-commit-id-plugin in the build
- Added Version & Commit in Banner

### Changed

- Improved .editorconfig support
- Moved development to the package info.jab.cli
- Moved jacoco to specific profile -Pjacoco

## [0.7.0] 2025/04/27

### Added

- Centralized implementation for IO operations
- Added initial cursor rules for Unit Testing & Integration Test for Java
- Added initial cursor rules for Processes. Many thanks for the ideas from (https://x.com/ryancarson, https://x.com/elie2222 & https://x.com/EyalToledano)

### Changed

- Simplified support for Devcontainers
- Improved Tests
- Improved compilation with Error-prone support

## [0.6.1] 2025/04/06

### Changed

- Improved terminal width visualization

## [0.6.0] 2025/04/06

### Added

- Added support for SDKMAN init file .sdkmanrc
- Added support for .editorconfig
- Added initial Quarkus support in Cursor rules

### Changed

- Java 24
- Moved templates to folder templates
- Improved Maven behaviour
- Added Graphviz in Devcontainer support

## [0.5.0] 2025/03/13

### Added

- Fixed small issue in GA Pipeline
- Improved the internal implementation to reduce maintenance

## [0.4.1] 2025/03/02

### Added

- Added a Github Action template for Maven build
- Added Apache Maven support
- Improving Pipeline to reduce the maintenance
- Added DevContainer support
- Added Spring CLI support
- Cursor rules for Java update. Now all cursor rules has the extension .mdc
- Added CHANGELOG.md
