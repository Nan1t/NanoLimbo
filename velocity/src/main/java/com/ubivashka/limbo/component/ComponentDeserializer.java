package com.ubivashka.limbo.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public enum ComponentDeserializer {
    PLAIN {
        private final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

        @Override
        public Component deserialize(String text) {
            return serializer.deserialize(text);
        }
    }, GSON {
        private final GsonComponentSerializer serializer = GsonComponentSerializer.gson();

        @Override
        public Component deserialize(String text) {
            return serializer.deserialize(text);
        }
    }, GSON_LEGACY {
        private final GsonComponentSerializer serializer = GsonComponentSerializer.colorDownsamplingGson();

        @Override
        public Component deserialize(String text) {
            return serializer.deserialize(text);
        }
    }, LEGACY_AMPERSAND {
        private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

        @Override
        public Component deserialize(String text) {
            return serializer.deserialize(text);
        }
    }, LEGACY_SECTION {
        private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();

        @Override
        public Component deserialize(String text) {
            return serializer.deserialize(text);
        }
    }, MINIMESSAGE {
        private final MiniMessage serializer = MiniMessage.miniMessage();

        @Override
        public Component deserialize(String text) {
            return serializer.deserialize(text);
        }
    };

    public abstract Component deserialize(String text);
}
