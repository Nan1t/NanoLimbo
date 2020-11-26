package ru.nanit.limbo.protocol;

import ru.nanit.limbo.protocol.registry.Version;

public interface Packet {

    void encode(ByteMessage msg, Version version);

    void decode(ByteMessage msg, Version version);

}
