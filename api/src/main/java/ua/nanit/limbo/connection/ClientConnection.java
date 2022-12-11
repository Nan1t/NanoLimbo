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

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jetbrains.annotations.NotNull;

import com.grack.nanojson.JsonArray;
import com.grack.nanojson.JsonObject;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParserException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ua.nanit.limbo.connection.pipeline.PacketDecoder;
import ua.nanit.limbo.connection.pipeline.PacketEncoder;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.packets.login.PacketDisconnect;
import ua.nanit.limbo.protocol.packets.play.PacketKeepAlive;
import ua.nanit.limbo.protocol.registry.State;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Logger;
import ua.nanit.limbo.util.UuidUtil;

public class ClientConnection extends ChannelInboundHandlerAdapter {

    private final LimboServer server;
    private final Channel channel;
    private final GameProfile gameProfile;

    private final PacketDecoder decoder;
    private final PacketEncoder encoder;

    private State state;
    private Version clientVersion;
    private SocketAddress address;

    private int velocityLoginMessageId = -1;

    public ClientConnection(Channel channel, LimboServer server, PacketDecoder decoder, PacketEncoder encoder) {
        this.server = server;
        this.channel = channel;
        this.decoder = decoder;
        this.encoder = encoder;
        this.address = channel.remoteAddress();
        this.gameProfile = new GameProfile();
    }

    public UUID getUuid() {
        return gameProfile.getUuid();
    }

    public String getUsername() {
        return gameProfile.getUsername();
    }

    public SocketAddress getAddress() {
        return address;
    }

