# 2.2.0-SNAPSHOT starters still reference Spring Boot 3 class locations and pull a mismatched `cibseven-engine`

Filed upstream as <https://github.com/cibseven/cibseven/issues/339>.

## Resolution (2026-05-28)

The upstream maintainers clarified both issues:

- **Issue 1 was a wrong-artifact problem on our side.** The plain
  `cibseven-bpm-spring-boot-starter[-rest]` artifacts are the *Spring Boot 3*
  starters — Boot 3 FQNs in their bytecode are expected. The Spring Boot 4
  line ships under `-4`-suffixed artifact IDs (e.g.
  `cibseven-bpm-spring-boot-starter-rest-4`), compiled against Spring 7 /
  `cibseven-engine-spring-7` with the relocated FQNs. Switching to the `-4`
  artifacts lets us drop all of our Boot 3→4 compatibility shims.
- **Issue 2 was a real upstream POM bug.** `cibseven-engine-plugins` (the
  parent of `cibseven-engine-plugin-spin`) declared `cibseven-engine`
  without an explicit `<version>`, so consumers' nearest-wins resolution
  could drag in an older 2.1.0 engine. **Fix shipped in
  <https://github.com/cibseven/cibseven/issues/341> on 2026-05-26**; the
  consumer-side `<dependencyManagement>` pin workaround is no longer
  needed for the cibseven side.
- **Side issue surfaced after #341.** Our own
  `camunda7-incident-logger:2.0.0` was on the cibseven 2.1.0 line and
  was transitively pulling the older engine. Bumped to `2.0.1-SNAPSHOT`
  (depending on cibseven 2.2.0-SNAPSHOT); both transitive paths now
  agree on `cibseven-engine:2.2.0-SNAPSHOT` without any
  `<dependencyManagement>` override.

Both have been confirmed on this project's `feature/cibseven-2_2_0` branch —
the migration now passes `mvn clean verify` end-to-end with the `-4`
artifacts and no consumer-side workaround.

## Summary

The `2.2.0-SNAPSHOT` artifacts published to
`https://artifacts.cibseven.org/repository/snapshots/` are advertised as
Spring Boot 4 compatible, but:

1. The compiled bytecode of the auto-configuration classes still references
   the **Spring Boot 3** locations of `HibernateJpaAutoConfiguration`,
   `JerseyAutoConfiguration`, and `JerseyApplicationPath` — all of which were
   relocated in Spring Boot 4.
2. `cibseven-engine-plugin-spin:2.2.0-SNAPSHOT` transitively pulls
   `cibseven-engine:2.1.0`, while the other `2.2.0-SNAPSHOT` starters were
   compiled against a newer engine. Maven's nearest-wins mediation then
   selects the old engine, and the application fails to start with a
   `ClassNotFoundException` for a class only present in the newer engine.

Together these make the snapshots unusable on Spring Boot 4 without
re-introducing third-party compatibility shims, which defeats the purpose of
the migration.

## Environment

- CibSeven artifacts: `2.2.0-SNAPSHOT` (downloaded 2026-05-26, jars timestamped
  `05-26-2026 13:01`)
- Repository: `https://artifacts.cibseven.org/repository/snapshots/`
- Spring Boot: `4.0.5`
- Spring Framework: `7.0.6`
- Hibernate ORM: `7.2.7.Final`
- Java: `17`
- Maven: `3.9.x`

## Affected artifacts

- `org.cibseven.bpm.springboot:cibseven-bpm-spring-boot-starter:2.2.0-SNAPSHOT`
- `org.cibseven.bpm.springboot:cibseven-bpm-spring-boot-starter-rest:2.2.0-SNAPSHOT`
- `org.cibseven.bpm:cibseven-engine-plugin-spin:2.2.0-SNAPSHOT`

---

## Issue 1 — Boot 3 class locations are still baked into the snapshots

Spring Boot 4 relocated several auto-configurations into dedicated modules:

| Boot 3 location (no longer exists in Boot 4) | Boot 4 location |
|---|---|
| `org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration` | `org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration` (module `spring-boot-hibernate`) |
| `org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration` | `org.springframework.boot.jersey.autoconfigure.JerseyAutoConfiguration` |
| `org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath` | `org.springframework.boot.jersey.autoconfigure.JerseyApplicationPath` |

The `2.2.0-SNAPSHOT` jars still reference the old, removed locations:

### `cibseven-bpm-spring-boot-starter:2.2.0-SNAPSHOT`

```text
$ javap -v org.cibseven.bpm.spring.boot.starter.CamundaBpmAutoConfiguration \
  | grep -oE 'org/springframework/boot/[A-Za-z0-9_/]*' | sort -u
org/springframework/boot/autoconfigure/AutoConfigureAfter
org/springframework/boot/autoconfigure/condition/ConditionalOnProperty
org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaAutoConfiguration     <-- Boot 3 FQN
org/springframework/boot/context/properties/EnableConfigurationProperties
```

### `cibseven-bpm-spring-boot-starter-rest:2.2.0-SNAPSHOT`

