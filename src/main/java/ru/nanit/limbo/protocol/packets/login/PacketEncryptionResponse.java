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

package ru.nanit.limbo.protocol.packets.login;

import ru.nanit.limbo.connection.ClientConnection;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketIn;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.server.LimboServer;

/**
 * 0x01	Login	Server	Shared Secret Length	VarInt	Length of Shared Secret.
 * Shared Secret	Byte Array	Shared Secret value, encrypted with the server's public key.
 * Verify Token Length	VarInt	Length of Verify Token.
 * Verify Token	Byte Array	Verify Token value, encrypted with the same public key as the shared secret.
 */
public class PacketEncryptionResponse implements PacketIn {

    private byte[] sharedSecret;
    private byte[] verifyToken;

    public byte[] getSharedSecret() {
        return sharedSecret;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        sharedSecret = msg.readBytesArray();
        verifyToken = msg.readBytesArray();
    }

    @Override
    public void handle(ClientConnection conn, LimboServer server) {
        server.getPacketHandler().handle(conn, this);
    }
}
