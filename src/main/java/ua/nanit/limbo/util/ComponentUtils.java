/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.kyori.adventure.text.serializer.json.legacyimpl.NBTLegacyHoverEventSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.option.OptionSchema;
import ua.nanit.limbo.protocol.registry.Version;

@UtilityClass
public class ComponentUtils {

    private static final GsonComponentSerializer PRE_1_16_GSON_SERIALIZER =
            GsonComponentSerializer.builder()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // before 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.VALUE_FIELD)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    // before 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.FALSE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer PRE_1_20_3_GSON_SERIALIZER =
            GsonComponentSerializer.builder()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, true)
                                    // before 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.FALSE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.FALSE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer PRE_1_21_5_GSON_SERIALIZER =
            GsonComponentSerializer.builder()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.CAMEL_CASE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, true)
                                    // after 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.TRUE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.TRUE)
                                    // before 1.21.5
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.TRUE)
                                    .build()
                    )
                    .build();
    private static final GsonComponentSerializer MODERN_GSON_SERIALIZER =
            GsonComponentSerializer.builder()
                    .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get())
                    .options(
                            OptionSchema.globalSchema().stateBuilder()
                                    // general options
                                    .value(JSONOptions.EMIT_CLICK_URL_HTTPS, Boolean.TRUE)
                                    // after 1.16
                                    .value(JSONOptions.EMIT_RGB, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_EVENT_TYPE, JSONOptions.HoverEventValueMode.SNAKE_CASE)
                                    .value(JSONOptions.EMIT_CLICK_EVENT_TYPE, JSONOptions.ClickEventValueMode.SNAKE_CASE)
                                    // after 1.20.3
                                    .value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_ID_AS_INT_ARRAY, Boolean.TRUE)
                                    // after 1.21.5
                                    .value(JSONOptions.EMIT_HOVER_SHOW_ENTITY_KEY_AS_TYPE_AND_UUID_AS_ID, Boolean.FALSE)
                                    .value(JSONOptions.VALIDATE_STRICT_EVENTS, Boolean.TRUE)
                                    .value(JSONOptions.EMIT_CHANGE_PAGE_CLICK_EVENT_PAGE_AS_STRING, Boolean.FALSE)
                                    .build()
                    )
                    .build();

    private static final MiniMessage MINI_MESSAGE_SERIALIZER = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_HEX_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();
    private static final LegacyComponentSerializer LEGACY_SECTION_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText();

    @NonNull
    public static GsonComponentSerializer getJsonChatSerializer(@NonNull Version version) {
        if (version.moreOrEqual(Version.V1_21_5)) {
            return MODERN_GSON_SERIALIZER;
        } else if (version.moreOrEqual(Version.V1_20_3)) {
            return PRE_1_21_5_GSON_SERIALIZER;
        } else if (version.moreOrEqual(Version.V1_16)) {
            return PRE_1_20_3_GSON_SERIALIZER;
        }
        return PRE_1_16_GSON_SERIALIZER;
    }

    @NonNull
    public static String toLegacyString(@NonNull Component component) {
        return LEGACY_SECTION_SERIALIZER.serialize(component);
    }

    @NonNull
    public static String toPlainString(@NonNull Component component) {
        return PLAIN_TEXT_SERIALIZER.serialize(component);
    }

    @NonNull
    public static String toMiniMessageString(@NonNull Component component) {
        return MINI_MESSAGE_SERIALIZER.serialize(component);
    }

    @NonNull
    public static Component parse(@NonNull String text) {
        if (text.isEmpty()) {
            return Component.empty();
        }

        // Old json like deserialization
        if (JsonUtils.isValidJson(text)) {
            try {
                return MODERN_GSON_SERIALIZER.deserialize(text.replace(LegacyComponentSerializer.AMPERSAND_CHAR, LegacyComponentSerializer.SECTION_CHAR));
            } catch (Exception ignored) {
            }
        }

        if (text.indexOf(LegacyComponentSerializer.SECTION_CHAR) != -1) {
            text = text.replace(LegacyComponentSerializer.SECTION_CHAR, LegacyComponentSerializer.AMPERSAND_CHAR);
        }

        if (text.indexOf(LegacyComponentSerializer.AMPERSAND_CHAR) == -1) {
            return MINI_MESSAGE_SERIALIZER.deserialize(text);
        }

        return MINI_MESSAGE_SERIALIZER.deserialize(
                MINI_MESSAGE_SERIALIZER.serialize(LEGACY_HEX_SERIALIZER.deserialize(text))
                        .replace("\\<", "<")
                        .replace("\\>", ">")
        );
    }

}
