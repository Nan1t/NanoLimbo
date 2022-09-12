package ru.nanit.limbo.server.commands;

import java.util.Collection;

import ru.nanit.limbo.server.Command;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.server.Logger;

public class CmdHelp implements Command {

    private final LimboServer server;

    public CmdHelp(LimboServer server) {
        this.server = server;
    }

    @Override
    public void execute() {
        Collection<Command> commands = server.getCommandManager().getCommands();

        Logger.info("Available commands:");

        for (Command command : commands) {
            Logger.info("%s - %s", command.name(), command.description());
        }
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "Show this message";
    }
}
