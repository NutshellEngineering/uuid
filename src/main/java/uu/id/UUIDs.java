/*
 * Copyright 2025 Nutshell Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uu.id;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;

import static uu.id.Bytes.bytesToUUID;
import static uu.id.Epoch.UUID_UTC_BASE_TIME;

/**
 * Static utility methods for creating, parsing, inspecting, and comparing {@link java.util.UUID UUID} instances
 * in accordance with <a href="https://datatracker.ietf.org/doc/html/rfc9562">RFC 9562</a>.<p>
 * This class provides factory methods for generating all standard UUID versions defined in RFC 9562,
 * including:
 * <ul>
 *     <li>{@linkplain #v1UUID() Version 1} (time-based UUIDs)</li>
 *     <li>{@linkplain #v3UUID(Namespace, String) Version 3} (name-based using MD5)</li>
 *     <li>{@linkplain #v4UUID() Version 4} (random UUIDs)</li>
 *     <li>{@linkplain #v5UUID(Namespace, String) Version 5} (name-based using SHA-1)</li>
 *     <li>{@linkplain #v6UUID() Version 6} (sortable, time-reordered)</li>
 *     <li>{@linkplain #v7UUID() Version 7} (Unix time-based with randomness)</li>
 *     <li>{@linkplain #v8UUID() Version 8} (custom format)</li>
 * </ul>
 * It also defines constants and utilities for working with the special values defined in the RFC:
 * <ul>
 *     <li>{@linkplain #nilUUID() Nil UUID} (all bits set to zero)</li>
 *     <li>{@linkplain #maxUUID() Max UUID} (all bits set to one)</li>
 *     <li>{@linkplain NS Standard name spaces} for deterministic UUIDs</li>
 * </ul><p>
 * Unlike the default {@link java.util.UUID#compareTo(UUID)} implementation, which uses signed component-wise
 * comparison and is known to produce incorrect results in some edge cases, this class offers a correct
 * comparator via {@link #comparator()} that performs canonical bytewise comparison as defined in
 * <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-6">RFC 9562 Section 6</a>.<p>
 * This class is immutable and thread-safe.
 *
 * @see java.util.UUID
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562">RFC 9562 </a>
 * @since 1.0.0
 */
@SuppressWarnings("unused")
@API(status = Status.STABLE, since = "1.0.0")
public final class UUIDs {

    /**
     * The nil UUID is special form of UUID that is specified to have all
     * 128 bits set to zero.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.9">RFC 9562 Section 5.9</a>
     */
    public static final UUID NIL_UUID = new UUID(0L, 0L);

    /**
     * The nil UUID is special form of UUID that is specified to have all
     * 128 bits set to zero.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.9">RFC 9562 Section 5.9</a>
     */
    public static final UUID MIN_UUID = NIL_UUID;

    /**
     * The Max UUID is a special form of UUID that is specified to have all
     * 128 bits set to 1. This UUID can be thought of as the inverse of the
     * Nil UUID defined in <a href="https://www.rfc-editor.org/rfc/rfc9562#name-nil-uuid">Section 5.9</a>.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.10">RFC 9562 Section 5.10</a>
     */
    public static final UUID MAX_UUID = new UUID(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL);

    private UUIDs() {
    }

