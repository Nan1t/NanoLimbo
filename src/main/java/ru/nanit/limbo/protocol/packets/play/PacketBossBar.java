package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.server.data.BossBar;

import java.util.UUID;

public class PacketBossBar implements PacketOut {

    private UUID uuid;
    private BossBar bossBar;
    private int flags;

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setBossBar(BossBar bossBar) {
        this.bossBar = bossBar;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeUuid(uuid);
        msg.writeVarInt(0); // Create bossbar
        msg.writeString(bossBar.getText());
        msg.writeFloat(bossBar.getHealth());
        msg.writeVarInt(bossBar.getColor().getIndex());
        msg.writeVarInt(bossBar.getDivision().getIndex());
        msg.writeByte(flags);
    }

}
