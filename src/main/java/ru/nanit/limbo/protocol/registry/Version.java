package ru.nanit.limbo.protocol.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum Version {

    UNDEFINED(-1),
    V1_9(107),
    V1_9_1(108),
    V1_9_2(109),
    V1_9_4(110),
    V1_10(210),
    V1_11(315),
    V1_11_1(316),
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
    V1_16_4(754);

    public static final Map<Integer, Version> VERSION_MAP;

    static {
        VERSION_MAP = new HashMap<>();

        for (Version version : values()){
            VERSION_MAP.put(version.getProtocolNumber(), version);
        }
    }

    public static Version getMinimal(){
        return V1_9;
    }

    public static Version of(int protocolNumber){
        return VERSION_MAP.getOrDefault(protocolNumber, UNDEFINED);
    }

    private final int protocolNumber;

    Version(int protocolNumber){
        this.protocolNumber = protocolNumber;
    }

    public int getProtocolNumber(){
        return this.protocolNumber;
    }

    public Version getClosest(Collection<Version> available){
        Version closest = getMinimal();

        for (Version version : available){
            if (version.protocolNumber > closest.protocolNumber && version.protocolNumber < this.protocolNumber){
                closest = version;
            }
        }

        return closest;
    }

}
