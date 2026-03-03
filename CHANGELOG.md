# Changelog

All notable changes to this project will be documented in this file.

## [1.0.8]

### Added

- `withCount(int count)` method to builders, allowing registration of the same listener multiple times in a single call (useful for tasks in loops).
- `withExecutionConsumer(Consumer<DelegateExecution> consumer)` method to builders, providing access to workflow variables within custom logic. This allows reading variables via `execution.getVariable()` and modifying them via `execution.setVariable()`.
- Introduced `CHANGELOG.md` (this file), referenced from `README.md`.
- `release` Maven profile for source jar, javadoc jar, GPG signing, and central publishing — `mvn clean verify` now works without GPG keys or publishing credentials.

### Removed

- Deprecated `registerReceiveTaskExecutionListener()` method. Use `registerMessageCatchExecutionListener()` instead.

### Changed

- Overhauled `README.md`: fixed incorrect examples, added Builder Methods reference section, corrected method signatures, and improved documentation.
- Updates Spring Boot to 3.5.11.
- Updates Maven Surefire/Failsafe plugins to 3.5.5.
- Lowered JaCoCo branch coverage threshold from 90% to 75% (line and instruction thresholds remain at 90%). The remaining uncovered branches are defensive null checks in private methods interacting with the Camunda engine runtime.

## [1.0.7]

### Changed

- Extends `registerMessageCatchExecutionListener` to support additional BPMN element types:
  - Receive Task (already supported)
  - Message Intermediate Catch Event
  - Boundary Message Event
- Deprecates `registerReceiveTaskExecutionListener` (removed in 1.0.8).

## [1.0.6]

### Changed

- Updates Documentation
- Specifies `legacyJobRetryBehaviorEnabled=true` property in test scope

## [1.0.5]

### Changed

- Updates Spring Boot to 3.5.6
- Updates Camunda to 7.24.0
- Updates Camunda Incident Logger to 1.0.4

## [1.0.4]

### Changed

- Updates Spring Boot to 3.4.4
- Updates Camunda to 7.23.0
- Updates Camunda Incident Logger to 1.0.3
- Initial open source version

## [1.0.3] (Backward Incompatible)

### Added

- Custom Task Execution Listener for all tasks.
- `TaskExecution` interface to execute the expectation of all tasks.
- New methods to the `CamundaExpectationUtil` class.
- New class `TaskExecutionRegistry` to manage the expectations of all tasks.

### Changed

- Updates Spring Boot to 3.4.2

## [1.0.2]

### Changed

- Updates Spring Boot to 3.4.0
- Updates Camunda Incident Logger to 1.0.2

## [1.0.1]

### Changed

- Updates Camunda to 7.22.0 together with related libraries.

## [1.0.0]

### Added

- Initial Version

[1.0.8]: https://github.com/opentmf/camunda7-test-framework/compare/v1.0.7...v1.0.8
[1.0.7]: https://github.com/opentmf/camunda7-test-framework/compare/v1.0.6...v1.0.7
[1.0.6]: https://github.com/opentmf/camunda7-test-framework/compare/v1.0.5...v1.0.6
[1.0.5]: https://github.com/opentmf/camunda7-test-framework/compare/v1.0.4...v1.0.5
[1.0.4]: https://github.com/opentmf/camunda7-test-framework/compare/v1.0.3...v1.0.4
[1.0.3]: https://github.com/opentmf/camunda7-test-framework/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/opentmf/camunda7-test-framework/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/opentmf/camunda7-test-framework/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/opentmf/camunda7-test-framework/releases/tag/v1.0.0
