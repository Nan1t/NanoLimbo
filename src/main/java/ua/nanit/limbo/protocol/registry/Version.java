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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum Version {
    UNDEFINED(-1, "UNDEFINED"),
    V1_7_2(4, "1.7.2"),
    // 1.7.2-1.7.5 has same protocol numbers
    V1_7_6(5, "1.7.6"),
    // 1.7.6-1.7.10 has same protocol numbers
    V1_8(47, "1.8"),
    // 1.8-1.8.8 has same protocol numbers
    V1_9(107, "1.9"),
    V1_9_1(108, "1.9.1"),
    V1_9_2(109, "1.9.2"),
    V1_9_4(110, "1.9.4"),
    V1_10(210, "1.10"),
    // 1.10-1.10.2 has same protocol numbers
    V1_11(315, "1.11"),
    V1_11_1(316, "1.11.1"),
    // 1.11.2 has same protocol number
    V1_12(335, "1.12"),
    V1_12_1(338, "1.12.1"),
    V1_12_2(340, "1.12.2"),
    V1_13(393, "1.13"),
    V1_13_1(401, "1.13.1"),
    V1_13_2(404, "1.13.2"),
    V1_14(477, "1.14"),
    V1_14_1(480, "1.14.1"),
    V1_14_2(485, "1.14.2"),
    V1_14_3(490, "1.14.3"),
    V1_14_4(498, "1.14.4"),
    V1_15(573, "1.15"),
    V1_15_1(575, "1.15.1"),
    V1_15_2(578, "1.15.2"),
    V1_16(735, "1.16"),
    V1_16_1(736, "1.16.1"),
    V1_16_2(751, "1.16.2"),
    V1_16_3(753, "1.16.3"),
    V1_16_4(754, "1.16.4"),
    // 1.16.5 has same protocol number
    V1_17(755, "1.17"),
    V1_17_1(756, "1.17.1"),
    V1_18(757, "1.18"),
    // 1.18.1 has same protocol number
    V1_18_2(758, "1.18.2"),
    V1_19(759, "1.19"),
    V1_19_1(760, "1.19.1"),
    // 1.19.2 has same protocol number
    V1_19_3(761, "1.19.3"),
    V1_19_4(762, "1.19.4"),
    V1_20(763, "1.20"),
    // 1.20.1 has same protocol number
    V1_20_2(764, "1.20.2"),
    V1_20_3(765, "1.20.3"),
    V1_20_5(766, "1.20.5"),
    // 1.20.6 has same protocol number
    V1_21(767, "1.21"),
    // 1.21.1 has same protocol number
    V1_21_2(768, "1.21.2"),
    // 1.21.3 has same protocol number
    V1_21_4(769, "1.21.4"),
    V1_21_5(770, "1.21.5"),
    V1_21_6(771, "1.21.6"),
    V1_21_7(772, "1.21.7"),
    // 1.21.8 has same protocol number
    V1_21_9(773, "1.21.9"),
    // 1.21.10 has same protocol number
    V1_21_11(774, "1.21.11"),
    V26_1(775, "26.1"),
    // 26.1.1-26.1.2 has same protocol number
    V26_2(776, "26.2");

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
    private final String displayName;
    private Version prev;

    public boolean more(@NonNull Version another) {
        return this.protocolNumber > another.protocolNumber;
    }

    public boolean moreOrEqual(@NonNull Version another) {
        return this.protocolNumber >= another.protocolNumber;
    }

    public boolean less(@NonNull Version another) {
        return this.protocolNumber < another.protocolNumber;
    }

    public boolean lessOrEqual(@NonNull Version another) {
        return this.protocolNumber <= another.protocolNumber;
    }

    public boolean fromTo(@NonNull Version min, @NonNull Version max) {
        return this.protocolNumber >= min.protocolNumber && this.protocolNumber <= max.protocolNumber;
    }

    public boolean isSupported() {
        return this != UNDEFINED;
    }

    @NonNull
    public static Version getMin() {
        return V1_7_2;
    }

    @NonNull
    public static Version getMax() {
        return MAX;
    }

    @NonNull
    public static Version of(int protocolNumber) {
        return VERSION_MAP.getOrDefault(protocolNumber, UNDEFINED);
    }
}
