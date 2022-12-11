package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketSpawnPosition implements PacketOut  {

    private long x;
    private long y;
    private long z;

    public PacketSpawnPosition() { }

    public PacketSpawnPosition(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeLong(encodePosition(x, y ,z));
        msg.writeFloat(0);
    }

    private static long encodePosition(long x, long y, long z) {
        return ((x & 0x3FFFFFF) << 38) | ((z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }
}
