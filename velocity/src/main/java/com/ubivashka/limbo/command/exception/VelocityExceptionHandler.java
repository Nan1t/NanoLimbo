package com.ubivashka.limbo.command.exception;

import com.ubivashka.limbo.NanoLimboVelocity;

import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.velocity.VelocityCommandActor;
import revxrsal.commands.velocity.exception.VelocityExceptionAdapter;

public class VelocityExceptionHandler extends VelocityExceptionAdapter {
    private final NanoLimboVelocity plugin;

    public VelocityExceptionHandler(NanoLimboVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void missingArgument(CommandActor actor, MissingArgumentException exception) {
        actor.as(VelocityCommandActor.class).reply(plugin.getLimboConfig().getMessages().message("no-argument"));
    }

    @Override
    public void noPermission(CommandActor actor, NoPermissionException exception) {
        super.noPermission(actor, exception);
    }


}
