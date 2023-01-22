package ua.nanit.limbo.server.commands.client;

import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.server.LimboServer;

public class CmdHelp implements ClientCommand {


    @Override
    public void execute(LimboServer server, ClientConnection connection, String[] args) {
        connection.sendMessage("&dHello :)");
    }

    @Override
    public String description() {
        return "Hello there :)";
    }
}