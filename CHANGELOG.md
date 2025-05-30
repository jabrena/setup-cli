# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.10.0] 30/5/2025

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

## [0.9.0] 13/05/2025

### Added

- Added initial cursor rules for Agile.

## [0.8.0] 12/05/2025

### Added

- Added support for JSpecify
- Added flatten-maven-plugin in the build
- Added git-commit-id-plugin in the build
- Added Version & Commit in Banner

### Changed

- Improved .editorconfig support
- Moved development to the package info.jab.cli
- Moved jacoco to specific profile -Pjacoco

## [0.7.0] 27/04/2025

### Added

- Centralized implementation for IO operations
- Added initial cursor rules for Unit Testing & Integration Test for Java
- Added initial cursor rules for Processes. Many thanks for the ideas from (https://x.com/ryancarson, https://x.com/elie2222 & https://x.com/EyalToledano)

### Changed

- Simplified support for Devcontainers
- Improved Tests
- Improved compilation with Error-prone support

## [0.6.1] 06/04/2025

### Changed

- Improved terminal width visualization

## [0.6.0] 06/04/2025

### Added

- Added support for SDKMAN init file .sdkmanrc
- Added support for .editorconfig
- Added initial Quarkus support in Cursor rules

### Changed

- Java 24
- Moved templates to folder templates
- Improved Maven behaviour
- Added Graphviz in Devcontainer support

## [0.5.0] 13/03/2025

### Added

- Fixed small issue in GA Pipeline
- Improved the internal implementation to reduce maintenance

## [0.4.1] 02/03/2025

### Added

- Added a Github Action template for Maven build
- Added Apache Maven support
- Improving Pipeline to reduce the maintenance
- Added DevContainer support
- Added Spring CLI support
- Cursor rules for Java update. Now all cursor rules has the extension .mdc
- Added CHANGELOG.md
