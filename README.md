
# assertapi-core

...

## Status
[![CI/CD](https://github.com/oneteme/assertapi-core/actions/workflows/main.yml/badge.svg?branch=develop)](https://github.com/oneteme/assertapi-core/actions/workflows/main.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=oneteme_assertapi-core&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oneteme_assertapi-core)

## Problem

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/problem.diagram.svg)

## Solution

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/solution.diagram.svg)

## Integration

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/integration.diagram.svg)

## Usecase

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/usecase.diagram.svg)

## Overview

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/overview.diagram.svg)

## Setup

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/setup.diagram.svg)

## Deploy

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/deploy.diagram.svg)

## Assertion

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/assertion.diagram.svg)

## Architecture

text..

![CI/CD](https://raw.githubusercontent.com/oneteme/assertapi-core/main/doc/diagram/architecture.diagram.svg)

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
        .comparing(stableRelease, latestRelease) //run api on stable and latest server
        .using(responseComparator) // ResponseComparator by default
        .build() 
        .assertApi(api); // compare results each other
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
        .regiter("BASIC_TOKEN", customTokenAuthenticator) // customTokenAuthenticator must implements ClientAuthenticator
        .comparing(stableRelease, latestRelease)
        .using(responseComparator)
        .build()
        .assertApi(api);
```

### Comparison Stages

  
  1. ELAPSED_TIME
  2. HTTP_CODE
  3. CONTENT_TYPE
  4. HEADER_CONTENT
  5. RESPONSE_CONTENT


### ApiRequest
| Field             | Description             | default |
| ----------------  | ----------------------- | ------- |
| uri               | HTTP uri                |         |
| method            | HTTP method             | GET     |
| headers           | HTTP headers            | N/A     |
| body              | HTTP body               | N/A     |
| acceptableStatus  | HTTP expected status    | [200]   |
| name              | API name                | N/A     |
| version           | API version             | N/A     |
| description       | API description         | N/A     |
| contentComparator | Content comparator      | N/A     |
| executionConfig   | Execution configuration | N/A     |

### ContentComparator
| Field             | Description             | default |
| ----------------  | ----------------------- | ------- |
| type              | Content comparator type | N/A     |
| transformers      | Content transformers    | N/A     |

### ExecutionConfig
| Field             | Description             | default |
| ----------------  | ----------------------- | ------- |
| parallel          | API Parallel execution  | true    |
| enabled           | API Assertion enabled   | true    |
