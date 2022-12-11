package com.ubivashka.limbo.command;

import java.util.Collection;
import java.util.Collections;

import com.ubivashka.limbo.NanoLimboBungee;
import com.ubivashka.limbo.command.exception.BungeeExceptionHandler;

import revxrsal.commands.CommandHandler;
import revxrsal.commands.bungee.core.BungeeHandler;
import revxrsal.commands.exception.SendMessageException;
import ua.nanit.limbo.server.Command;
import ua.nanit.limbo.server.LimboServer;

public class LampBungeeCommandHandler implements ua.nanit.limbo.server.CommandHandler<Command> {
    private final NanoLimboBungee plugin;
    private final CommandHandler commandHandler;

    public LampBungeeCommandHandler(NanoLimboBungee plugin) {
        this.plugin = plugin;
        commandHandler = new BungeeHandler(plugin).disableStackTraceSanitizing().setExceptionHandler(new BungeeExceptionHandler(plugin));
    }

    public LampBungeeCommandHandler registerAll() {
        commandHandler.registerValueResolver(LimboServer.class, context -> {
            LimboServer limboServer = plugin.getServers().getOrDefault(context.popForParameter(), null);
            if (limboServer == null)
                throw new SendMessageException(plugin.getLimboConfig().getMessages().message("invalid-limbo"));
            return limboServer;
        });
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
        throw new UnsupportedOperationException("Cannot execute command in LampBungeeCommandHandler");
    }
}
