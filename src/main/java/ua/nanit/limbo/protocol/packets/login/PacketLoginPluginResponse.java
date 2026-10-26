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

package ua.nanit.limbo.protocol.packets.login;

import io.netty.handler.codec.DecoderException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketIn;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketLoginPluginResponse implements PacketIn {

    private int messageId;
    private boolean successful;
    private byte[] data;

    @Override
    public void decode(@NonNull ByteMessage msg, @NonNull Version version) {
        this.messageId = msg.readVarInt();
        this.successful = msg.readBoolean();

        if (msg.readableBytes() > 0) {
            int readableBytes = msg.readableBytes();
            if (readableBytes > Short.MAX_VALUE) {
                throw new DecoderException("Cannot receive payload larger than " + Short.MAX_VALUE);
            }
            this.data = new byte[msg.readableBytes()];
            msg.readBytes(this.data);
        }
    }

    @Override
    public void handle(@NonNull ClientConnection conn, @NonNull LimboServer server) {
        server.getPacketHandler().handle(conn, this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
