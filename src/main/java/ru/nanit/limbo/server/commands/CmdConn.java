package ru.nanit.limbo.server.commands;

import ru.nanit.limbo.server.Command;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.server.Logger;

public class CmdConn implements Command {

    private final LimboServer server;

    public CmdConn(LimboServer server) {
        this.server = server;
    }

    @Override
    public void execute() {
        Logger.info("Connections: %d", server.getConnections().getCount());
    }

    @Override
    public String description() {
        return "Display connections count";
    }
}
