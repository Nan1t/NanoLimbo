package com.ubivashka.limbo.command;

import com.ubivashka.limbo.NanoLimboBungee;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.bungee.annotation.CommandPermission;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.SendMessageException;
import ru.nanit.limbo.server.LimboServer;

@Command("limbostart")
public class StartCommand {
    @Dependency
    private NanoLimboBungee plugin;

    @Default
    @CommandPermission("limbo.start")
    public void execute(CommandActor actor, LimboServer limboServer) throws Exception {
        if (limboServer.isRunning())
            throw new SendMessageException(plugin.getLimboConfig().getMessages().message("already-running"));
        limboServer.start();
        throw new SendMessageException(plugin.getLimboConfig().getMessages().message("successfully-started"));
    }
}
