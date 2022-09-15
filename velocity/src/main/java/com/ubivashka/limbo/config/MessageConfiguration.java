package com.ubivashka.limbo.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.configurate.ConfigurationNode;

import com.ubivashka.limbo.component.ComponentDeserializer;

import net.kyori.adventure.text.Component;

public class MessageConfiguration {
    private final Map<String, Component> messages = new HashMap<>();
    private final Map<String, MessageConfiguration> subMessages = new HashMap<>();

    public MessageConfiguration(ConfigurationNode node) {
        ComponentDeserializer deserializer = ComponentDeserializer.valueOf(node.node("deserializer").getString(ComponentDeserializer.LEGACY_AMPERSAND.name()));
        for (Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()) {
            String key = entry.getKey().toString();
            ConfigurationNode childNode = entry.getValue();
            if (childNode.isMap()) {
                subMessages.put(key, new MessageConfiguration(childNode));
                continue;
            }
            messages.put(key, deserializer.deserialize(childNode.getString()));
        }
    }

    public MessageConfiguration subMessage(String key) {
        return subMessages.get(key);
    }

    public Component message(String key, Object... placeholders) {
        Component component = messages.get(key);
        for (int i = 0; i < placeholders.length; i += 2) {
            String placeholder = placeholders[i].toString();
            String replacement = placeholders[i + 1].toString();
            component = component.replaceText(builder -> builder.matchLiteral(placeholder).replacement(replacement));
        }
        return component;
    }
}
