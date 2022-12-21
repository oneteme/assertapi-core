
# assertapi-core

...

## Status

[![Java CI with Maven](https://github.com/oneteme/assertapi-core/actions/workflows/maven.yml/badge.svg)](https://github.com/oneteme/assertapi-core/actions/workflows/maven.yml)


## MAVEN Integration


```xml
<dependency>
  <groupId>io.github.oneteme.assertapi</groupId>
  <artifactId>assertapi-core</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```


## Usage

```java
assertion = new ApiAssertionsFactory()
        .comparing(stableRelease, latestRelease)
        .using(responseComparator)
        .build();
assertion.exec(api);
```