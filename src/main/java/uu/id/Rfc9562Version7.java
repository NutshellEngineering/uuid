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
import java.util.UUID;

import static uu.id.Bytes.bytesToUUID;
import static uu.id.Bytes.concat;
import static uu.id.Bytes.random;

@API(status = Status.INTERNAL)
final class Rfc9562Version7 extends Rfc9562TimeBasedUuid {

    private Rfc9562Version7() {
    }

    public static UUID generate() {
        long now = System.currentTimeMillis();
        byte[] bytes = new byte[16];

        long time = now & 0xFFFFFFFFFFFFL;
        bytes[0] = (byte) (time >>> 40);
        bytes[1] = (byte) (time >>> 32);
        bytes[2] = (byte) (time >>> 24);
        bytes[3] = (byte) (time >>> 16);
        bytes[4] = (byte) (time >>> 8);
        bytes[5] = (byte) time;

        concat(random(10), bytes);

        bytes[6] &= 0x0F;           // clear version
        bytes[6] |= 0x70;           // set to version 7
        bytes[8] &= 0x3F;           // clear variant
        bytes[8] |= (byte) 0x80;    // set to variant 2

        return bytesToUUID(bytes);
    }

    public static Instant realTimestamp(UUID uuid) {
        if (uuid.version() != 7) {
            throw new UnsupportedOperationException("Not a version 7 UUID");
        }

        long msb = uuid.getMostSignificantBits();
        long timestamp = (msb >>> 16) & 0xFFFFFFFFFFFFL;
        return Instant.ofEpochMilli(timestamp);
    }
}