    /**
     * Represents a namespace identifier used for name-based UUID generation (versions 3 and 5). <p>
     * Implementations of this interface provide a specific {@link UUID} that serves as the namespace
     * for generating name-based UUIDs, as defined in
     * <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-6.5">RFC 9562, Section 6.5</a>.
     *
     * @apiNote
     * This interface allows applications to define both standard and custom namespace identifiers.
     * Well-known namespaces (such as DNS, URL, OID, and X.500) are defined by RFC 9562 and are
     * provided as predefined constants in this library. Applications that need additional or
     * application-specific namespaces can implement this interface to supply a custom UUID value. <p>
     * When creating a custom namespace UUID, you should generally use a randomly or time-ordered
     * generated UUID, such as a UUIDv4 (random) or UUIDv7 (time-ordered with random bits).
     * This helps avoid namespace collisions and preserves uniqueness across systems.
     *
     * @implSpec
     * Implementations must return a {@link UUID} that uniquely identifies the namespace. Implementers
     * should avoid using UUIDs that conflict with the standard namespace identifiers defined in
     * <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-6.6">RFC 9562, Section 6.6</a>,
     * which are as follows:
     * <ul>
     *   <li>DNS: {@code 6ba7b810-9dad-11d1-80b4-00c04fd430c8}</li>
     *   <li>URL: {@code 6ba7b811-9dad-11d1-80b4-00c04fd430c8}</li>
     *   <li>OID: {@code 6ba7b812-9dad-11d1-80b4-00c04fd430c8}</li>
     *   <li>X.500: {@code 6ba7b814-9dad-11d1-80b4-00c04fd430c8}</li>
     * </ul>
     * These UUIDs were originally generated using UUIDv1 (timestamp-based) and are reserved for
     * global interoperability. To prevent accidental conflicts, do not generate new namespaces
     * using UUIDv1, UUIDv3, or UUIDv5 — these formats may result in non-unique or repeating identifiers.
     * Prefer UUIDv4 or UUIDv7 for generating your own application-specific namespace UUIDs.
     *
     * <h6>Example Implementation</h6>
     * <pre>{@code
     * enum MyNamespace implements Namespace {
     *
     *     URN(UUIDs.uuid("1fb81e8f-4f60-4cc6-a022-29d7cba9bba9"));
     *
     *     private final UUID namespace;
     *
     *     MyNamespace(UUID namespace) {
     *         this.namespace = namespace;
     *     }
     *
     *     @Override
     *     public UUID namespace() {
     *         return namespace;
     *     }
     * }
     * }</pre>
     */
    public interface Namespace {

        /**
         * Returns the {@link UUID} that identifies this namespace.
         *
         * @return the namespace UUID
         */
        UUID namespace();
    }

    /**
     * Well-known namespace UUIDs used for name-based UUID generation (v3 and v5). <p>
     * These values are defined in <a href="https://datatracker.ietf.org/doc/html/rfc9562#name_based_uuid_generation">RFC 9562 Section 6.5</a>.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.2">RFC 9562 Section 5.2</a>
     */
    public enum NS implements Namespace {
        /**
         * Namespace for DNS names.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#name-namespace-id-usage-and-allo">RFC 9562 Section 6.6</a>
         */
        DNS(uuid("6ba7b810-9dad-11d1-80b4-00c04fd430c8")),

        /**
         * Namespace for URL strings.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#name-namespace-id-usage-and-allo">RFC 9562 Section 6.6</a>
         */
        URL(uuid("6ba7b811-9dad-11d1-80b4-00c04fd430c8")),

        /**
         * Namespace for ISO OIDs.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#name-namespace-id-usage-and-allo">RFC 9562 Section 6.6</a>
         */
        OID(uuid("6ba7b812-9dad-11d1-80b4-00c04fd430c8")),

        /**
         * Namespace for X.500 Distinguished Names.
         *
         * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#name-namespace-id-usage-and-allo">RFC 9562 Section 6.6</a>
         */
        X500(uuid("6ba7b814-9dad-11d1-80b4-00c04fd430c8"));

        private final UUID namespace;

        NS(UUID uuid) {
            this.namespace = uuid;
        }

        public UUID namespace() {
            return namespace;
        }
    }

    /**
     * timeBasedUUID is an alias for {@link #v1UUID()}<p>
     * UUIDv1 is a time-based UUID featuring a 60-bit timestamp represented by Coordinated Universal Time (UTC)
     * as a count of 100-nanosecond intervals since 00:00:00.00, 15 October 1582 (the date of Gregorian reform
     * to the Christian calendar).
     *
     * @return a Version 1 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.1">RFC 9562 Section 5.1</a>
     */
    public static UUID timeBasedUUID() {
        return v1UUID();
    }

