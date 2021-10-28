package ru.nanit.limbo.protocol;

import ru.nanit.limbo.protocol.registry.Version;

public interface PacketOut extends Packet {

    @Override
    default void decode(ByteMessage msg, Version version) {
        // Can be ignored for outgoing packets
    }

}
