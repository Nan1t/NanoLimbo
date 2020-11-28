package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;

import java.util.UUID;

/**
 * This packet was very simplified and using only for ADD_PLAYER action
 */
public class PacketPlayerInfo implements PacketOut {

    private int gameMode = 3;
    private String username = "";
    private UUID uuid;

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setUuid(UUID uuid){
        this.uuid = uuid;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeVarInt(0);
        msg.writeVarInt(1);
        msg.writeUuid(uuid);
        msg.writeString(username);
        msg.writeVarInt(0);
        msg.writeVarInt(gameMode);
        msg.writeVarInt(60);
        msg.writeBoolean(false);
    }

}
