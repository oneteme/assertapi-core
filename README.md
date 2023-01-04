
# assertapi-core

...

## Status
[![Java CI with Maven](https://github.com/oneteme/assertapi-core/actions/workflows/build.yml/badge.svg)](https://github.com/oneteme/assertapi-core/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=oneteme_assertapi-core&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oneteme_assertapi-core)


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
assertion.assertApi(api);
```
