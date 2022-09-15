package com.ubivashka.limbo.command;


import com.ubivashka.limbo.NanoLimboVelocity;
import com.ubivashka.limbo.command.exception.SendComponentException;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.velocity.annotation.CommandPermission;
import ru.nanit.limbo.server.LimboServer;

@Command("limbostop")
public class StopCommand {
    @Dependency
    private NanoLimboVelocity plugin;

    @Default
    @CommandPermission("limbo.stop")
    public void execute(CommandActor actor, LimboServer limboServer) {
        if (!limboServer.isRunning())
            throw new SendComponentException(plugin.getLimboConfig().getMessages().message("already-stopped"));
        limboServer.stop();
        throw new SendComponentException(plugin.getLimboConfig().getMessages().message("successfully-stopped"));
    }
}