    public Version getClientVersion() {
        return clientVersion;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    @Override
    public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
        if (state.equals(State.PLAY)) {
            server.getConnections().removeConnection(this);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (channel.isActive()) {
            Logger.error("Unhandled exception: ", cause);
        }
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) {
        handlePacket(msg);
    }

    public void handlePacket(Object packet) {
        if (packet instanceof Packet) {
            ((Packet) packet).handle(this, server);
        }
    }

    public void fireLoginSuccess() {
        if (server.getConfig().getInfoForwarding().isModern() && velocityLoginMessageId == -1) {
            disconnectLogin("You need to connect with Velocity");
            return;
        }

        sendPacket(server.getPacketSnapshots().getPacketLoginSuccess());
        updateState(State.PLAY);
        server.getConnections().addConnection(this);

        Runnable sendPlayPackets = () -> {
            writePacket(server.getPacketSnapshots().getPacketJoinGame());
            writePacket(server.getPacketSnapshots().getPacketPlayerAbilities());

            if (clientVersion.less(Version.V1_9)) {
                writePacket(server.getPacketSnapshots().getPacketPlayerPosAndLookLegacy());
            } else {
                writePacket(server.getPacketSnapshots().getPacketPlayerPosAndLook());
            }

            if (clientVersion.moreOrEqual(Version.V1_19_3))
                writePacket(server.getPacketSnapshots().getPacketSpawnPosition());

            if (server.getConfig().isUsePlayerList() || clientVersion.equals(Version.V1_16_4))
                writePacket(server.getPacketSnapshots().getPacketPlayerInfo());

            if (clientVersion.moreOrEqual(Version.V1_13)) {
                writePacket(server.getPacketSnapshots().getPacketDeclareCommands());

                if (server.getPacketSnapshots().getPacketPluginMessage() != null)
                    writePacket(server.getPacketSnapshots().getPacketPluginMessage());
            }

            if (server.getPacketSnapshots().getPacketBossBar() != null && clientVersion.moreOrEqual(Version.V1_9))
                writePacket(server.getPacketSnapshots().getPacketBossBar());

            if (server.getPacketSnapshots().getPacketJoinMessage() != null)
                writePacket(server.getPacketSnapshots().getPacketJoinMessage());

            if (server.getPacketSnapshots().getPacketTitleTitle() != null && clientVersion.moreOrEqual(Version.V1_8))
                writeTitle();

            if (server.getPacketSnapshots().getPacketHeaderAndFooter() != null && clientVersion.moreOrEqual(Version.V1_8))
                writePacket(server.getPacketSnapshots().getPacketHeaderAndFooter());

            sendKeepAlive();
        };

        if (clientVersion.lessOrEqual(Version.V1_7_6)) {
            this.channel.eventLoop().schedule(sendPlayPackets, 100, TimeUnit.MILLISECONDS);
        } else {
            sendPlayPackets.run();
        }
    }

    public void disconnectLogin(String reason) {
        if (isConnected() && state == State.LOGIN) {
            PacketDisconnect disconnect = new PacketDisconnect();
            disconnect.setReason(reason);
            sendPacketAndClose(disconnect);
        }
    }

    public void writeTitle() {
        if (clientVersion.moreOrEqual(Version.V1_17)) {
            writePacket(server.getPacketSnapshots().getPacketTitleTitle());
            writePacket(server.getPacketSnapshots().getPacketTitleSubtitle());
            writePacket(server.getPacketSnapshots().getPacketTitleTimes());
        } else {
            writePacket(server.getPacketSnapshots().getPacketTitleLegacyTitle());
            writePacket(server.getPacketSnapshots().getPacketTitleLegacySubtitle());
            writePacket(server.getPacketSnapshots().getPacketTitleLegacyTimes());
        }
    }

    public void sendKeepAlive() {
        if (state.equals(State.PLAY)) {
            PacketKeepAlive keepAlive = new PacketKeepAlive();
            keepAlive.setId(ThreadLocalRandom.current().nextLong());
            sendPacket(keepAlive);
        }
    }

    public void sendPacket(Object packet) {
        if (isConnected())
            channel.writeAndFlush(packet, channel.voidPromise());
    }

    public void sendPacketAndClose(Object packet) {
        if (isConnected())
            channel.writeAndFlush(packet).addListener(ChannelFutureListener.CLOSE);
    }

    public void writePacket(Object packet) {
        if (isConnected())
            channel.write(packet, channel.voidPromise());
    }

    public boolean isConnected() {
        return channel.isActive();
    }

    public void updateState(State state) {
        this.state = state;
        decoder.updateState(state);
        encoder.updateState(state);
    }

    public void updateVersion(Version version) {
        clientVersion = version;
        decoder.updateVersion(version);
        encoder.updateVersion(version);
    }

    public void setAddress(String host) {
        this.address = new InetSocketAddress(host, ((InetSocketAddress) this.address).getPort());
    }

    boolean checkBungeeGuardHandshake(String handshake) {
        String[] split = handshake.split("\00");

        if (split.length != 4)
            return false;

        String socketAddressHostname = split[1];
        UUID uuid = UuidUtil.fromString(split[2]);
        JsonArray arr;

        try {
            arr = JsonParser.array().from(split[3]);
        } catch(JsonParserException e) {
            return false;
        }

        String token = null;

        for (Object obj : arr) {
            if (obj instanceof JsonObject) {
                JsonObject prop = (JsonObject) obj;
                if (prop.getString("name").equals("bungeeguard-token")) {
                    token = prop.getString("value");
                    break;
                }
            }
        }

        if (!server.getConfig().getInfoForwarding().hasToken(token))
            return false;

        setAddress(socketAddressHostname);
        gameProfile.setUuid(uuid);

        Logger.debug("Successfully verified BungeeGuard token");

        return true;
    }

    int getVelocityLoginMessageId() {
        return velocityLoginMessageId;
    }

    void setVelocityLoginMessageId(int velocityLoginMessageId) {
        this.velocityLoginMessageId = velocityLoginMessageId;
    }

    boolean checkVelocityKeyIntegrity(ByteMessage buf) {
        byte[] signature = new byte[32];
        buf.readBytes(signature);
        byte[] data = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), data);
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(server.getConfig().getInfoForwarding().getSecretKey(), "HmacSHA256"));
            byte[] mySignature = mac.doFinal(data);
            if (!MessageDigest.isEqual(signature, mySignature))
                return false;
        } catch(InvalidKeyException | java.security.NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        int version = buf.readVarInt();
        if (version != 1)
            throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + '\001');
        return true;
    }
}
