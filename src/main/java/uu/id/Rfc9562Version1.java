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
 * Note, in comparison to v6UUID, the time_high and time_low are
 * switched.<p>
 * In layman's terms, time_high relates to the date, hour, and seconds,
 * time_mid is milliseconds, and time_low relates to nanoseconds.<p>
 * Because of the layout of
 * <pre>{@code
 *    0                   1                   2                   3
 *     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                          time_low                             |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |       time_mid                |         time_hi_and_version   |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |clk_seq_hi_res |  clk_seq_low  |         node (0-1)            |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *    |                         node (2-5)                            |
 *    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * }</pre>
 */
@API(status = Status.INTERNAL)
final class Rfc9562Version1 extends Rfc9562TimeBasedUuid {

    private Rfc9562Version1() {
    }

    public static UUID generate() {
        return new UUID(mostSignificantBits(), LEAST_SIG_BITS);
    }

    public static Instant realTimestamp(UUID uuid) {
        if (uuid.version() != 1) {
            throw new UnsupportedOperationException("Not a version 1 UUID");
        }
        return Instant.ofEpochMilli(uuid.timestamp() / 10000).plusMillis(UUID_UTC_BASE_TIME.toEpochMilli());
    }

    private static long mostSignificantBits() {
        long timestamp = timestamp();
        long msb = 0L;
        msb |= (timestamp & 0xFFFFFFFFL) << 32;
        msb |= (timestamp & 0xFFFF00000000L) >>> 16;
        msb |= (timestamp & 0x0FFF000000000000L) >>> 48;
        msb |= 0x0000000000001000L; // version 1
        return msb;
    }
}
