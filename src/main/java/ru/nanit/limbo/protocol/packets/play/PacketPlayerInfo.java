package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.connection.ClientConnection;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;

/**
 * This packet wrapper used only for ADD_PLAYER action
 */
public class PacketPlayerInfo implements PacketOut {

    private int gameMode;
    private ClientConnection connection;

    public void setConnection(ClientConnection connection) {
        this.connection = connection;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeVarInt(0);
        msg.writeVarInt(1);
        msg.writeUuid(connection.getUuid());
        msg.writeString(connection.getUsername());
        msg.writeVarInt(0);
        msg.writeVarInt(gameMode);
        msg.writeVarInt(60);
        msg.writeBoolean(false);
    }

}
