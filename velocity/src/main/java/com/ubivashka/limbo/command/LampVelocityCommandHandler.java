package com.ubivashka.limbo.command;

import java.util.Collection;
import java.util.Collections;

import com.ubivashka.limbo.NanoLimboVelocity;
import com.ubivashka.limbo.command.exception.SendComponentException;
import com.ubivashka.limbo.command.exception.VelocityExceptionHandler;

import revxrsal.commands.CommandHandler;
import revxrsal.commands.velocity.core.VelocityHandler;
import ru.nanit.limbo.server.Command;
import ru.nanit.limbo.server.LimboServer;

public class LampVelocityCommandHandler implements ru.nanit.limbo.server.CommandHandler<Command> {
    private final NanoLimboVelocity plugin;
    private final CommandHandler commandHandler;

    public LampVelocityCommandHandler(NanoLimboVelocity plugin) {
        this.plugin = plugin;
        commandHandler = new VelocityHandler(plugin, plugin.getServer()).disableStackTraceSanitizing()
                .setExceptionHandler(new VelocityExceptionHandler(plugin));
    }

    public LampVelocityCommandHandler registerAll() {
        commandHandler.registerValueResolver(LimboServer.class, context -> {
            LimboServer limboServer = plugin.getServers().getOrDefault(context.popForParameter(), null);
            if (limboServer == null)
                throw new SendComponentException(plugin.getLimboConfig().getMessages().message("invalid-limbo"));
            return limboServer;
        });
        commandHandler.registerExceptionHandler(SendComponentException.class, (actor, e) -> e.send(actor));
        commandHandler.registerDependency(NanoLimboVelocity.class, plugin);
        commandHandler.register(new ConnectionCountCommand(), new HelpCommand(), new MemoryCommand(), new StopCommand(), new StartCommand());
        return this;
    }

    @Override
    public Collection<Command> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public void register(Command command) {
        commandHandler.register(command);
    }

    @Override
    public boolean executeCommand(String input) {
        throw new UnsupportedOperationException("Cannot execute command in LampVelocityCommandHandler");
    }
}
