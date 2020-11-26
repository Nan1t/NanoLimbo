package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Packet;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketKeepAlive implements Packet {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeLong(id);
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        this.id = msg.readLong();
    }

}
