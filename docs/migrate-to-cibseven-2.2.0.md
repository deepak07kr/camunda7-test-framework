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
