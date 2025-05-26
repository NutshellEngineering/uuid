---
weight: 2
bookFlatSection: true
title: "Custom Namespaces"
---

# Creating Custom Namespaces

This `UUIDs.Namespace` interface allows applications to define both
standard and custom namespace identifiers.

Well-known namespaces (such as DNS, URL, OID, and X.500) are defined by
RFC 9562 and are provided as predefined constants in this library.
Applications that need additional or application-specific namespaces can
implement this interface to supply a custom UUID value.

When creating a custom namespace UUID, you should generally use a
randomly or time-ordered generated UUID, such as a UUIDv4 (random) or
UUIDv7 (time-ordered with random bits). This helps avoid namespace
collisions and preserves uniqueness across systems.

## Implementation Notes

Implementations must return a `UUID` that uniquely identifies the
namespace. Implementers should avoid using UUIDs that conflict with the
standard namespace identifiers defined in [RFC 9562, Section
6.6](https://datatracker.ietf.org/doc/html/rfc9562#section-6.6), which
are:

- DNS     `6ba7b810-9dad-11d1-80b4-00c04fd430c8`
- URL     `6ba7b811-9dad-11d1-80b4-00c04fd430c8`
- OID     `6ba7b812-9dad-11d1-80b4-00c04fd430c8`
- X.500   `6ba7b814-9dad-11d1-80b4-00c04fd430c8`

These UUIDs were originally generated using UUIDv1 (timestamp-based) and
are reserved for global interoperability. To prevent accidental
conflicts:

- **Do not generate new namespaces using UUIDv1, UUIDv3, or UUIDv5**,
  which can result in non-unique or repeating identifiers.
- **Prefer UUIDv4 or UUIDv7** for generating your own
  application-specific namespace UUIDs.

## Example

Here is an example implementation using an `enum`:

```java
enum MyNamespace implements Namespace {
    URN(UUIDs.uuid("1fb81e8f-4f60-4cc6-a022-29d7cba9bba9"));

    private final UUID namespace;

    MyNamespace(UUID namespace) {
        this.namespace = namespace;
    }

    @Override
    public UUID namespace() {
        return namespace;
    }
}
```

This can be passed to a version 3 or 5 UUID, for example:

```java
var rfc9562Urn = v3UUID(MyNamespace.URN, "urn:ietf:rfc:9562");
```