    /**
     * UUIDv1 is a time-based UUID featuring a 60-bit timestamp represented by Coordinated Universal Time (UTC)
     * as a count of 100-nanosecond intervals since 00:00:00.00, 15 October 1582 (the date of Gregorian reform
     * to the Christian calendar).
     *
     * @return a Version 1 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.1">RFC 9562 Section 5.1</a>
     */
    public static UUID v1UUID() {
        return Rfc9562Version1.generate();
    }

    /**
     * nameBasedUUID is an alias for {@link #v3UUID(Namespace, String)}<p>
     * UUIDv3 is a name-based UUID that uses MD5 hashing.
     *
     * @param namespace the namespace UUID
     * @param name      the name string
     * @return a Version 3 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.3">RFC 9562 Section 5.3</a>
     */
    public static UUID nameBasedUUID(Namespace namespace, String name) {
        return v3UUID(namespace, name);
    }

    /**
     * md5UUID is an alias for {@link #v3UUID(Namespace, String)}<p>
     * UUIDv3 is a name-based UUID that uses MD5 hashing.
     *
     * @param namespace the namespace UUID
     * @param name      the name string
     * @return a Version 3 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.3">RFC 9562 Section 5.3</a>
     */
    public static UUID md5UUID(NS namespace, String name) {
        return v3UUID(namespace, name);
    }

    /**
     * UUIDv3 is a name-based UUID that uses MD5 hashing.
     *
     * @param namespace the namespace UUID
     * @param name      the name string
     * @return a Version 3 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.3">RFC 9562 Section 5.3</a>
     */
    public static UUID v3UUID(Namespace namespace, String name) {
        return Rfc9562Version3.generate(namespace.namespace(), name);
    }

    /**
     * nameBasedUUID is an alias for {@link #v3UUID(String)}<p>
     * UUIDv3 is a name-based UUID that uses MD5 hashing.<p>
     * Version 3 UUIDs generated without a namespace are not standard compliant.
     * UUIDs generated using this method are identical to UUIDs created using {@link UUID#nameUUIDFromBytes(byte[])},
     * and carry the same risk of semantic collision.
     *
     * @param name the name string
     * @return a Version 3 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.3">RFC 9562 Section 5.3</a>
     */
    public static UUID nameBasedUUID(String name) {
        return Rfc9562Version3.generate(name);
    }

    /**
     * md5UUID is an alias for {@link #v3UUID(String)}<p>
     * UUIDv3 is a name-based UUID that uses MD5 hashing.<p>
     * Version 3 UUIDs generated without a namespace are not standard compliant.
     * UUIDs generated using this method are identical to UUIDs created using {@link UUID#nameUUIDFromBytes(byte[])},
     * and carry the same risk of semantic collision.
     *
     * @param name the name string
     * @return a Version 3 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.3">RFC 9562 Section 5.3</a>
     */
    public static UUID md5UUID(String name) {
        return Rfc9562Version3.generate(name);
    }

    /**
     * UUIDv3 is a name-based UUID that uses MD5 hashing.<p>
     * Version 3 UUIDs generated without a namespace are not standard compliant.
     * UUIDs generated using this method are identical to UUIDs created using {@link UUID#nameUUIDFromBytes(byte[])},
     * and carry the same risk of semantic collision.
     *
     * @param name the name string
     * @return a Version 3 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.3">RFC 9562 Section 5.3</a>
     */
    public static UUID v3UUID(String name) {
        return Rfc9562Version3.generate(name);
    }

    /**
     * randomUUID is an alias for {@link #v4UUID()}<p>
     * UUIDv4 is a randomly generated UUID that uses 122 bits of randomness.
     *
     * @return a Version 4 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.4">RFC 9562 Section 5.4</a>
     */
    public static UUID randomUUID() {
        return v4UUID();
    }

    /**
     * UUIDv4 is a randomly generated UUID that uses 122 bits of randomness.
     *
     * @return a Version 4 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.4">RFC 9562 Section 5.4</a>
     */
    public static UUID v4UUID() {
        return Rfc9562Version4.generate();
    }

