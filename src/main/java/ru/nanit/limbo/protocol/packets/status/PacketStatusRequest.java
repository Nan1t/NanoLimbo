package ru.nanit.limbo.protocol.packets.status;

import ru.nanit.limbo.protocol.*;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketStatusRequest implements PacketIn {

    @Override
    public void decode(ByteMessage msg, Version version) {

    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
