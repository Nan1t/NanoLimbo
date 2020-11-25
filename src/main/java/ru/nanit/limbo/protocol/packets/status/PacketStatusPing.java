package ru.nanit.limbo.protocol.packets.status;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Packet;
import ru.nanit.limbo.protocol.Direction;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketStatusPing implements Packet {

    private long randomId;

    @Override
    public void encode(ByteMessage msg, Direction direction, Version version) {
        msg.writeLong(randomId);
    }

    @Override
    public void decode(ByteMessage msg, Direction direction, Version version) {
        this.randomId = msg.readLong();
    }

}