    /**
     * sha1UUID is an alias for {@link #v5UUID(Namespace, String)}<p>
     * UUIDv5 is a name-based UUID that uses SHA-1 hashing.
     *
     * @param namespace the namespace UUID
     * @param name      the name string
     * @return a Version 5 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.5">RFC 9562 Section 5.5</a>
     */
    public static UUID sha1UUID(Namespace namespace, String name) {
        return v5UUID(namespace, name);
    }

    /**
     * UUIDv5 is a name-based UUID that uses SHA-1 hashing.
     *
     * @param namespace the namespace UUID
     * @param name      the name string
     * @return a Version 5 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.5">RFC 9562 Section 5.5</a>
     */
    public static UUID v5UUID(Namespace namespace, String name) {
        return Rfc9562Version5.generate(namespace.namespace(), name);
    }

    /**
     * sha1UUID is an alias for {@link #v5UUID(String)}<p>
     * UUIDv5 is a name-based UUID that uses SHA-1 hashing.
     * Version 5 UUIDs generated without a namespace are not standard compliant. There is a risk of semantic
     * collision.
     *
     * @param name the name string
     * @return a Version 5 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.5">RFC 9562 Section 5.5</a>
     */
    public static UUID sha1UUID(String name) {
        return v5UUID(name);
    }

    /**
     * UUIDv5 is a name-based UUID that uses SHA-1 hashing.<p>
     * Version 5 UUIDs generated without a namespace are not standard compliant. There is a risk of semantic
     * collision.
     *
     * @param name the name string
     * @return a Version 5 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.5">RFC 9562 Section 5.5</a>
     */
    public static UUID v5UUID(String name) {
        return Rfc9562Version5.generate(name);
    }

    /**
     * sortableTimeBasedUUID is an alias for {@link #v6UUID()}<p>
     * UUIDv6 is a time-ordered UUID with the 60-bit timestamp placed in the most significant bits
     * to improve lexicographic sortability.
     *
     * @return a Version 6 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.6">RFC 9562 Section 5.6</a>
     */
    public static UUID sortableTimeBasedUUID() {
        return v6UUID();
    }

    /**
     * UUIDv6 is a time-ordered UUID with the 60-bit timestamp placed in the most significant bits
     * to improve lexicographic sortability.
     *
     * @return a Version 6 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.6">RFC 9562 Section 5.6</a>
     */
    public static UUID v6UUID() {
        return Rfc9562Version6.generate();
    }

    /**
     * unixTimeBasedUUID is an alias for {@link #v7UUID()}<p>
     * UUIDv7 encodes a 48-bit Unix timestamp in milliseconds plus 74 random bits.
     *
     * @return a Version 7 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.7">RFC 9562 Section 5.7</a>
     */
    public static UUID unixTimeBasedUUID() {
        return v7UUID();
    }

    /**
     * UUIDv7 encodes a 48-bit Unix timestamp in milliseconds plus 74 random bits.
     *
     * @return a Version 7 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.7">RFC 9562 Section 5.7</a>
     */
    public static UUID v7UUID() {
        return Rfc9562Version7.generate();
    }

    /**
     * customUUID is an alias for {@link #v8UUID()}<p>
     * UUIDv8 is reserved for application-specific semantics.
     *
     * @return a Version 8 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.8">RFC 9562 Section 5.8</a>
     */
    public static UUID customUUID() {
        return v8UUID();
    }

    /**
     * UUIDv8 is reserved for application-specific semantics.
     *
     * @return a Version 8 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.8">RFC 9562 Section 5.8</a>
     */
    public static UUID v8UUID() {
        return v8UUID(v4UUID());
    }

    /**
     * customUUID is an alias for {@link #v8UUID(UUID)}<p>
     * UUIDv8 generated from a source UUID by rewriting version and variant.
     *
     * @param source an existing UUID to base the custom UUID on
     * @return a Version 8 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.8">RFC 9562 Section 5.8</a>
     */
    public static UUID customUUID(UUID source) {
        return v8UUID(source);
    }

    /**
     * UUIDv8 generated from a source UUID by rewriting version and variant.
     *
     * @param source an existing UUID to base the custom UUID on
     * @return a Version 8 UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.8">RFC 9562 Section 5.8</a>
     */
    public static UUID v8UUID(UUID source) {
        return Rfc9562Version8.generate(source);
    }

