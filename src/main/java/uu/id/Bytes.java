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

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

@API(status = Status.INTERNAL)
final class Bytes {

    private static final SecureRandom RANDOM = new SecureRandom();

    private Bytes() {
    }

    public static byte[] bytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static UUID bytesToUUID(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        return new UUID(bb.getLong(), bb.getLong());
    }

    public static byte[] random(int bytes) {
        byte[] result = new byte[bytes];
        random(result);
        return result;
    }

    public static void random(byte[] bytes) {
        RANDOM.nextBytes(bytes);
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static void updateDigest(MessageDigest digest, String value) {
        if (value != null) {
            digest.update(value.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static long bytesToLong(byte[] data, int length, boolean reverse) {
        long result = 0;
        for (int i = 0; i < length; i++) {
            int idx = reverse ? (length - 1 - i) : i;
            result |= ((long) data[idx] & 0xFFL) << (8 * i);
        }
        return result;
    }
}
