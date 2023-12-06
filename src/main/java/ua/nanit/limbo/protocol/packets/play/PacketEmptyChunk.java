package ua.nanit.limbo.protocol.packets.play;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

public class PacketEmptyChunk implements PacketOut {

    private int x;
    private int z;

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeInt(x);
        msg.writeInt(z);

        LongArrayBinaryTag longArrayTag = LongArrayBinaryTag.longArrayBinaryTag(new long[37]);
        CompoundBinaryTag tag = CompoundBinaryTag.builder()
                .put("MOTION_BLOCKING", longArrayTag).build();
        CompoundBinaryTag rootTag = CompoundBinaryTag.builder()
                .put("root", tag).build();
        msg.writeNamelessCompoundTag(rootTag);

        byte[] sectionData = new byte[]{0, 0, 0, 0, 0, 0, 1, 0};
        msg.writeVarInt(sectionData.length * 16);
        for (int i = 0; i < 16; i++) {
            msg.writeBytes(sectionData);
        }

        msg.writeVarInt(0);

        byte[] lightData = new byte[]{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, -1, -1, 0, 0};
        msg.ensureWritable(lightData.length);
        msg.writeBytes(lightData, 1, lightData.length - 1);
    }
    
}
