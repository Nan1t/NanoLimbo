package es.angelillo15.limbo;

import net.md_5.bungee.api.ProxyServer;
import ru.nanit.limbo.NanoLimbo;
import ru.nanit.limbo.server.BungeeCommandManager;

public class NanoLimboLoader extends NanoLimbo {
    @Override
    public void onEnable(){
        getLogger().info("Loading NanoLimbo");
        start();
    }
}
