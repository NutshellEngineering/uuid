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

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static uu.id.Bytes.bytes;
import static uu.id.Bytes.concat;

@API(status = Status.INTERNAL)
final class Rfc9562Version3 {

    private Rfc9562Version3() {
    }

    public static UUID generate(UUID namespace, String name) {
        return UUID.nameUUIDFromBytes(concat(bytes(namespace), name.getBytes(UTF_8)));
    }
}
