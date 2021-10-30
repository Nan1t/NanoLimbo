package ru.nanit.limbo.protocol.packets.status;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Packet;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketStatusPing implements Packet {

    private long randomId;

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeLong(randomId);
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        this.randomId = msg.readLong();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
