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

package ua.nanit.limbo.connection;

import io.netty.buffer.Unpooled;
import ua.nanit.limbo.LimboConstants;
import ua.nanit.limbo.protocol.packets.PacketHandshake;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginRequest;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginResponse;
import ua.nanit.limbo.protocol.packets.login.PacketLoginStart;
import ua.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ua.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ua.nanit.limbo.protocol.packets.status.PacketStatusResponse;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Logger;
import ua.nanit.limbo.util.UuidUtil;

import java.util.concurrent.ThreadLocalRandom;

public class PacketHandler {

    private final LimboServer server;

    public PacketHandler(LimboServer server) {
        this.server = server;
    }

    public void handle(ClientConnection conn, PacketHandshake packet) {
        conn.updateVersion(packet.getVersion());
        conn.updateState(packet.getNextState());

        Logger.debug("Pinged from %s [%s]", conn.getAddress(),
                conn.getClientVersion().toString());

        if (server.getConfig().getInfoForwarding().isLegacy()) {
            String[] split = packet.getHost().split("\00");

            if (split.length == 3 || split.length == 4) {
                conn.setAddress(split[1]);
                conn.getGameProfile().setUuid(UuidUtil.fromString(split[2]));
            } else {
                conn.disconnectLogin("You've enabled player info forwarding. You need to connect with proxy");
            }
        } else if (server.getConfig().getInfoForwarding().isBungeeGuard()) {
            if (!conn.checkBungeeGuardHandshake(packet.getHost())) {
                conn.disconnectLogin("Invalid BungeeGuard token or handshake format");
            }
        }
    }

    public void handle(ClientConnection conn, PacketStatusRequest packet) {
        conn.sendPacket(new PacketStatusResponse(server));
    }

    public void handle(ClientConnection conn, PacketStatusPing packet) {
        conn.sendPacketAndClose(packet);
    }

    public void handle(ClientConnection conn, PacketLoginStart packet) {
        if (server.getConfig().getMaxPlayers() > 0 &&
                server.getConnections().getCount() >= server.getConfig().getMaxPlayers()) {
            conn.disconnectLogin("Too many players connected");
            return;
        }

        if (!conn.getClientVersion().isSupported()) {
            conn.disconnectLogin("Unsupported client version");
            return;
        }

        if (server.getConfig().getInfoForwarding().isModern()) {
            int loginId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            PacketLoginPluginRequest request = new PacketLoginPluginRequest();

            request.setMessageId(loginId);
            request.setChannel(LimboConstants.VELOCITY_INFO_CHANNEL);
            request.setData(Unpooled.EMPTY_BUFFER);

            conn.setVelocityLoginMessageId(loginId);
            conn.sendPacket(request);
            return;
        }

        if (!server.getConfig().getInfoForwarding().isModern()) {
            conn.getGameProfile().setUsername(packet.getUsername());
            conn.getGameProfile().setUuid(UuidUtil.getOfflineModeUuid(packet.getUsername()));
        }

        conn.fireLoginSuccess();
    }

    public void handle(ClientConnection conn, PacketLoginPluginResponse packet) {
        if (server.getConfig().getInfoForwarding().isModern()
                && packet.getMessageId() == conn.getVelocityLoginMessageId()) {

            if (!packet.isSuccessful() || packet.getData() == null) {
                conn.disconnectLogin("You need to connect with Velocity");
                return;
            }

            if (!conn.checkVelocityKeyIntegrity(packet.getData())) {
                conn.disconnectLogin("Can't verify forwarded player info");
                return;
            }

            // Order is important
            conn.setAddress(packet.getData().readString());
            conn.getGameProfile().setUuid(packet.getData().readUuid());
            conn.getGameProfile().setUsername(packet.getData().readString());

            conn.fireLoginSuccess();
        }
    }

}
