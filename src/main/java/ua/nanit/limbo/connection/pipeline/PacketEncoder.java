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

package ua.nanit.limbo.connection.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.NonNull;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.PacketSnapshot;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.Log;
import ua.nanit.limbo.util.PacketUtils;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private State state;
    private State.PacketRegistry registry;
    private Version version;

    public PacketEncoder() {
        updateVersion(Version.getMin());
        this.state = State.HANDSHAKING;
        updateState(this.state);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        if (registry == null) return;

        ByteMessage msg = new ByteMessage(out);
        int packetId;

        if (packet instanceof PacketSnapshot packetSnapshot) {
            packetId = registry.getPacketId(packetSnapshot.getPacketClass());
        } else {
            packetId = registry.getPacketId(packet.getClass());
        }

        if (packetId == -1) {
            Log.warning("Undefined packet class: %s(%s) [%s|%s] (%d bytes)", packet.getClass().getName(), PacketUtils.toPacketId(packetId), version, state, msg.readableBytes());
            return;
        }

        msg.writeVarInt(packetId);

        try {
            packet.encode(msg, version);

            if (Log.isDebug()) {
                Log.debug("Sending %s(%s) [%s|%s] packet (%d bytes)", packet.toString(), PacketUtils.toPacketId(packetId), version, state, msg.readableBytes());
            }
        } catch (Throwable throwable) {
            throw new EncoderException("Cannot encode packet " + PacketUtils.toDetailedInfo(packet, packetId, version, state), throwable);
        }
    }

    public void updateVersion(@NonNull Version version) {
        this.version = version;
    }

    public void updateState(@NonNull State state) {
        this.state = state;
        this.registry = state.clientBound.getRegistry(version);
    }
}
