package com.ubivashka.limbo.config;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.configurate.ConfigurationNode;

import net.md_5.bungee.api.ChatColor;

public class MessageConfiguration {
    private final Map<String, String> messages = new HashMap<>();
    private final Map<String, MessageConfiguration> subMessages = new HashMap<>();

    public MessageConfiguration(ConfigurationNode node) {
        for (Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
            String key = entry.getKey().toString();
            ConfigurationNode childNode = entry.getValue();
            if (childNode.isMap()) {
                subMessages.put(key, new MessageConfiguration(childNode));
                continue;
            }
            messages.put(key, color(childNode.getString()));
        }
    }

    public MessageConfiguration subMessage(String key) {
        return subMessages.get(key);
    }

    public String message(String key, Object... placeholders) {
        return MessageFormat.format(messages.get(key), placeholders);
    }

    private static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
