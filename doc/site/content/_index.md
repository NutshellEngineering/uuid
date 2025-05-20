# uuid

Ergonomic UUIDs for Java, with first-class RFC 9562 support. 

This library provides static utility methods for generating,
parsing, inspecting, and comparing UUIDs in accordance with [RFC 9562](https://datatracker.ietf.org/doc/html/rfc9562).
It is fully compatible with [`UUID`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/UUID.html)
from the JDK.

This library supports:
- All standard UUID versions (1, 3, 4, 5, 6, 7, 8)
- Name-based UUIDs using standard namespaces
- Correct RFC-compliant UUID comparison
- UUID timestamp extraction

# Getting Started

## Add the Dependency

{{% tabs "id" %}}
{{% tab "Maven" %}}
Add `uuid` to your `pom.xml`.

``` xml
<dependency>
    <groupId>io.github.nutshellengineering</groupId>
    <artifactId>uuid</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```
{{% /tab %}}
{{% tab "Gradle Groovy" %}}
Add `uuid` to your `build.gradle` file.

```gradle
dependencies {
    implementation 'io.github.nutshellengineering:uuid:1.0.0'
}
```
{{% /tab %}}
{{% tab "Gradle Kotlin" %}}
Add `uuid` to your `build.gradle.kts` file.

```kotlin
dependencies {
    implementation("io.github.nutshellengineering:uuid:1.0.0")
}
``` 
{{% /tab %}}
{{% /tabs %}}

## Generate UUIDs

```java
import static uu.id.UUIDs.*;

var v1 = v1UUID();       // Time-based UUID
var v4 = v4UUID();       // Random UUID
var v6 = v6UUID();       // Sortable UUID
var v7 = v7UUID();       // Unix-time UUID
```

## Name-based UUIDs

```java
import static uu.id.UUIDs.*; 

var v3 = v3UUID(NS.DNS, "example.com");            // MD5
var v5 = v5UUID(NS.URL, "https://example.com");    // SHA-1
```

## Compare UUIDs Canonically

```java
import uu.id.UUIDs;  

list.sort(UUIDs.comparator());  // RFC 9562-compliant sort
```

## Convert from bytes or string

```java
import static uu.id.UUIDs.*; 

var u1 = uuid("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
var u2 = uuid(byteArray);
```

## Timestamp from time-based UUIDs

```java
import uu.id.UUIDs;

Instant timestamp = UUIDs.realTimestamp(UUIDs.v1UUID());
```
