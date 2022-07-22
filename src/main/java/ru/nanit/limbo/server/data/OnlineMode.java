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

package ru.nanit.limbo.server.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class OnlineMode {

    private boolean enable;
    private boolean preventProxy;
    private String sessionServer;

    public boolean isEnable() {
        return enable;
    }

    public boolean isPreventProxy() {
        return preventProxy;
    }

    public String getSessionServer() {
        return sessionServer;
    }

    public static class Serializer implements TypeSerializer<OnlineMode> {

        @Override
        public OnlineMode deserialize(Type type, ConfigurationNode node) throws SerializationException {
            OnlineMode onlineMode = new OnlineMode();

            onlineMode.enable = node.node("enable").getBoolean(false);
            onlineMode.preventProxy = node.node("preventProxy").getBoolean(false);
            onlineMode.sessionServer = node.node("sessionServer")
                    .getString("https://sessionserver.mojang.com/session/minecraft/hasJoined");

            return onlineMode;
        }

        @Override
        public void serialize(Type type, @Nullable OnlineMode obj, ConfigurationNode node) throws SerializationException {
            //
        }
    }
}
