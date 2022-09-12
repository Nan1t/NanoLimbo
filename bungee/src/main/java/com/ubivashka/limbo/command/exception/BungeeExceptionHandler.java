package com.ubivashka.limbo.command.exception;

import com.ubivashka.limbo.NanoLimboBungee;

import revxrsal.commands.bungee.exception.BungeeExceptionAdapter;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

public class BungeeExceptionHandler extends BungeeExceptionAdapter {
    private final NanoLimboBungee plugin;

    public BungeeExceptionHandler(NanoLimboBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public void missingArgument(CommandActor actor, MissingArgumentException exception) {
        actor.reply(plugin.getLimboConfig().getMessages().message("no-argument"));
    }

    @Override
    public void noPermission(CommandActor actor, NoPermissionException exception) {
        super.noPermission(actor, exception);
    }


}
