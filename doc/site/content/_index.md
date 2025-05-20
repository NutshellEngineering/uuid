# uuid

Static utility methods for generating, parsing, inspecting, and comparing UUIDs in accordance with [RFC 9562](https://datatracker.ietf.org/doc/html/rfc9562).

This library supports:
- All standard UUID versions (1, 3, 4, 5, 6, 7, 8)
- Name-based UUIDs using standard namespaces
- Correct RFC-compliant UUID comparison
- UUID timestamp extraction

---

## Getting Started

### Generate UUIDs

```java
import uu.id.UUIDs;
import java.util.UUID;

UUID v1 = UUIDs.v1UUID();       // Time-based UUID
UUID v4 = UUIDs.v4UUID();       // Random UUID
UUID v6 = UUIDs.v6UUID();       // Sortable UUID
UUID v7 = UUIDs.v7UUID();       // Unix-time UUID
```

### Name-based UUIDs

```java
UUID v3 = UUIDs.v3UUID(UUIDs.NS.DNS, "example.com");            // MD5
UUID v5 = UUIDs.v5UUID(UUIDs.NS.URL, "https://example.com");    // SHA-1
```

### Compare UUIDs Canonically

```java
list.sort(UUIDs.comparator());  // RFC 9562-compliant sort
```

### Convert from bytes or string

```java
UUID u1 = UUIDs.uuid("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
UUID u2 = UUIDs.uuid(byteArray);
```

### Timestamp from time-based UUIDs

```java
Instant timestamp = UUIDs.realTimestamp(UUIDs.v1UUID());
```
