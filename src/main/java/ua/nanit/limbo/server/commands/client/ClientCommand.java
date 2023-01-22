package ua.nanit.limbo.server.commands.client;

import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.server.LimboServer;

public interface ClientCommand {

    void execute(LimboServer server, ClientConnection connection, String args[]);

    String description();

}
