package com.ubivashka.limbo.config.model;

import java.io.File;
import java.lang.reflect.Type;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import com.ubivashka.limbo.NanoLimboBungee;
import com.ubivashka.limbo.config.ConfigurationUtil;

import ua.nanit.limbo.configuration.LimboConfig;
import ua.nanit.limbo.configuration.YamlLimboConfig;

public class BungeeLimboServer {
    private final LimboConfig limboConfig;
    private final String limboName;
    private boolean restricted;
    private String motd;

    public BungeeLimboServer(LimboConfig limboConfig, String limboName, boolean restricted, String motd) {
        this.limboConfig = limboConfig;
        this.limboName = limboName;
        this.restricted = restricted;
        this.motd = motd;
    }

    public LimboConfig getLimboConfig() {
        return limboConfig;
    }

    public String getLimboName() {
        return limboName;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public String getMotd() {
        return motd;
    }

    public static class BungeeLimboSerializer implements TypeSerializer<BungeeLimboServer> {
        private static final NanoLimboBungee PLUGIN = NanoLimboBungee.getInstance();

        @Override
        public BungeeLimboServer deserialize(Type type, ConfigurationNode node) throws SerializationException {
            Object keyObject = node.key();
            if (keyObject == null && node.node("name").virtual())
                throw new SerializationException("Cannot load limbo without name!");
            String limboName = keyObject == null ? node.node("name").getString() : keyObject.toString();
            String motd = node.node("motd").getString("");
            boolean restricted = node.node("restricted").getBoolean();
            String settingsFolder = node.node("settingsFolder").getString("");
            LimboConfig limboConfig;
            try {
                File limboSettingsFolder = new File(PLUGIN.getDataFolder().getPath(), settingsFolder);
                ConfigurationUtil.saveDefaultConfig(PLUGIN.getClass().getClassLoader(), limboSettingsFolder, "settings.yml");
                limboConfig = new YamlLimboConfig(limboSettingsFolder.toPath(), PLUGIN.getClass().getClassLoader()).load();
            } catch(Exception e) {
                e.printStackTrace();
                throw new SerializationException();
            }
            return new BungeeLimboServer(limboConfig, limboName, restricted, motd);
        }

        @Override
        public void serialize(Type type, @Nullable BungeeLimboServer obj, ConfigurationNode node) throws SerializationException {
        }
    }
}
