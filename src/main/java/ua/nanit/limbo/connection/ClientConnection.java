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

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.NetUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import ua.nanit.limbo.connection.pipeline.PacketDecoder;
import ua.nanit.limbo.connection.pipeline.PacketEncoder;
import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.PacketSnapshot;
import ua.nanit.limbo.protocol.packets.login.PacketLoginDisconnect;
import ua.nanit.limbo.protocol.packets.play.PacketDisconnect;
import ua.nanit.limbo.protocol.packets.play.PacketKeepAlive;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;
import ua.nanit.limbo.util.ComponentUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Getter
public class ClientConnection extends ChannelInboundHandlerAdapter {

    private final LimboServer server;
    private final Channel channel;
    private final GameProfile gameProfile;

    private final PacketDecoder decoder;
    private final PacketEncoder encoder;

    private State state;
    private Version clientVersion;
    private SocketAddress address;

    @Setter
    private int velocityLoginMessageId = -1;
    private boolean knownPacksReceived;

    public ClientConnection(@NonNull Channel channel,
                            @NonNull LimboServer server,
                            @NonNull PacketDecoder decoder,
                            @NonNull PacketEncoder encoder) {
        this.server = server;
        this.channel = channel;
        this.decoder = decoder;
        this.encoder = encoder;
        this.address = channel.remoteAddress();
        this.gameProfile = new GameProfile();
    }

    @Nullable
    public UUID getUuid() {
        return gameProfile.getUuid();
    }

    @Nullable
    public String getUsername() {
        return gameProfile.getUsername();
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        server.getConnections().removeConnection(this);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (channel.isActive()) {
            if (isExpectedDisconnect(cause)) {
                String message = String.valueOf(cause.getMessage()).replace("\r", "").replace("\n", "");
                Log.debug("Expected disconnect: %s: %s", cause.getClass().getSimpleName(), message);
            } else {
                Log.error("Encountered exception", cause);
            }

            ctx.close();
        }
    }

