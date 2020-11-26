package ru.nanit.limbo.protocol.packets.status;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Packet;

public class PacketStatusPing implements Packet {

    private long randomId;

    @Override
    public void encode(ByteMessage msg) {
        msg.writeLong(randomId);
    }

    @Override
    public void decode(ByteMessage msg) {
        this.randomId = msg.readLong();
    }

}
