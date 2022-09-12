package com.ubivashka.limbo.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import net.md_5.bungee.api.plugin.Plugin;

public class ConfigurationUtil {
    private ConfigurationUtil() {
    }

    public static File saveDefaultConfig(Plugin plugin, String configurationName) {
        return saveDefaultConfig(plugin.getClass().getClassLoader(), plugin.getDataFolder(), configurationName);
    }

    public static File saveDefaultConfig(ClassLoader classLoader, File configurationFolder, String configurationName) {
        try {
            configurationFolder.mkdirs();
            File configurationFile = new File(configurationFolder, configurationName);
            if (!configurationFile.exists()) {
                try (InputStream inputStream = classLoader.getResourceAsStream(configurationName)) {
                    if (inputStream == null)
                        throw new IllegalArgumentException("Configuration with name " + configurationName + " not found in resources!");
                    Files.copy(inputStream, configurationFile.toPath());
                }
            }
            return configurationFile;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
