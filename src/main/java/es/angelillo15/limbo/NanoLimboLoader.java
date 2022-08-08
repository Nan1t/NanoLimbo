package es.angelillo15.limbo;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import ru.nanit.limbo.NanoLimbo;
import ru.nanit.limbo.server.BungeeCommandManager;
import ru.nanit.limbo.server.LimboServer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NanoLimboLoader extends Plugin {
    @Override
    public void onEnable(){
        getLogger().info("Loading NanoLimbo");
        try {
            Path path = Paths.get(this.getDataFolder().getAbsolutePath());
            getLogger().info(path.toString());
            new LimboServer(path, true, this).start();
            getLogger().info("NanoLimbo successfully loaded");
        } catch (Exception e) {
            getLogger().warning("Error loading Nanolimbo"+ e);
        }
    }
}
