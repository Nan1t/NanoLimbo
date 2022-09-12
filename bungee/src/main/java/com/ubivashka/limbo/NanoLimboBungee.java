package com.ubivashka.limbo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import com.ubivashka.limbo.command.BungeeCommandHandler;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import ru.nanit.limbo.NanoLimbo;
import ru.nanit.limbo.configuration.YamlLimboConfig;
import ru.nanit.limbo.server.LimboServer;

public class NanoLimboBungee extends Plugin {
    static {
        NanoLimbo.class.getName(); // For preventing minimizing this class
    }

    @Override
    public void onEnable() {
        saveDefaultConfiguration(this, "settings.yml");
        BungeeCommandHandler commandHandler = new BungeeCommandHandler(this);
        try {
            LimboServer server = new LimboServer(new YamlLimboConfig(getDataFolder().toPath(), getClass().getClassLoader()).load(), commandHandler,
                    getClass().getClassLoader());
            commandHandler.registerAll(server);
            server.start();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Configuration saveDefaultConfiguration(Plugin plugin, String configurationName) {
        try {
            if (!plugin.getDataFolder().exists())
                plugin.getDataFolder().mkdir();
            File configurationFile = new File(plugin.getDataFolder(), configurationName);
            if (!configurationFile.exists()) {
                try (InputStream inputStream = plugin.getResourceAsStream(configurationName)) {
                    Files.copy(inputStream, configurationFile.toPath());
                }
            }

            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
