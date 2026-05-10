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
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ua.nanit.limbo.LimboConstants;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.packets.PacketHandshake;
import ua.nanit.limbo.protocol.packets.configuration.PacketFinishConfiguration;
import ua.nanit.limbo.protocol.packets.configuration.PacketKnownPacks;
import ua.nanit.limbo.protocol.packets.login.PacketLoginAcknowledged;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginRequest;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginResponse;
import ua.nanit.limbo.protocol.packets.login.PacketLoginStart;
import ua.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ua.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ua.nanit.limbo.protocol.packets.status.PacketStatusResponse;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;
import ua.nanit.limbo.util.ComponentUtils;
import ua.nanit.limbo.util.UUIDUtils;
import ua.nanit.limbo.util.ForwardingUtils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class PacketHandler {

    private final LimboServer server;

    public void handle(@NonNull ClientConnection conn, @NonNull PacketHandshake packet) {
        conn.updateVersion(packet.getVersion());

        switch (packet.getIntent()) {
            case STATUS -> {
                conn.updateState(State.STATUS);

                Log.debug("Pinged from %s [%s]", conn.getAddress(), conn.getClientVersion().toString());
            }
            case LOGIN, TRANSFER -> {
                conn.updateState(State.LOGIN);

                if (!conn.getClientVersion().isSupported()) {
                    conn.disconnect(Component.text("Unsupported client version", NamedTextColor.RED));
                    return;
                }

                if (this.server.getConfig().getInfoForwarding().isLegacy()) {
                    if (!applyLegacyForwarding(conn, packet.getHost())) {
                        conn.disconnect(Component.text("You've enabled player info forwarding. You need to connect with proxy", NamedTextColor.RED));
                    }
                } else if (this.server.getConfig().getInfoForwarding().isBungeeGuard()) {
                    if (!ForwardingUtils.checkBungeeGuardHandshake(conn, packet.getHost())) {
                        conn.disconnect(Component.text("Invalid BungeeGuard token or handshake format", NamedTextColor.RED));
                    }
                }
            }
            default -> conn.disconnect(Component.text("Invalid handshake intent!", NamedTextColor.RED));
        }
    }

    private boolean applyLegacyForwarding(@NonNull ClientConnection conn, @NonNull String handshake) {
        String[] split = handshake.split("\00");
        if (split.length < 3) {
            return false;
        }

        if (applyLegacyForwarding(conn, split[1], split[2])) {
            return true;
        }

        return split.length >= 4 && applyLegacyForwarding(conn, split[2], split[3]);
    }

    private boolean applyLegacyForwarding(@NonNull ClientConnection conn,
                                          @NonNull String address,
                                          @NonNull String uuid) {
        if (address.isBlank() || address.startsWith("[") || address.startsWith("{")) {
            return false;
        }

        try {
            UUID parsedUuid = UUIDUtils.fromString(uuid);
            conn.setAddress(address);
            conn.getGameProfile().setUuid(parsedUuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketStatusRequest packet) {
        int protocol;
        int staticProtocol = this.server.getConfig().getPingData().getProtocol();

        if (staticProtocol > 0) {
            protocol = staticProtocol;
        } else {
            protocol = this.server.getConfig().getInfoForwarding().isNone()
                    ? conn.getClientVersion().getProtocolNumber()
                    : Version.getMax().getProtocolNumber();
        }

        PacketStatusResponse packetStatusResponse = new PacketStatusResponse();
        packetStatusResponse.setResponse(createStatusResponse(protocol));
        conn.sendPacket(packetStatusResponse);
    }

    @NonNull
    private PacketStatusResponse.Response createStatusResponse(int protocol) {
        Component version = this.server.getConfig().getPingData().getVersion();
        Component description = this.server.getConfig().getPingData().getDescription();

        PacketStatusResponse.Response response = new PacketStatusResponse.Response();
        response.setVersion(new PacketStatusResponse.Response.Protocol(ComponentUtils.toLegacyString(version), protocol));
        response.setPlayers(new PacketStatusResponse.Response.Players(
                this.server.getConfig().getMaxPlayers(),
                this.server.getConnections().getCount(),
                new PacketStatusResponse.Response.PlayerInfo[0]
        ));
        response.setDescription(description);
        return response;
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketStatusPing packet) {
        conn.sendPacketAndClose(packet);
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketLoginStart packet) {
        if (server.getConfig().getMaxPlayers() > 0 &&
                server.getConnections().getCount() >= server.getConfig().getMaxPlayers()) {
            conn.disconnect(Component.text("Too many players connected", NamedTextColor.RED));
            return;
        }

        if (server.getConfig().getInfoForwarding().isModern()) {
            int loginId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
            ByteMessage msg = new ByteMessage(Unpooled.buffer());
            try {
                PacketLoginPluginRequest request = new PacketLoginPluginRequest();
                request.setMessageId(loginId);
                request.setChannel(LimboConstants.VELOCITY_INFO_CHANNEL);
                msg.writeByte(ForwardingUtils.VELOCITY_MAX_SUPPORTED_FORWARDING_VERSION);
                request.setData(msg);

                conn.setVelocityLoginMessageId(loginId);
                conn.sendPacket(request);
            } finally {
                msg.release();
            }
            return;
        }

        if (!server.getConfig().getInfoForwarding().isModern()) {
            conn.getGameProfile().setUsername(packet.getUsername());
            conn.getGameProfile().setUuid(UUIDUtils.getOfflineModeUuid(packet.getUsername()));
        }

        conn.fireLoginSuccess();
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketLoginPluginResponse packet) {
        if (server.getConfig().getInfoForwarding().isModern()
                && packet.getMessageId() == conn.getVelocityLoginMessageId()) {

            if (!packet.isSuccessful() || packet.getData() == null) {
                conn.disconnect(Component.text("You need to connect with Velocity", NamedTextColor.RED));
                return;
            }

            ByteMessage msg = packet.getData();
            if (!ForwardingUtils.checkVelocityKeyIntegrity(conn, msg)) {
                conn.disconnect(Component.text("Can't verify forwarded player info", NamedTextColor.RED));
                return;
            }

            int version = msg.readVarInt();
            if (version > ForwardingUtils.VELOCITY_MAX_SUPPORTED_FORWARDING_VERSION) {
                conn.disconnect(Component.text("Unsupported forwarding version " + version + ", wanted upto " + ForwardingUtils.VELOCITY_MAX_SUPPORTED_FORWARDING_VERSION, NamedTextColor.RED));
                return;
            }

            String address = msg.readString();
            UUID uuid = msg.readUuid();
            String userName = msg.readString();

            conn.setAddress(address);
            GameProfile gameProfile = conn.getGameProfile();
            gameProfile.setUuid(uuid);
            gameProfile.setUsername(userName);

            conn.fireLoginSuccess();
        }
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketLoginAcknowledged packet) {
        conn.onLoginAcknowledgedReceived();
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketFinishConfiguration packet) {
        conn.spawnPlayer();
    }

    public void handle(@NonNull ClientConnection conn, @NonNull PacketKnownPacks packet) {
        conn.onKnownPacksReceived();
    }
}
