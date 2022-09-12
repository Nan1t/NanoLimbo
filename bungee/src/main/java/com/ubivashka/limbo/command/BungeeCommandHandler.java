package com.ubivashka.limbo.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.ubivashka.limbo.NanoLimboBungee;

import net.md_5.bungee.api.ProxyServer;
import ru.nanit.limbo.server.Command;
import ru.nanit.limbo.server.CommandHandler;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.server.commands.CmdConn;
import ru.nanit.limbo.server.commands.CmdHelp;
import ru.nanit.limbo.server.commands.CmdMem;
import ru.nanit.limbo.server.commands.CmdStop;

public class BungeeCommandHandler implements CommandHandler<Command> {
    private final List<Command> commands = new ArrayList<>();
    private final NanoLimboBungee plugin;

    public BungeeCommandHandler(NanoLimboBungee plugin) {
        this.plugin = plugin;
    }

    @Override
    public Collection<Command> getCommands() {
        return Collections.unmodifiableCollection(commands);
    }

    @Override
    public void register(Command command) {
        new BungeeCommandWrapper(command);
    }

    @Override
    public boolean executeCommand(String input) {
        return ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), input);
    }

    public void registerAll(LimboServer server) {
        register(new CmdHelp(server));
        register(new CmdConn(server));
        register(new CmdMem());
        register(new CmdStop());
    }
}
