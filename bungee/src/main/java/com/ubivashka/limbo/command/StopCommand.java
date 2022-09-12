package com.ubivashka.limbo.command;


import com.ubivashka.limbo.NanoLimboBungee;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.bungee.annotation.CommandPermission;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.SendMessageException;
import ru.nanit.limbo.server.LimboServer;

@Command("limbostop")
public class StopCommand {
    @Dependency
    private NanoLimboBungee plugin;

    @Default
    @CommandPermission("limbo.stop")
    public void execute(CommandActor actor, LimboServer limboServer) {
        if (!limboServer.isRunning())
            throw new SendMessageException(plugin.getLimboConfig().getMessages().message("already-stopped"));
        limboServer.stop();
        throw new SendMessageException(plugin.getLimboConfig().getMessages().message("successfully-stopped"));
    }
}
