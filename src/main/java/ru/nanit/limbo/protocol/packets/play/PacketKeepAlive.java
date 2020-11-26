package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Packet;

public class PacketKeepAlive implements Packet {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeLong(id);
    }

    @Override
    public void decode(ByteMessage msg) {
        this.id = msg.readLong();
    }

}
