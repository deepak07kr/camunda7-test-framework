When migrating to Spring Boot 4, we had switched using cibseven libraries in place of the retired camunda7 libraries.

At that time, the latest cibseven libraries were at 2.1.0 level, and they lacked Spring Boot 4 support.

Hence, you had to introduce compatibility configurations within this project under this base package:
org.springframework.*

Now, cibseven released their 2.2.0-SNAPSHOT artifacts. They contain Spring Boot 4 starters. They still depend on Jackson 2, and it will continue to be like that for a long time, so beware.

I want you to start using their 2.2.0-SNAPSHOT libraries. I believe this will nullify the compatibility
layer that you previously introduced.

To use their snapshot libraries, the following needs to be done:

- Add the Snapshot Repository
```xml
<repositories>
  <repository>
    <id>mvn-cibseven-snapshots</id>
    <name>CIB seven Community Snapshots</name>
    <url>https://artifacts.cibseven.org/repository/snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

We are already in a separate branch.

Can you inspect the project and generate an implementation plan?

---

## Resolution (2026-05-28)

The migration is **complete on this branch as a POC** (not yet for release).
The key correction from the original assumption: the Boot 3 and Boot 4
starter lines live under **different artifact IDs**, not different
versions. The `-4`-suffixed artifacts are compiled against Spring Boot 4 /
Spring 7 / `cibseven-engine-spring-7` with the relocated Boot 4 FQNs,
while the plain `cibseven-bpm-spring-boot-starter[-rest|-external-task-client]`
artifacts are the Spring Boot 3 line. See
<https://github.com/cibseven/cibseven/issues/339> for the upstream
confirmation.

Two side issues surfaced and were resolved during the POC:

- **`cibseven-engine-plugin-spin` used to drag an older engine onto the
  classpath** because its POM declared `cibseven-engine` without an
  explicit version. Fixed upstream in
  <https://github.com/cibseven/cibseven/issues/341>; the latest
  2.2.0-SNAPSHOT contains the fix, so no consumer-side workaround is
  required.
- **`camunda7-incident-logger:2.0.0` was on the cibseven 2.1.0 line** and
  was transitively pulling the old engine. Bumped to
  `2.0.1-SNAPSHOT`, which depends on cibseven 2.2.0-SNAPSHOT and brings
  the engine in at the matching version. Both transitive paths now agree
  on `cibseven-engine:2.2.0-SNAPSHOT` without any
  `<dependencyManagement>` override.

### What changed in this branch

- `pom.xml`:
  - Added the `mvn-cibseven-snapshots` repository.
  - Bumped `cibseven.version` to `2.2.0-SNAPSHOT`.
  - Bumped `camunda7-incident-logger.version` to `2.0.1-SNAPSHOT`.
  - Renamed `cibseven-bpm-spring-boot-starter-rest` →
    `cibseven-bpm-spring-boot-starter-rest-4` and
    `cibseven-bpm-spring-boot-starter-external-task-client` →
    `cibseven-bpm-spring-boot-starter-external-task-client-4`.
  - Dropped the `spring-boot-hibernate` direct dependency (it was only
    there to back the Boot-3-FQN compat stub).
- Deleted the Boot 3→4 compatibility layer:
  - `src/main/java/org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration.java`
  - `src/main/java/org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaAutoConfiguration.java`
  - `src/main/java/org/springframework/boot/autoconfigure/web/servlet/JerseyApplicationPath.java`
  - `src/main/java/org/opentmf/camunda/test/configuration/compat/Boot4CibSevenCompatAutoConfiguration.java`
  - `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

### Verification

`mvn clean verify` passes: 14 integration tests in `BpmnTaskParseListenerPluginIT`
all green, all JaCoCo coverage thresholds met (line/instruction ≥ 90%,
branch ≥ 70%, ≤ 2 missed classes), ArchUnit tests pass.

### POC status

This branch is not intended for release in its current form — it depends
on `2.2.0-SNAPSHOT` artifacts (both CibSeven and `camunda7-incident-logger`).
When CibSeven 2.2.0 ships as a stable release:

1. Drop the snapshot repository declaration.
2. Bump `cibseven.version` and `camunda7-incident-logger.version` to the
   stable releases.

The Jackson 2 caveat from the original doc still applies — the engine
will continue to depend on Jackson 2 for the foreseeable future.
