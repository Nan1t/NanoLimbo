/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.protocol.registry;

import java.util.HashMap;
import java.util.Map;

public enum Version {

    UNDEFINED(-1),
    V1_7_2(4),
    // 1.7.2-1.7.5 has same protocol numbers
    V1_7_6(5),
    // 1.7.6-1.7.10 has same protocol numbers
    V1_8(47),
    // 1.8-1.8.8 has same protocol numbers
    V1_9(107),
    V1_9_1(108),
    V1_9_2(109),
    V1_9_4(110),
    V1_10(210),
    // 1.10-1.10.2 has same protocol numbers
    V1_11(315),
    V1_11_1(316),
    // 1.11.2 has same protocol number
    V1_12(335),
    V1_12_1(338),
    V1_12_2(340),
    V1_13(393),
    V1_13_1(401),
    V1_13_2(404),
    V1_14(477),
    V1_14_1(480),
    V1_14_2(485),
    V1_14_3(490),
    V1_14_4(498),
    V1_15(573),
    V1_15_1(575),
    V1_15_2(578),
    V1_16(735),
    V1_16_1(736),
    V1_16_2(751),
    V1_16_3(753),
    V1_16_4(754),
    // 1.16.5 has same protocol number
    V1_17(755),
    V1_17_1(756),
    V1_18(757),
    // 1.18.1 has same protocol number
    V1_18_2(758),
    V1_19(759),
    V1_19_1(760),
    // 1.19.2 has same protocol number
    V1_19_3(761),
    V1_19_4(762),
    V1_20(763);

    private static final Map<Integer, Version> VERSION_MAP;
    private static final Version MAX;

    static {
        Version[] values = values();

        VERSION_MAP = new HashMap<>();
        MAX = values[values.length - 1];

        Version last = null;
        for (Version version : values) {
            version.prev = last;
            last = version;
            VERSION_MAP.put(version.getProtocolNumber(), version);
        }
    }

    private final int protocolNumber;
    private Version prev;

    Version(int protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public int getProtocolNumber() {
        return this.protocolNumber;
    }

    public Version getPrev() {
        return prev;
    }

    public boolean more(Version another) {
        return this.protocolNumber > another.protocolNumber;
    }

    public boolean moreOrEqual(Version another) {
        return this.protocolNumber >= another.protocolNumber;
    }

    public boolean less(Version another) {
        return this.protocolNumber < another.protocolNumber;
    }

    public boolean lessOrEqual(Version another) {
        return this.protocolNumber <= another.protocolNumber;
    }

    public boolean fromTo(Version min, Version max) {
        return this.protocolNumber >= min.protocolNumber && this.protocolNumber <= max.protocolNumber;
    }

    public boolean isSupported() {
        return this != UNDEFINED;
    }

    public static Version getMin() {
        return V1_7_2;
    }

    public static Version getMax() {
        return MAX;
    }

    public static Version of(int protocolNumber) {
        return VERSION_MAP.getOrDefault(protocolNumber, UNDEFINED);
    }
}