    /**
     * The nil UUID is special form of UUID that is specified to have all
     * 128 bits set to zero.
     *
     * @return the Nil UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.9">RFC 9562 Section 5.9</a>
     */
    public static UUID nilUUID() {
        return NIL_UUID;
    }

    /**
     * The Max UUID is a special form of UUID that is specified to have all
     * 128 bits set to 1. This UUID can be thought of as the inverse of the
     * Nil UUID defined in <a href="https://www.rfc-editor.org/rfc/rfc9562#name-nil-uuid">Section 5.9</a>.
     *
     * @return the Max UUID
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-5.10">RFC 9562 Section 5.10</a>
     */
    public static UUID maxUUID() {
        return MAX_UUID;
    }

    /**
     * Extracts the real timestamp from a time-based UUID (v1, v6, or v7).
     *
     * @param uuid a time-based UUID
     * @return the corresponding Instant
     * @throws UnsupportedOperationException if the UUID version is not time-based
     */
    public static Instant realTimestamp(UUID uuid) {
        return switch (uuid.version()) {
            case 1 -> Rfc9562Version1.realTimestamp(uuid);
            case 6 -> Rfc9562Version6.realTimestamp(uuid);
            case 7 -> Rfc9562Version7.realTimestamp(uuid);
            default -> throw new UnsupportedOperationException(uuid + " is not a time-based UUID");
        };
    }

    /**
     * Converts a 60-bit UUID timestamp into an Instant.
     *
     * @param timestamp UUID timestamp in 100ns ticks
     * @return an Instant corresponding to the UUID time
     */
    public static Instant realTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp / 10_000).plusMillis(UUID_UTC_BASE_TIME.toEpochMilli());
    }

    /**
     * Returns a binary string representation of the UUID.
     *
     * @param uuid the UUID to represent
     * @return the binary string in grouped layout
     */
    public static String toBinaryString(UUID uuid) {
        return formatBinaryString(
                Long.toBinaryString(uuid.getMostSignificantBits()) +
                        Long.toBinaryString(uuid.getLeastSignificantBits())
        );
    }

    /**
     * Returns a comparator that performs correct unsigned bytewise comparison of UUIDs. <p>
     * The default {@link UUID#compareTo(UUID)} implementation performs signed comparisons, which violates
     * the expected ordering semantics for UUIDs. This comparator corrects that.
     *
     * @return a comparator that matches RFC 9562 canonical ordering
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9562#section-6">RFC 9562 Section 6</a>
     * @see <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=7025832">JDK Bug 7025832</a>
     */
    public static Comparator<UUID> comparator() {
        return new Rfc9562UuidComparator();
    }

    /**
     * Converts a byte array to a UUID.
     *
     * @param data a 16-byte array
     * @return the UUID
     */
    public static UUID uuid(byte[] data) {
        return bytesToUUID(data);
    }

    /**
     * Parses a UUID from a standard string representation.
     *
     * @param name the UUID string
     * @return the UUID
     * @throws IllegalArgumentException If name does not conform to the string representation as
     *                                  described in {@link UUID#toString}
     * @see <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8216407">JDK Bug 8216407</a>
     */
    public static UUID uuid(String name) {
        if (name.getBytes(StandardCharsets.UTF_8).length != 36
                || name.charAt(8) != '-'
                || name.charAt(13) != '-'
                || name.charAt(18) != '-'
                || name.charAt(23) != '-') {
            throw new IllegalArgumentException("Invalid UUID string: " + name);
        }
        return UUID.fromString(name);
    }

    private static String formatBinaryString(String binaryString) {
        binaryString = String.format("%128s", binaryString).replace(' ', '0');
        return binaryString.substring(0, 32) + "-" +
                binaryString.substring(32, 48) + "-" +
                binaryString.substring(48, 64) + "-" +
                binaryString.substring(64, 80) + "-" +
                binaryString.substring(80, 128);
    }
}
