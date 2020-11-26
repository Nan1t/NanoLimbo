package ru.nanit.limbo.protocol.packets.play;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketChunkData implements PacketOut {

    private int chunkX;
    private int chunkZ;
    private boolean fullChunk;
    private int primaryBitMask;
    private CompoundBinaryTag heightMaps;
    private int[] biomes;
    private byte[] data;
    private CompoundBinaryTag[] blockEntities;

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    public void setFullChunk(boolean fullChunk) {
        this.fullChunk = fullChunk;
    }

    public void setPrimaryBitMask(int primaryBitMask) {
        this.primaryBitMask = primaryBitMask;
    }

    public void setHeightMaps(CompoundBinaryTag heightMaps) {
        this.heightMaps = heightMaps;
    }

    public void setBiomes(int[] biomes) {
        this.biomes = biomes;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setBlockEntities(CompoundBinaryTag[] blockEntities) {
        this.blockEntities = blockEntities;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeInt(chunkX);
        msg.writeInt(chunkZ);
        msg.writeBoolean(fullChunk);
        msg.writeVarInt(primaryBitMask);
        msg.writeCompoundTag(heightMaps);

        if (fullChunk){
            msg.writeVarIntArray(biomes);
        }



        msg.writeBytesArray(data);
        msg.writeCompoundTagArray(blockEntities);
    }

}
