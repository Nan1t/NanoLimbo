package ru.nanit.limbo.protocol;

import ru.nanit.limbo.protocol.registry.Version;

public interface Packet {

    void encode(ByteMessage msg, Direction direction, Version version);

    void decode(ByteMessage msg, Direction direction, Version version);

}