    private boolean isExpectedDisconnect(Throwable cause) {
        if (cause instanceof IOException || cause instanceof ReadTimeoutException) {
            return true;
        }
        if (!(cause instanceof DecoderException)
                || (state != null && state != State.STATUS)) {
            return false;
        }
        while (cause instanceof DecoderException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getCause() == null
                && (cause instanceof DecoderException || cause.getClass() == IndexOutOfBoundsException.class);
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
        handlePacket(msg);
    }

    public void handlePacket(@NonNull Object packet) {
        if (packet instanceof Packet) {
            ((Packet) packet).handle(this, server);
        }
    }

    public void fireLoginSuccess() {
        if (!isConnected()) {
            return;
        }
        if (server.getConfig().getInfoForwarding().isModern() && velocityLoginMessageId == -1) {
            disconnect(Component.text("You need to connect with Velocity", NamedTextColor.RED));
            return;
        }

        ClientConnection oldConnection = this.server.getConnections().addConnection(this);
        if (oldConnection != null) {
            oldConnection.disconnect(Component.text("You logged in from another location", NamedTextColor.RED));
        }

        sendPacket(PacketSnapshots.PACKET_LOGIN_SUCCESS);

        // Preparing for configuration mode
        if (clientVersion.moreOrEqual(Version.V1_20_2)) {
            updateEncoderState(State.CONFIGURATION);
            return;
        }

        spawnPlayer();
    }

    public void spawnPlayer() {
        updateState(State.PLAY);

        Runnable sendPlayPackets = () -> {
            writePacket(PacketSnapshots.PACKET_JOIN_GAME);
            writePacket(PacketSnapshots.PACKET_PLAYER_ABILITIES);

            if (clientVersion.less(Version.V1_9)) {
                writePacket(PacketSnapshots.PACKET_PLAYER_POS_AND_LOOK_LEGACY);
            } else {
                writePacket(PacketSnapshots.PACKET_PLAYER_POS_AND_LOOK);
            }

            if (clientVersion.moreOrEqual(Version.V1_19_3)) {
                writePacket(PacketSnapshots.PACKET_SPAWN_POSITION);
            }

            if (server.getConfig().isUsePlayerList() || clientVersion.equals(Version.V1_16_4)) {
                writePacket(PacketSnapshots.PACKET_PLAYER_INFO);
            }

            if (clientVersion.moreOrEqual(Version.V1_13)) {
                writePacket(PacketSnapshots.PACKET_DECLARE_COMMANDS);

                if (PacketSnapshots.PACKET_PLUGIN_MESSAGE != null) {
                    writePacket(PacketSnapshots.PACKET_PLUGIN_MESSAGE);
                }
            }

            if (PacketSnapshots.PACKET_BOSS_BAR != null && clientVersion.moreOrEqual(Version.V1_9)) {
                writePacket(PacketSnapshots.PACKET_BOSS_BAR);
            }

            if (PacketSnapshots.PACKET_JOIN_MESSAGE != null) {
                writePacket(PacketSnapshots.PACKET_JOIN_MESSAGE);
            }

            if (PacketSnapshots.PACKET_TITLE_TITLE != null && clientVersion.moreOrEqual(Version.V1_8)) {
                writeTitle();
            }

            if (PacketSnapshots.PACKET_ACTION_BAR != null && clientVersion.moreOrEqual(Version.V1_8)) {
                if (clientVersion.moreOrEqual(Version.V1_17)) {
                    writePacket(PacketSnapshots.PACKET_ACTION_BAR);
                } else if (clientVersion.moreOrEqual(Version.V1_11)) {
                    writePacket(PacketSnapshots.PACKET_ACTION_BAR_LEGACY);
                } else {
                    writePacket(PacketSnapshots.PACKET_ACTION_BAR_CHAT);
                }
            }

            if (PacketSnapshots.PACKET_HEADER_AND_FOOTER != null && clientVersion.moreOrEqual(Version.V1_8)) {
                writePacket(PacketSnapshots.PACKET_HEADER_AND_FOOTER);
            }

            if (clientVersion.moreOrEqual(Version.V1_20_3)) {
                writePacket(PacketSnapshots.PACKET_START_WAITING_CHUNKS);

                writePackets(PacketSnapshots.PACKETS_CHUNKS);
            }

            sendKeepAlive();
        };

        if (clientVersion.lessOrEqual(Version.V1_7_6)) {
            this.channel.eventLoop().schedule(sendPlayPackets, 100, TimeUnit.MILLISECONDS);
        } else {
            sendPlayPackets.run();
        }
    }

    public void onLoginAcknowledgedReceived() {
        updateState(State.CONFIGURATION);

        if (PacketSnapshots.PACKET_PLUGIN_MESSAGE != null) {
            writePacket(PacketSnapshots.PACKET_PLUGIN_MESSAGE);
        }

        if (clientVersion.moreOrEqual(Version.V1_20_5)) {
            sendPacket(PacketSnapshots.PACKET_KNOWN_PACKS);
            return;
        }

        writePacket(PacketSnapshots.PACKET_REGISTRY_DATA);

        sendPacket(PacketSnapshots.PACKET_FINISH_CONFIGURATION);
    }

    public void onKnownPacksReceived() {
        // Each reply resends the full registry, so a repeat would amplify traffic.
        if (knownPacksReceived) {
            return;
        }
        knownPacksReceived = true;
        List<PacketSnapshot> registry = PacketSnapshots.getPacketsRegistryData(this.clientVersion);
        if (registry != null) {
            writePackets(registry);
        }

        writePacket(PacketSnapshots.PACKET_UPDATE_TAGS);

        sendPacket(PacketSnapshots.PACKET_FINISH_CONFIGURATION);
    }

    private void writePackets(@NonNull List<PacketSnapshot> packets) {
        for (PacketSnapshot packet : packets) {
            writePacket(packet);
        }
    }

    public void disconnect(@NonNull Component reason) {
        if (!isConnected()) {
            return;
        }

        String name = getUsername();
        Log.debug("%s kicked: %s", (name != null ? name : this.address), ComponentUtils.toPlainString(reason));

        if (!(this.state == State.LOGIN || this.state == State.CONFIGURATION || this.state == State.PLAY)) {
            this.channel.close();
            return;
        }

        Packet packet;
        if (this.state == State.LOGIN) {
            PacketLoginDisconnect packetLoginDisconnect = new PacketLoginDisconnect();
            packetLoginDisconnect.setReason(reason);
            packet = packetLoginDisconnect;
        } else {
            PacketDisconnect packetDisconnect = new PacketDisconnect();
            packetDisconnect.setReason(reason);
            packet = packetDisconnect;
        }

        sendPacketAndClose(packet);
    }

    public void writeTitle() {
        if (clientVersion.moreOrEqual(Version.V1_17)) {
            writePacket(PacketSnapshots.PACKET_TITLE_TITLE);
            writePacket(PacketSnapshots.PACKET_TITLE_SUBTITLE);
            writePacket(PacketSnapshots.PACKET_TITLE_TIMES);
        } else {
            writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_TITLE);
            writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_SUBTITLE);
            writePacket(PacketSnapshots.PACKET_TITLE_LEGACY_TIMES);
        }
    }

    public void sendKeepAlive() {
        if (state.equals(State.PLAY)) {
            PacketKeepAlive keepAlive = new PacketKeepAlive();
            keepAlive.setId(ThreadLocalRandom.current().nextLong());
            sendPacket(keepAlive);
        }
    }

    public void sendPacket(@NonNull Object packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet, channel.voidPromise());
        }
    }

    public void sendPacketAndClose(@NonNull Object packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void writePacket(@NonNull Object packet) {
        if (isConnected()) {
            channel.write(packet, channel.voidPromise());
        }
    }

    public boolean isConnected() {
        return channel.isActive();
    }

    public void updateState(@NonNull State state) {
        this.state = state;
        decoder.updateState(state);
        encoder.updateState(state);
    }

    public void updateEncoderState(@NonNull State state) {
        encoder.updateState(state);
    }

    public void updateVersion(@NonNull Version version) {
        clientVersion = version;
        decoder.updateVersion(version);
        encoder.updateVersion(version);
    }

    public void setAddress(@NonNull String host) {
        if (!(this.address instanceof InetSocketAddress inetSocketAddress)) {
            return;
        }
        int port = inetSocketAddress.getPort();
        InetAddress ip = NetUtil.createInetAddressFromIpAddressString(host);
        this.address = ip != null ? new InetSocketAddress(ip, port) : InetSocketAddress.createUnresolved(host, port);
    }

}
