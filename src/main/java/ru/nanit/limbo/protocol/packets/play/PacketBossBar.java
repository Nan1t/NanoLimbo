package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;

import java.util.UUID;

public class PacketBossBar implements PacketOut {

    private UUID uuid;
    private String title;
    private float health;
    private Color color;
    private Division division;
    private int flags;

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeUuid(uuid);
        msg.writeVarInt(0); // Create bossbar
        msg.writeString(title);
        msg.writeFloat(health);
        msg.writeVarInt(color.index);
        msg.writeVarInt(division.index);
        msg.writeByte(flags);
    }

    public enum Color {

        PINK(0),
        BLUE(1),
        RED(2),
        GREEN(3),
        YELLOW(4),
        PURPLE(5),
        WHITE(6);

        private final int index;

        Color(int index) {
            this.index = index;
        }
    }

    public enum Division {

        SOLID(0),
        DASHES_6(1),
        DASHES_10(2),
        DASHES_12(3),
        DASHES_20(4);

        private final int index;

        Division(int index) {
            this.index = index;
        }
    }

}
