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

package ru.nanit.limbo.protocol.packets;

import ru.nanit.limbo.connection.ClientConnection;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketIn;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.server.LimboServer;

public class PacketHandshake implements PacketIn {

    private Version version;
    private String host;
    private int port;
    private State nextState;

    public Version getVersion() {
        return version;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public State getNextState() {
        return nextState;
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        try {
            this.version = Version.of(msg.readVarInt());
        } catch(IllegalArgumentException e) {
            this.version = Version.UNDEFINED;
        }

        this.host = msg.readString();
        this.port = msg.readUnsignedShort();
        this.nextState = State.getById(msg.readVarInt());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void handle(ClientConnection conn, LimboServer server) {
        server.getPacketHandler().handle(conn, this);
    }
}
