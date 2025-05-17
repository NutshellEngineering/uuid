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

import co.mp.ComparatorVerifier;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;
import static uu.id.UUIDs.*;

final class UUIDsTest {

    @Test
    void realTimestamp_returns_expected_instant_for_given_ticks() {
        var expected = Instant.parse("2018-05-27T17:43:10.101Z");
        var actual = UUIDs.realTimestamp(137467357901010000L);
        assertEquals(expected, actual);
    }

    @Test
    void realTimestamp_throws_for_unsupported_versions_and_accepts_valid() {
        assertAll(
                () -> assertThrows(UnsupportedOperationException.class, () -> UUIDs.realTimestamp(nilUUID())),
                () -> assertThrows(UnsupportedOperationException.class, () -> UUIDs.realTimestamp(v3UUID(NS.URL, "test"))),
                () -> assertThrows(UnsupportedOperationException.class, () -> UUIDs.realTimestamp(v4UUID())),
                () -> assertThrows(UnsupportedOperationException.class, () -> UUIDs.realTimestamp(v5UUID(NS.URL, "test"))),
                () -> assertDoesNotThrow(() -> UUIDs.realTimestamp(timeBasedUUID())),
                () -> assertDoesNotThrow(() -> UUIDs.realTimestamp(sortableTimeBasedUUID())),
                () -> assertDoesNotThrow(() -> UUIDs.realTimestamp(unixTimeBasedUUID())));
    }

    @Test
    void realTimestamp_matches_expected_instant_for_known_uuids() {
        var expected = Instant.parse("2018-05-27T17:43:10.101Z");
        assertAll(
                () -> assertEquals(expected, UUIDs.realTimestamp(uuid("6be6b450-61d5-11e8-8f76-010203040506")), "v1"),
                () -> assertEquals(expected, UUIDs.realTimestamp(uuid("1e861d56-be6b-6450-8f76-010203040506")), "v6"),
                () -> assertEquals(expected, UUIDs.realTimestamp(uuid("0163a2b2-6415-7d00-8f76-010203040506")), "v7"));
    }

    @Test
    void version_reports_correct_values_for_all_uuid_variants() {
        assertAll(
                () -> assertEquals(0, nilUUID().version(), "v0"),
                () -> assertEquals(1, v1UUID().version(), "v1"),
                () -> assertEquals(3, v3UUID(NS.URL, "test").version(), "v3"),
                () -> assertEquals(4, v4UUID().version(), "v4"),
                () -> assertEquals(5, v5UUID(NS.URL, "test").version(), "v5"),
                () -> assertEquals(6, v6UUID().version(), "v6"),
                () -> assertEquals(7, v7UUID().version(), "v7"),
                () -> assertEquals(8, v8UUID().version(), "v8"),
                () -> assertEquals(15, maxUUID().version(), "max"));
    }

    @Test
    void variant_reports_expected_values_for_all_uuid_variants() {
        assertAll(
                () -> assertEquals(0, nilUUID().variant(), "v0"),
                () -> assertEquals(2, v1UUID().variant(), "v1"),
                () -> assertEquals(2, v3UUID(NS.URL, "test").variant(), "v3"),
                () -> assertEquals(2, v4UUID().variant(), "v4"),
                () -> assertEquals(2, v5UUID(NS.URL, "test").variant(), "v5"),
                () -> assertEquals(2, v6UUID().variant(), "v6"),
                () -> assertEquals(2, v7UUID().variant(), "v7"),
                () -> assertEquals(2, v8UUID().variant(), "v8"),
                () -> assertEquals(7, maxUUID().variant(), "max"));
    }

    @Test
    void comparator_sorts_uuids_correctly_using_unsigned_ordering() {
        // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=7025832
        // JDK's UUID#compareTo is intentionally incorrect since inception
        // this test case demonstrates an edge case where signed comparison
        // produces the incorrect relation:
        var zeroth = nilUUID();  // sanity check
        var first = uuid("20000000-0000-4000-8000-000000000000");
        var second = uuid("e0000000-0000-4000-8000-000000000000");
        // this assertion use of a correctly implemented comparator
        var uuids = Arrays.asList(zeroth, first, second);
        uuids.sort(UUIDs.comparator());
        assertThat(uuids, contains(zeroth, first, second));

        // sort using UUID#compareTo, nb the order is not what we would expect
        var control = Arrays.asList(zeroth, first, second);
        Collections.sort(control);
        assertThat(control, contains(second, zeroth, first));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    void comparator_preserves_time_order_and_identity_for_version1() {
        var oldest = timeBasedUUID();
        var same = timeBasedUUID();
        var latest = timeBasedUUID();
        assertAll(
                () -> assertEquals(-1, oldest.compareTo(latest)),
                () -> assertEquals(0, same.compareTo(same)),
                () -> assertEquals(1, latest.compareTo(oldest)));
        var uuids = Arrays.asList(latest, oldest);
        uuids.sort(UUIDs.comparator());
        assertThat(uuids, contains(oldest, latest));
    }

    @Test
    void v3UUID_returns_canonical_values_for_known_namespaces() {
        assertAll(
                () -> assertEquals(uuid("1cf93550-8eb4-3c32-a229-826cf8c1be59"), v3UUID(NS.URL, "test")),
                () -> assertEquals(uuid("45a113ac-c7f2-30b0-90a5-a399ab912716"), v3UUID(NS.DNS, "test")),
                () -> assertEquals(uuid("61df151d-7508-321d-ada6-27936752b809"), v3UUID(NS.OID, "test")),
                () -> assertEquals(uuid("d9c53a66-fde2-3d04-b5ad-dce3848df07e"), v3UUID(NS.X500, "test")));
    }

    @Test
    void v5UUID_returns_canonical_values_for_known_namespaces() {
        assertAll(
                () -> assertEquals(uuid("da5b8893-d6ca-5c1c-9a9c-91f40a2a3649"), v5UUID(NS.URL, "test")),
                () -> assertEquals(uuid("4be0643f-1d98-573b-97cd-ca98a65347dd"), v5UUID(NS.DNS, "test")),
                () -> assertEquals(uuid("b428b5d9-df19-5bb9-a1dc-115e071b836c"), v5UUID(NS.OID, "test")),
                () -> assertEquals(uuid("63a3ab2b-61b8-5b04-ae2f-70d3875c6e97"), v5UUID(NS.X500, "test")));
    }

    @Test
    void nilUUID_has_all_bits_zero() {
        assertEquals("00000000-0000-0000-0000-000000000000", nilUUID().toString());
    }

    @Test
    void maxUUID_has_all_bits_set() {
        assertEquals("ffffffff-ffff-ffff-ffff-ffffffffffff", maxUUID().toString());
    }

    @Test
    void comparator_contract() {
        ComparatorVerifier.forComparator(Rfc9562UuidComparator.class)
                .withExamples(
                        nilUUID(),
                        uuid("20000000-0000-4000-8000-000000000000"),
                        uuid("e0000000-0000-4000-8000-000000000000"),
                        maxUUID())
                .verify();
    }
}
