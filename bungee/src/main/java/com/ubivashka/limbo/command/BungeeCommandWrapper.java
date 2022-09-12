package com.ubivashka.limbo.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import ru.nanit.limbo.server.Command;

public class BungeeCommandWrapper extends net.md_5.bungee.api.plugin.Command {
    private final Command command;

    public BungeeCommandWrapper(Command command) {
        super(command.name());
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer)
            return;
        command.execute();
    }
}
