package ru.nanit.limbo.server.commands;

import ru.nanit.limbo.server.Command;
import ru.nanit.limbo.server.LimboServer;

import java.util.Map;

public class CmdHelp implements Command {

    private final LimboServer server;

    public CmdHelp(LimboServer server) {
        this.server = server;
    }

    @Override
    public void execute() {
        StringBuilder msg = new StringBuilder();
        Map<String, Command> commands = server.getCommandManager().getCommands();

        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            msg.append("\n");
            msg.append(entry.getKey());
            msg.append(" - ");
            msg.append(entry.getValue().description());
        }

        msg.append("\n");

        System.out.println(msg);
    }

    @Override
    public String description() {
        return "Show this message";
    }
}
