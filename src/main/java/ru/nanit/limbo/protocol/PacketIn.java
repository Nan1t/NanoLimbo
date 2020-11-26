package ru.nanit.limbo.protocol;

import ru.nanit.limbo.protocol.registry.Version;

public interface PacketIn extends Packet {

    @Override
    default void encode(ByteMessage msg, Version version) {
        // Can be ignored for incoming packets
    }

}
