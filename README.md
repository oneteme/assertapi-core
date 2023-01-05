
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
var assertion = new ApiAssertionFactory()
        .comparing(stableRelease, latestRelease) 
        .using(responseComparator) // ResponseComparator by default
        .build()
        .assertApi(api);
```

### Handle assertion result

```java
var assertion = new ApiAssertionFactory()
        .comparing(stableRelease, latestRelease)
        .using(responseComparator)
        .trace((api, res)-> log.debug("testing : {} => {}", api, res)) //log api compare result
        .build()
        .assertApi(api);
```

### Register custom Client Authenticator

```java
var assertion = new ApiAssertionFactory()
        .regiter("BASIC_TOKEN", customTokenAuthenticator) // customTokenAuthenticator must extends ClientAuthenticator
        .comparing(stableRelease, latestRelease)
        .using(responseComparator)
        .build()
        .assertApi(api);
```

