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
        if (version.moreOrEqual(Version.V1_12_2)) {
            msg.writeLong(id);
        } else {
            msg.writeVarInt((int) id);
        }
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        if (version.moreOrEqual(Version.V1_12_2)) {
            this.id = msg.readLong();
        } else {
            this.id = msg.readVarInt();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
