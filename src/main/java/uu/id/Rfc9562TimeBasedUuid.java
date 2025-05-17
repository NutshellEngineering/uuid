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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static uu.id.Bytes.bytesToLong;
import static uu.id.Bytes.updateDigest;
import static uu.id.Epoch.UUID_UTC_BASE_TIME;

@API(status = Status.INTERNAL)
abstract class Rfc9562TimeBasedUuid {
    private static final AtomicLong LAST_TIMESTAMP = new AtomicLong(0);
    protected static final long LEAST_SIG_BITS = clockSequenceAndNode();

    /**
     * 4.1.5.  Clock Sequence
     * For UUID version 1, the clock sequence is used to help avoid
     * duplicates that could arise when the clock is set backwards in time
     * or if the node ID changes.
     * If the clock is set backwards, or might have been set backwards
     * (e.g., while the system was powered off), and the UUID generator can
     * not be sure that no UUIDs were generated with timestamps larger than
     * the value to which the clock was set, then the clock sequence has to
     * be changed.  If the previous value of the clock sequence is known, it
     * can just be incremented; otherwise it should be set to a random or
     * high-quality pseudo-random value.
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc4122#section-4.1.5">RFC 4122 4.1.5</a>
     */
     private static long clockSequenceAndNode() {
        long clock = random(System.currentTimeMillis());
        long node = node();
        long lsb = 0L;
        lsb |= clock << 48;
        lsb &= 0x3FFFFFFFFFFFFFFFL; // clear bits 63 and 62
        lsb |= 0x8000000000000000L; // set to variant 2
        lsb |= node;
        return lsb;
    }

    /**
     * 4.1.6.  Node
     * For UUID version 1, the node field consists of an IEEE 802 MAC
     * address, usually the host address.  For systems with multiple IEEE
     * 802 addresses, any available one can be used.  The lowest addressed
     * octet (octet number 10) contains the global/local bit and the
     * unicast/multicast bit, and is the first octet of the address
     * transmitted on an 802.3 LAN.
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc4122#section-4.1.6">RFC 4122 4.1.6</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc4122#section-4.5">RFC 4122 4.5</a>
     */
    private static long node() {
        try {
            return macAddress();
        } catch (UnknownHostException | SocketException ignored) {
        }
        // For systems with no IEEE address, a randomly or pseudo-randomly
        // generated value may be used; see Section 4.5.
        return randomAddress();
    }

    private static long macAddress() throws UnknownHostException, SocketException {
        var ip = InetAddress.getLocalHost();
        var network = NetworkInterface.getByInetAddress(ip);
        if (network == null) {
            throw new SocketException("No interface found");
        }
        if (network.getHardwareAddress() == null) {
            throw new SocketException("No hardware address found");
        }
        return bytesToLong(network.getHardwareAddress(), 6, true);
    }

    /**
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc4122#section-4.1.6">RFC 4122 4.1.6</a>
     */
    private static long randomAddress() {
        try {
            var digest = MessageDigest.getInstance("MD5");
            var properties = System.getProperties();
            for (String key : new String[]{"java.vendor", "java.vendor.url", "java.version", "os.arch", "os.name", "os.version"}) {
                updateDigest(digest, properties.getProperty(key));
            }
            byte[] hash = digest.digest();
            var node = bytesToLong(hash, 6, false);
            // The multicast bit must be set (1) in pseudo-random addresses, in order that they will never
            // conflict with addresses obtained from network cards.
            //                                 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            //                multicast bit -> |M        node (0-1)            |
            // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            // |                         node (2-5)                            |
            // +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            return node | 0x0000010000000000L;
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError("MD5 not supported", e);
        }
    }

    private static long random(long seed) {
        return new Random(seed).nextLong();
    }

    /**
     * 4.1.4.  Timestamp
     * The timestamp is a 60-bit value.  For UUID version 1, this is
     * represented by Coordinated Universal Time (UTC) as a count of 100-
     * nanosecond intervals since 00:00:00.00, 15 October 1582 (the date of
     * Gregorian reform to the Christian calendar).
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc4122#section-4.1.4">RFC 4122 4.1.4</a>
     */
    protected static long timestamp() {
        // we are limited to generating 10k UUIDs/ms (ten-million/s), so we may have to block
        while (true) {
            long now = (System.currentTimeMillis() - UUID_UTC_BASE_TIME.toEpochMilli()) * 10000;
            long last = LAST_TIMESTAMP.get();
            if (now > last && LAST_TIMESTAMP.compareAndSet(last, now)) return now;

            long candidate = last + 1;
            // If we've generated more than 10k uuid in that millisecond,
            // we restart the whole process until we get to the next millis.
            // Otherwise, we try use our candidate ... unless we've been
            // beaten by another thread in which case we try again.
            if ((candidate / 10000) == (last / 10000) && LAST_TIMESTAMP.compareAndSet(last, candidate))
                return candidate;
        }
    }
}
