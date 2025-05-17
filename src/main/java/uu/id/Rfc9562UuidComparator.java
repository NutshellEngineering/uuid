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

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

import static uu.id.Bytes.bytes;

class Rfc9562UuidComparator implements Comparator<UUID>, Serializable {
    @Override
    public int compare(UUID u1, UUID u2) {
        var b1 = bytes(u1);
        var b2 = bytes(u2);
        if (b1.length != b2.length) {
            throw new IllegalArgumentException("UUID byte arrays differ in length");
        }
        for (int i = 0; i < b1.length; i++) {
            int diff = Byte.compareUnsigned(b1[i], b2[i]);
            if (diff != 0) return diff;
        }
        return 0;
    }
}
