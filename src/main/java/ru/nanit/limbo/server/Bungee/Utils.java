package ru.nanit.limbo.server.Bungee;

import es.angelillo15.limbo.NanoLimboLoader;
import net.md_5.bungee.api.ProxyServer;
import ru.nanit.limbo.server.BungeeCommandManager;
import ru.nanit.limbo.server.LimboServer;

public class Utils {
    public static void RegisterCommands(NanoLimboLoader nanoLimbo, LimboServer limboServer){
        ProxyServer.getInstance().getPluginManager().registerCommand(nanoLimbo, new BungeeCommandManager(limboServer));
    }
}
