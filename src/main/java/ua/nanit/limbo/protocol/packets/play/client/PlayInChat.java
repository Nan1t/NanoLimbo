package ua.nanit.limbo.protocol.packets.play.client;

import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketIn;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;

public class PlayInChat implements PacketIn {

    String player = null;
    String chat = null;

    @Override
    public void decode(ByteMessage msg, Version version) {
        this.chat = msg.readString();
    }

    @Override
    public void handle(ClientConnection conn, LimboServer server) {
        this.player = conn.getUsername();
        if (chat.startsWith("/") && server.getCommandManager().getClientCommands().containsKey(chat.split(" ")[0].replace("/", "")))
            server.getCommandManager().getClientCommand(chat.split(" ")[0].replace("/", "")).execute(server, conn, chat.split(" "));
    }
}