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

import static uu.id.Epoch.UUID_UTC_BASE_TIME;

/**
 * Note, in comparison to v1UUID, the time_high and time_low are
 * switched.
 * <pre>{@code
 *    0                   1                   2                   3
 *     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |               time_high (32 bits, most recent first)          |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |  time_mid (16)                |  time_low_and_version (16)    |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |clk_seq_hi_res |  clk_seq_low  |         node (0-1)            |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                         node (2-5)                            |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * }</pre>
 */
@API(status = Status.INTERNAL)
final class Rfc9562Version6 extends Rfc9562TimeBasedUuid {

    private Rfc9562Version6() {
    }

    public static UUID generate() {
        return new UUID(mostSignificantBits(), LEAST_SIG_BITS);
    }

    public static Instant realTimestamp(UUID uuid) {
        if (uuid.version() != 6) {
            throw new UnsupportedOperationException("Not a version 6 UUID");
        }

        long msb = uuid.getMostSignificantBits();

        // Remove the version (bits 12–15) and recover the 60-bit timestamp
        long timestamp = (msb >>> 4) & 0x0FFFFFFFFFFFFFFFL;

        // Convert from 100-ns ticks since 1582-10-15 to Instant
        long millis = timestamp / 10_000;
        return UUID_UTC_BASE_TIME.plusMillis(millis);
    }

    private static long mostSignificantBits() {
        long timestamp = timestamp();
        long msb = (timestamp & 0x0FFFFFFFFFFFFFFFL) << 4;
        msb &= ~(0xFL << 12);        // clear version bits
        msb |= (6L << 12);           // set version 6
        return msb;
    }
}
