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

import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;

import static uu.id.Epoch.UUID_UTC_BASE_TIME;

@SuppressWarnings("unused")
@API(status = Status.STABLE, since = "1.0.0")
public final class UUIDs {

    public static final UUID NIL_UUID = new UUID(0L, 0L);
    public static final UUID MIN_UUID = NIL_UUID;
    public static final UUID MAX_UUID = new UUID(0xFFFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFFFL);

    private UUIDs() {
    }

    public enum NS {
        DNS(uuid("6ba7b810-9dad-11d1-80b4-00c04fd430c8")),
        URL(uuid("6ba7b811-9dad-11d1-80b4-00c04fd430c8")),
        OID(uuid("6ba7b812-9dad-11d1-80b4-00c04fd430c8")),
        X500(uuid("6ba7b814-9dad-11d1-80b4-00c04fd430c8"));

        public final UUID namespace;

        NS(UUID uuid) {
            this.namespace = uuid;
        }
    }

    // Constructors and Factories

    // Version 1

    public static UUID timeBasedUUID() {
        return Rfc9562Version1.generate();
    }

    public static UUID v1UUID() {
        return Rfc9562Version1.generate();
    }

    // Version 3

    public static UUID nameBasedUUID(NS namespace, String name) {
        return v3UUID(namespace, name);
    }

    public static UUID md5UUID(NS namespace, String name) {
        return v3UUID(namespace, name);
    }

    public static UUID v3UUID(NS namespace, String name) {
        return Rfc9562Version3.generate(namespace.namespace, name);
    }

    // Version 4

    public static UUID randomUUID() {
        return v4UUID();
    }

    public static UUID v4UUID() {
        return Rfc9562Version4.generate();
    }

    // Version 5

    public static UUID sha1UUID(NS namespace, String name) {
        return v5UUID(namespace, name);
    }

    public static UUID v5UUID(NS namespace, String name) {
        return Rfc9562Version5.generate(namespace.namespace, name);
    }

    // Version 6

    public static UUID lexicographicTimeBasedUUID() {
        return v6UUID();
    }


    public static UUID v6UUID() {
        return Rfc9562Version6.generate();
    }

    // Version 7

    public static UUID unixTimeBasedUUID() {
        return v7UUID();
    }

    public static UUID v7UUID() {
        return Rfc9562Version7.generate();
    }

    // Version 8

    public static UUID customUUID() {
        return v8UUID();
    }

    public static UUID customUUID(UUID source) {
        return v8UUID(source);
    }

    public static UUID v8UUID() {
        return v8UUID(v4UUID());
    }

    public static UUID v8UUID(UUID source) {
        return Rfc9562Version8.generate(source);
    }

    // Version 0 (Nil UUID)

    /**
     * Nil UUID
     * The nil UUID is special form of UUID that is specified to have all
     * 128 bits set to zero.
     */
    public static UUID nilUUID() {
        return NIL_UUID;
    }

    /**
     * The Max UUID is a special form of UUID that is specified to have all
     * 128 bits set to 1. This UUID can be thought of as the inverse of the
     * Nil UUID defined in <a href="https://www.rfc-editor.org/rfc/rfc9562#name-nil-uuid">Section 5.9</a>.
     * @see <a href="https://www.rfc-editor.org/rfc/rfc9562#name-max-uuid">5.10. Max UUID</a>
     */
    public static UUID maxUUID() {
        return MAX_UUID;
    }

    // Utilities

    public static Instant realTimestamp(UUID uuid) {
        return switch (uuid.version()) {
            case 1 -> Rfc9562Version1.realTimestamp(uuid);
            case 6 -> Rfc9562Version6.realTimestamp(uuid);
            case 7 -> Rfc9562Version7.realTimestamp(uuid);
            default -> throw new UnsupportedOperationException(uuid + " is not a time-based UUID");
        };
    }

    public static Instant realTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp / 10000).plusMillis(UUID_UTC_BASE_TIME.toEpochMilli());
    }

    public static String toBinaryString(UUID uuid) {
        return formatBinaryString(
                Long.toBinaryString(uuid.getMostSignificantBits()) +
                        Long.toBinaryString(uuid.getLeastSignificantBits())
        );
    }

    public static Comparator<UUID> comparator() {
        return new Rfc9562UuidComparator();
    }

    public static UUID uuid(byte[] data) {
        return Bytes.bytesToUUID(data);
    }

    public static UUID uuid(String str) {
        return UUID.fromString(str);
    }

    // Private

    private static String formatBinaryString(String binaryString) {
        binaryString = String.format("%128s", binaryString).replace(' ', '0');
        return binaryString.substring(0, 32) + "-" +
                binaryString.substring(32, 48) + "-" +
                binaryString.substring(48, 64) + "-" +
                binaryString.substring(64, 80) + "-" +
                binaryString.substring(80, 128);
    }

}
