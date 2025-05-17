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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static uu.id.Bytes.bytes;
import static uu.id.Bytes.bytesToUUID;
import static uu.id.Bytes.concat;

@API(status = Status.INTERNAL)
final class Rfc9562Version5 {

    private Rfc9562Version5() {
    }

    public static UUID generate(UUID namespace, String name) {
        return generate(namespace, name.getBytes(StandardCharsets.UTF_8));
    }

    public static UUID generate(UUID namespace, byte[] name) {
        try {
            var digest = MessageDigest.getInstance("SHA1");
            digest.update(concat(bytes(namespace), name));
            byte[] sha1Bytes = digest.digest();
            sha1Bytes[6] &= 0x0f;        // clear version
            sha1Bytes[6] |= 0x50;        // set to version 5
            sha1Bytes[8] &= 0x3f;        // clear variant
            sha1Bytes[8] |= (byte) 0x80; // set to variant 2
            return bytesToUUID(sha1Bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError("SHA1 not supported", e);
        }
    }
}