```text
$ javap -v org.cibseven.bpm.spring.boot.starter.rest.CamundaBpmRestJerseyAutoConfiguration \
  | grep -oE 'org/springframework/boot/[A-Za-z0-9_/$]*' | sort -u
org/springframework/boot/autoconfigure/AutoConfigureAfter
org/springframework/boot/autoconfigure/AutoConfigureBefore
org/springframework/boot/autoconfigure/condition/ConditionalOnMissingBean
org/springframework/boot/autoconfigure/jersey/JerseyAutoConfiguration            <-- Boot 3 FQN
org/springframework/boot/autoconfigure/web/servlet/JerseyApplicationPath         <-- Boot 3 FQN
```

### Observed failure (without third-party compatibility shims)

```text
Caused by: java.lang.IllegalStateException: Failed to generate bean name for
imported class 'org.cibseven.bpm.spring.boot.starter.CamundaBpmAutoConfiguration'
...
Caused by: java.lang.ClassNotFoundException:
  org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
```

### Expected

Annotations like `@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)`
should reference the Boot 4 FQNs from `spring-boot-hibernate` /
`spring-boot-jersey` so the auto-config can be loaded on Spring Boot 4
without ClassNotFound errors.

### Suggested fix

Recompile the Boot starters against the Spring Boot 4 dependencies. The
starter POMs should depend on `org.springframework.boot:spring-boot-hibernate`
and `org.springframework.boot:spring-boot-jersey` (the modules that now host
the relocated classes), and the source annotations should be updated to the
new FQNs.

---

## Issue 2 — `cibseven-engine-plugin-spin:2.2.0-SNAPSHOT` pulls `cibseven-engine:2.1.0`

### Reproduction

`pom.xml`:

```xml
<dependency>
  <groupId>org.cibseven.bpm</groupId>
  <artifactId>cibseven-engine-plugin-spin</artifactId>
  <version>2.2.0-SNAPSHOT</version>
</dependency>
```

```text
$ mvn dependency:tree
[INFO] +- org.cibseven.bpm:cibseven-engine-plugin-spin:jar:2.2.0-SNAPSHOT:compile
[INFO] |  +- org.cibseven.bpm:cibseven-engine:jar:2.1.0:compile             <-- old engine
[INFO] |  |  +- org.cibseven.bpm.model:cibseven-bpmn-model:jar:2.1.0:compile
[INFO] |  |  |  \- org.cibseven.bpm.model:cibseven-xml-model:jar:2.1.0:compile
[INFO] |  |  +- org.cibseven.bpm.model:cibseven-cmmn-model:jar:2.1.0:compile
[INFO] |  |  +- org.cibseven.bpm.juel:cibseven-juel:jar:2.1.0:compile
[INFO] |  |  +- org.cibseven.bpm.dmn:cibseven-engine-dmn:jar:2.1.0:compile
[INFO] |  |  ...
```

Meanwhile, `cibseven-bpm-spring-boot-starter:2.2.0-SNAPSHOT` transitively
brings `cibseven-engine-spring-6:2.2.0-SNAPSHOT`, which was compiled against
a newer engine.

### Observed failure (with the Boot-3-FQN compatibility shims in place)

```text
Caused by: java.lang.ClassNotFoundException:
  org.cibseven.bpm.engine.impl.cfg.CronProperty
  at ...DefaultBindConstructorProvider$Constructors.getCandidateConstructors(...)
  at ...ConfigurationPropertiesBeanRegistrar.registerBeanDefinition(...)
```

`CronProperty` is referenced by a newer auto-config but does not exist in
`cibseven-engine:2.1.0`.

### Expected

`cibseven-engine-plugin-spin:2.2.0-SNAPSHOT`'s POM should declare the
engine dependency at `2.2.0-SNAPSHOT` (or whichever engine version actually
contains `CronProperty`). All sibling `2.2.0-SNAPSHOT` modules should agree
on a single engine version.

### Suggested fix

Bump (or interpolate from a BOM-style property) the
`cibseven-engine` dependency declared in
`org.cibseven.bpm:cibseven-engine-plugin-spin:2.2.0-SNAPSHOT`'s POM to the
matching `2.2.0-SNAPSHOT` version. A BOM that pins all sibling artifacts to
the same version would prevent this class of mismatch.

---

## How this was discovered

While migrating a Spring Boot 4 test framework
(`opentmf/camunda7-test-framework`) from `cibseven 2.1.0` to
`cibseven 2.2.0-SNAPSHOT`, the goal was to remove the third-party
compatibility shims (empty Boot-3-FQN classes plus a bridge auto-config)
that had been added to make the `2.1.0` starters work on Boot 4. With the
`2.2.0-SNAPSHOT` jars on the classpath:

- Removing the shims surfaced **Issue 1** (`CNFE` on the Boot 3 FQN).
- Restoring the shims surfaced **Issue 2** (`CNFE` on `CronProperty`).

Both reproducers were run with a fresh local Maven repository (`-U`), and
the artifacts were fetched from `artifacts.cibseven.org/repository/snapshots/`
on 2026-05-26.

---

## Why this matters

Adopters who already wrote Boot-3-to-Boot-4 compatibility shims to use
`cibseven 2.1.0` on Spring Boot 4 cannot drop those shims by upgrading to
`2.2.0-SNAPSHOT`, even though the marketing intent of the snapshot is to
provide native Boot 4 support. Until both issues are resolved, the snapshot
is effectively unusable on Boot 4.
