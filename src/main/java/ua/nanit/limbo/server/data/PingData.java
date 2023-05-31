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

package ua.nanit.limbo.server.data;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ua.nanit.limbo.util.Colors;

import java.lang.reflect.Type;

public class PingData {

    private String version;
    private String description;
    private int protocol;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public static class Serializer implements TypeSerializer<PingData> {

        @Override
        public PingData deserialize(Type type, ConfigurationNode node) {
            PingData pingData = new PingData();
            pingData.setDescription(Colors.of(node.node("description").getString("")));
            pingData.setVersion(Colors.of(node.node("version").getString("")));
            pingData.setProtocol(node.node("protocol").getInt(-1));
            return pingData;
        }

        @Override
        public void serialize(Type type, @Nullable PingData obj, ConfigurationNode node) {

        }
    }
}