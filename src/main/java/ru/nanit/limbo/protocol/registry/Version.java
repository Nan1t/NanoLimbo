package ru.nanit.limbo.protocol.registry;

import java.util.HashMap;
import java.util.Map;

public enum Version {

    UNDEFINED(-1),
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
    V1_18(757);

    private static final Map<Integer, Version> VERSION_MAP;

    static {
        VERSION_MAP = new HashMap<>();
        Version last = null;
        for (Version version : values()) {
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
        return V1_8;
    }

    public static Version getMax() {
        Version[] values = Version.values();
        return values[values.length - 1];
    }

    public static Version of(int protocolNumber) {
        return VERSION_MAP.getOrDefault(protocolNumber, UNDEFINED);
    }
}
