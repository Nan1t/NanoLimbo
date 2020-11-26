package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketUpdateViewPos implements PacketOut {

    private int chunkX;
    private int chunkY;

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public void setChunkY(int chunkY) {
        this.chunkY = chunkY;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(chunkX);
        msg.writeVarInt(chunkY);
    }

}
