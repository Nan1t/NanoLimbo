package ru.nanit.limbo.protocol.packets.play;

import io.netty.buffer.ByteBufOutputStream;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

import java.io.IOException;

public class PacketEmptyChunk implements PacketOut {

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeInt(0);
        msg.writeInt(0);

        writeHeightmaps(msg, version.getProtocolNumber());

        byte[] sectionData = new byte[]{0, 0, 0, 0, 0, 0, 1, 0};
        msg.writeVarInt(sectionData.length * 16);
        for (int i = 0; i < 16; i++) {
            msg.writeBytes(sectionData);
        }

        msg.writeVarInt(0);

        byte[] lightData = new byte[]{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 3, -1, -1, 0, 0};
        msg.writeBytes(lightData);
    }

    private void writeHeightmaps(ByteMessage buf, int version) {
        try (ByteBufOutputStream output = new ByteBufOutputStream(buf)) {
            output.writeByte(10); //CompoundTag
            output.writeUTF(""); // CompoundName
            output.writeByte(10); //CompoundTag
            output.writeUTF("root"); //root compound
            output.writeByte(12); //long array
            output.writeUTF("MOTION_BLOCKING");
            long[] longArrayTag = new long[version < Version.V1_18.getProtocolNumber() ? 36 : 37];
            output.writeInt(longArrayTag.length);
            for (int i = 0, length = longArrayTag.length; i < length; i++) {
                output.writeLong(longArrayTag[i]);
            }
            buf.writeByte(0); //end of compound
            buf.writeByte(0); //end of compound
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
