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

## Resolution (2026-05-26)

The migration is **complete on this branch as a POC** (not yet for release).
The path turned out to differ from the original assumption in two ways:

1. **Use the `-4`-suffixed starter artifact IDs**, not the original ones.
   The plain `cibseven-bpm-spring-boot-starter[-rest|-external-task-client]`
   artifacts are the Spring Boot 3 line, compiled against
   `cibseven-engine-spring-6` and the Boot 3 FQNs of
   `HibernateJpaAutoConfiguration` / `JerseyAutoConfiguration` /
   `JerseyApplicationPath`. The `-4` variants are compiled against Spring
   Boot 4 / Spring 7 / `cibseven-engine-spring-7` with the relocated FQNs.
   See <https://github.com/cibseven/cibseven/issues/339> for the upstream
   confirmation.
2. **Pin `cibseven-engine` in `<dependencyManagement>`** as a temporary
   workaround for <https://github.com/cibseven/cibseven/issues/341>, where
   `cibseven-engine-plugin-spin`'s POM declares `cibseven-engine` without
   an explicit version, letting Maven's nearest-wins resolution pull in
   an older 2.1.0. The pin lines all transitive cibseven artifacts up at
   2.2.0-SNAPSHOT. Remove the pin once #341 lands in a released snapshot.

### What changed in this branch

- `pom.xml`:
  - Added the `mvn-cibseven-snapshots` repository.
  - Bumped `cibseven.version` to `2.2.0-SNAPSHOT`.
  - Renamed `cibseven-bpm-spring-boot-starter-rest` →
    `cibseven-bpm-spring-boot-starter-rest-4` and
    `cibseven-bpm-spring-boot-starter-external-task-client` →
    `cibseven-bpm-spring-boot-starter-external-task-client-4`.
  - Added `cibseven-engine` to `<dependencyManagement>` (workaround for
    #341).
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
on `2.2.0-SNAPSHOT` artifacts and a workaround pin for #341. When CibSeven
2.2.0 ships as a stable release with #341 fixed:

1. Drop the snapshot repository declaration.
2. Drop the `cibseven-engine` pin from `<dependencyManagement>`.
3. Bump `cibseven.version` to the stable release version.

The Jackson 2 caveat from the original doc still applies — the engine
will continue to depend on Jackson 2 for the foreseeable future.
