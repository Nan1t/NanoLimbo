package com.ubivashka.limbo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ubivashka.limbo.command.LampBungeeCommandHandler;
import com.ubivashka.limbo.config.LimboConfig;
import com.ubivashka.limbo.config.model.BungeeLimboServer;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import ru.nanit.limbo.NanoLimbo;
import ru.nanit.limbo.server.Command;
import ru.nanit.limbo.server.CommandHandler;
import ru.nanit.limbo.server.LimboServer;

public class NanoLimboBungee extends Plugin {
    static {
        NanoLimbo.class.getName(); // For preventing minimizing this class
    }

    private static NanoLimboBungee instance;
    private final Map<String, LimboServer> servers = new HashMap<>();
    private LimboConfig limboConfig;

    @Override
    public void onEnable() {
        instance = this;
        limboConfig = new LimboConfig(this);
        CommandHandler<Command> commandHandler = new LampBungeeCommandHandler(this).registerAll();
        try {
            for (BungeeLimboServer bungeeLimboServer : limboConfig.getServers()) {
                LimboServer server = new LimboServer(bungeeLimboServer.getLimboConfig(), commandHandler,
                        getClass().getClassLoader());

                ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(bungeeLimboServer.getLimboName(), bungeeLimboServer.getLimboConfig()
                        .getAddress(), bungeeLimboServer.getMotd(), bungeeLimboServer.isRestricted());
                servers.put(serverInfo.getName(), server);
                ProxyServer.getInstance()
                        .getServers()
                        .put(serverInfo.getName(),
                                serverInfo);
                ProxyServer.getInstance().getConfig().getServers().put(serverInfo
                        .getName(), serverInfo);

                server.start();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, LimboServer> getServers() {
        return Collections.unmodifiableMap(servers);
    }

    public LimboConfig getLimboConfig() {
        return limboConfig;
    }

    public static NanoLimboBungee getInstance() {
        return instance;
    }
}
