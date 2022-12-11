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

import io.netty.buffer.ByteBuf;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

public class PacketLoginPluginRequest implements PacketOut {

    private int messageId;
    private String channel;
    private ByteBuf data;

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(messageId);
        msg.writeString(channel);
        msg.writeBytes(data);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
