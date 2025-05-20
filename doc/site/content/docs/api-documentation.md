---
weight: 1
bookFlatSection: true
title: "API Documentation"
---

# API Documentation

## UUIDs

Static utility methods for creating, parsing, inspecting, and comparing 
[`UUID`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/UUID.html) instances in accordance
with [RFC 9562](https://datatracker.ietf.org/doc/html/rfc9562).

The `UUIDs` class provides factory methods for generating all standard UUID versions defined in RFC 9562, including:

- [`v1UUID()`](#uuid-v1uuid) — Version 1 (time-based UUIDs)
- [`v3UUID(NS, String)`](#uuid-v3uuidns-namespace-string-name) — Version 3 (name-based using MD5)
- [`v3UUID(String)`](#uuid-v3uuidstring-name) — JDK Compatible Version 3
- [`v4UUID()`](#uuid-v4uuid) — Version 4 (random UUIDs)
- [`v5UUID(NS, String)`](#uuid-v5uuidns-namespace-string-name) — Version 5 (name-based using SHA-1)
- [`v5UUID(String)`](#uuid-v5uuidstring-name) — Version 5 with similarity to name-only Version 3
- [`v6UUID()`](#uuid-v6uuid) — Version 6 (sortable, time-reordered)
- [`v7UUID()`](#uuid-v7uuid) — Version 7 (Unix time-based with randomness)
- [`v8UUID()`](#uuid-v8uuid) — Version 8 (custom format)

It also defines constants and utilities for working with the special values defined in the RFC:

- [`nilUUID()`](#uuid-niluuid) — Nil UUID (all bits set to zero)
- [`maxUUID()`](#uuid-maxuuid) — Max UUID (all bits set to one)
- [`NS`](#enum-uuidsns) — Standard name spaces for deterministic UUIDs

Unlike the default [`UUID.compareTo(UUID)`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/UUID.html#compareTo(java.util.UUID)) implementation, which uses signed component-wise comparison and is known to produce incorrect results in some edge cases, this class offers a correct comparator via [`comparator()`](#comparatoruuid-comparator) that performs canonical bytewise comparison as defined in [RFC 9562 Section 6](https://datatracker.ietf.org/doc/html/rfc9562#section-6).

This class is immutable and thread-safe.

Since 1.0.0  
See [`UUID`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/UUID.html)  
See [RFC 9562](https://datatracker.ietf.org/doc/html/rfc9562)

## Constants

### `public static final UUID NIL_UUID`

The nil UUID is a special form of UUID with all 128 bits set to zero.

See [RFC 9562 §5.9](https://datatracker.ietf.org/doc/html/rfc9562#section-5.9)

---

### `public static final UUID MIN_UUID`

Alias for `NIL_UUID`.

See [RFC 9562 §5.9](https://datatracker.ietf.org/doc/html/rfc9562#section-5.9)

---

### `public static final UUID MAX_UUID`

The Max UUID is a special form of UUID with all 128 bits set to one.

See [RFC 9562 §5.10](https://datatracker.ietf.org/doc/html/rfc9562#section-5.10)

---

## Enum: `UUIDs.NS`

Well-known namespace UUIDs used for name-based generation.

- `DNS` — DNS names
- `URL` — URL strings
- `OID` — ISO OIDs
- `X500` — X.500 Distinguished Names

See [RFC 9562 §6.5](https://datatracker.ietf.org/doc/html/rfc9562#section-6.5)

---

## UUID Generation

### `UUID timeBasedUUID()`
### `UUID v1UUID()`

Generates a Version 1 time-based UUID.

Return a Version 1 UUID  
See [RFC 9562 §5.1](https://datatracker.ietf.org/doc/html/rfc9562#section-5.1)

---

### `UUID md5UUID(NS namespace, String name)`
### `UUID v3UUID(NS namespace, String name)`

Generates a Version 3 name-based UUID using MD5 hashing.

Param namespace the namespace UUID  
Param name the name string  
Return a Version 3 UUID  
See [RFC 9562 §5.3](https://datatracker.ietf.org/doc/html/rfc9562#section-5.3)

---

### `UUID md5UUID(String name)`
### `UUID v3UUID(String name)`

{{% hint warning %}}
The `v3UUID(String)` method, which omits a namespace, is **not compliant with [RFC 9562 §5.3](https://datatracker.ietf.org/doc/html/rfc9562#section-5.3)**.  
RFC-compliant Version 3 UUIDs require both a **namespace** and a **name** to ensure deterministic, globally unique output.

This method is provided for compatibility with the JDK’s built-in [`UUID.nameUUIDFromBytes()`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/UUID.html#nameUUIDFromBytes(byte%5B%5D)), which also omits the namespace prefix by default. As such, the result may match JVM-generated v3 UUIDs but should **not be relied on** for deterministic cross-platform UUID generation.
{{% /hint %}}

Generates a non-standard Version 3 UUID.

Return a Version 3 UUID  
See [RFC 9562 §5.3](https://datatracker.ietf.org/doc/html/rfc9562#section-5.3)

---

### `UUID randomUUID()`
### `UUID v4UUID()`

Generates a Version 4 random UUID with 122 bits of randomness.

Return a Version 4 UUID  
See [RFC 9562 §5.4](https://datatracker.ietf.org/doc/html/rfc9562#section-5.4)

---

### `UUID sha1UUID(NS namespace, String name)`
### `UUID v5UUID(NS namespace, String name)`

Generates a Version 5 name-based UUID using SHA-1 hashing.

Param namespace the namespace UUID  
Param name the name string  
Return a Version 5 UUID  
See [RFC 9562 §5.5](https://datatracker.ietf.org/doc/html/rfc9562#section-5.5)

---

### `UUID sha1UUID(String name)`
### `UUID v5UUID(String name)`

{{% hint warning %}}
The `v5UUID(String)` method, which omits a namespace, is **not compliant with [RFC 9562 §5.5](https://datatracker.ietf.org/doc/html/rfc9562#section-5.5)**.  
Version 5 UUIDs require both a **namespace** and a **name** to ensure deterministic, globally unique output.

This method is provided only for symmetry with `v3UUID(String)`, and should not be used in contexts where standards compliance is required.  
{{% /hint %}}

Generates a non-standard Version 5 UUID.

Return a Version 5 UUID  
See [RFC 9562 §5.5](https://datatracker.ietf.org/doc/html/rfc9562#section-5.5)

---

### `UUID sortableTimeBasedUUID()`
### `UUID v6UUID()`

Generates a Version 6 time-ordered UUID.

Return a Version 6 UUID  
See [RFC 9562 §5.6](https://datatracker.ietf.org/doc/html/rfc9562#section-5.6)

---

### `UUID unixTimeBasedUUID()`
### `UUID v7UUID()`

Generates a Version 7 UUID with a 48-bit Unix timestamp and 74 bits of randomness.

Return a Version 7 UUID  
See [RFC 9562 §5.7](https://datatracker.ietf.org/doc/html/rfc9562#section-5.7)

---

### `UUID customUUID()`
### `UUID v8UUID()`

Generates a Version 8 custom UUID from a random base.

Return a Version 8 UUID  
See [RFC 9562 §5.8](https://datatracker.ietf.org/doc/html/rfc9562#section-5.8)

---

### `UUID customUUID(UUID source)`
### `UUID v8UUID(UUID source)`

Generates a Version 8 UUID from an existing source UUID.

Param source the UUID to transform  
Return a Version 8 UUID  
See [RFC 9562 §5.8](https://datatracker.ietf.org/doc/html/rfc9562#section-5.8)

---

## Special Values

### `UUID nilUUID()`

Returns the Nil UUID (`00000000-0000-0000-0000-000000000000`).

Return the Nil UUID  
See [RFC 9562 §5.9](https://datatracker.ietf.org/doc/html/rfc9562#section-5.9)

---

### `UUID maxUUID()`

Returns the Max UUID (`ffffffff-ffff-ffff-ffff-ffffffffffff`).

Return the Max UUID  
See [RFC 9562 §5.10](https://datatracker.ietf.org/doc/html/rfc9562#section-5.10)

---

## Timestamps

### `Instant realTimestamp(UUID uuid)`

Extracts the real timestamp from a time-based UUID (v1, v6, or v7).

Param uuid a time-based UUID  
Return the corresponding `Instant`  
Throws UnsupportedOperationException if the UUID version is not time-based

---

### `Instant realTimestamp(long timestamp)`

Converts a 60-bit UUID timestamp (in 100-nanosecond units) into an `Instant`.

Param timestamp the UUID timestamp  
Return the equivalent `Instant`

---

## Parsing and Conversion

### `UUID uuid(byte[] data)`

Converts a 16-byte array to a UUID.

Param data a byte array of length 16  
Return the UUID

---

### `UUID uuid(String name)`

Parses a UUID from its string representation.

Param name the UUID string  
Return the parsed UUID
Throws UnsupportedOperationException If name does not conform to the string representation as described in [`UUID#toString()`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/UUID.html#toString())

---

### `String toBinaryString(UUID uuid)`

Returns a grouped binary string representation of the UUID.

Param uuid the UUID to convert  
Return a binary string like `00000000-00000000-...`

---

## Comparison

{{% hint warning %}}
Java’s default [`UUID.compareTo()`](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html#compareTo-java.util.UUID-) can produce unexpected results when sorting — consider using UUIDs.comparator() instead.
{{% /hint %}}

### `Comparator<UUID> comparator()`

Returns a comparator that performs correct unsigned bytewise comparison.

Return a canonical comparator  
See [RFC 9562 §6](https://datatracker.ietf.org/doc/html/rfc9562#section-6)  
See [JDK Bug 7025832](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=7025832)
