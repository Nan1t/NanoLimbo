package ru.nanit.limbo.connection;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.nanit.limbo.LimboConstants;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PreEncodedPacket;
import ru.nanit.limbo.protocol.packets.login.*;
import ru.nanit.limbo.protocol.packets.play.*;
import ru.nanit.limbo.connection.pipeline.PacketDecoder;
import ru.nanit.limbo.connection.pipeline.PacketEncoder;
import ru.nanit.limbo.protocol.packets.PacketHandshake;
import ru.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ru.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ru.nanit.limbo.protocol.packets.status.PacketStatusResponse;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.Logger;
import ru.nanit.limbo.util.UuidUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ClientConnection extends ChannelInboundHandlerAdapter {

    private static PreEncodedPacket PACKET_LOGIN_SUCCESS;
    private static PreEncodedPacket PACKET_JOIN_GAME;
    private static PreEncodedPacket PACKET_PLAYER_ABILITIES;
    private static PreEncodedPacket PACKET_PLAYER_INFO;
    private static PreEncodedPacket PACKET_DECLARE_COMMANDS;
    private static PreEncodedPacket PACKET_PLAYER_POS;
    private static PreEncodedPacket PACKET_JOIN_MESSAGE;
    private static PreEncodedPacket PACKET_BOSS_BAR;

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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        handlePacket(msg);
    }

    public void handlePacket(Object packet) {
        if (packet instanceof PacketHandshake) {
            PacketHandshake handshake = (PacketHandshake) packet;
            clientVersion = handshake.getVersion();

            updateStateAndVersion(handshake.getNextState(), clientVersion);

            Logger.debug("Pinged from %s [%s]", address, clientVersion.toString());

            if (server.getConfig().getInfoForwarding().isLegacy()) {
                String[] split = handshake.getHost().split("\00");

                if (split.length == 3 || split.length == 4) {
                    setAddress(split[1]);
                    gameProfile.setUuid(UuidUtil.fromString(split[2]));
                } else {
                    disconnectLogin("You've enabled player info forwarding. You need to connect with proxy");
                }
            }
            return;
        }

        if (packet instanceof PacketStatusRequest) {
            sendPacket(new PacketStatusResponse(server));
            return;
        }

        if (packet instanceof PacketStatusPing) {
            sendPacketAndClose(packet);
            return;
        }

        if (packet instanceof PacketLoginStart) {
            if (server.getConnections().getCount() >= server.getConfig().getMaxPlayers()) {
                disconnectLogin("Too many players connected");
                return;
            }

            if (!clientVersion.isSupported()) {
                disconnectLogin("Unsupported client version");
                return;
            }

            if (server.getConfig().getInfoForwarding().isModern()) {
                velocityLoginMessageId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
                PacketLoginPluginRequest request = new PacketLoginPluginRequest();
                request.setMessageId(velocityLoginMessageId);
                request.setChannel(LimboConstants.VELOCITY_INFO_CHANNEL);
                request.setData(Unpooled.EMPTY_BUFFER);
                sendPacket(request);
                return;
            }

            if (!server.getConfig().getInfoForwarding().isModern()) {
                gameProfile.setUsername(((PacketLoginStart)packet).getUsername());
                gameProfile.setUuid(UuidUtil.getOfflineModeUuid(getUsername()));
            }

            fireLoginSuccess();
            return;
        }

        if (packet instanceof PacketLoginPluginResponse) {
            PacketLoginPluginResponse response = (PacketLoginPluginResponse) packet;

            if (server.getConfig().getInfoForwarding().isModern()
                    && response.getMessageId() == velocityLoginMessageId) {

                if (!response.isSuccessful() || response.getData() == null) {
                    disconnectLogin("You need to connect with Velocity");
                    return;
                }

                if (!checkVelocityKeyIntegrity(response.getData())) {
                    disconnectLogin("Can't verify forwarded player info");
                    return;
                }

                // Order is important
                setAddress(response.getData().readString());
                gameProfile.setUuid(response.getData().readUuid());
                gameProfile.setUsername(response.getData().readString());

                fireLoginSuccess();
            }
        }
    }

    private void fireLoginSuccess() {
        if (server.getConfig().getInfoForwarding().isModern() && velocityLoginMessageId == -1) {
            disconnectLogin("You need to connect with Velocity");
            return;
        }

        writePacket(PACKET_LOGIN_SUCCESS);
        setPlayState();

        server.getConnections().addConnection(this);

        writePacket(PACKET_JOIN_GAME);
        writePacket(PACKET_PLAYER_ABILITIES);
        writePacket(PACKET_PLAYER_POS);
        writePacket(PACKET_PLAYER_INFO);

        if (clientVersion.moreOrEqual(Version.V1_13)){
            writePacket(PACKET_DECLARE_COMMANDS);
        }

        if (PACKET_BOSS_BAR != null && clientVersion.moreOrEqual(Version.V1_9))
            writePacket(PACKET_BOSS_BAR);

        if (PACKET_JOIN_MESSAGE != null)
            writePacket(PACKET_JOIN_MESSAGE);

        sendKeepAlive();
    }

    public void disconnectLogin(String reason) {
        if (isConnected() && state == State.LOGIN) {
            PacketDisconnect disconnect = new PacketDisconnect();
            disconnect.setReason(reason);
            sendPacketAndClose(disconnect);
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
        if (isConnected()) channel.write(packet, channel.voidPromise());
    }

    public boolean isConnected() {
        return channel.isActive();
    }

    private void setPlayState() {
        this.state = State.PLAY;
        decoder.updateState(this.state);
        encoder.updateState(this.state);
    }

    public void updateStateAndVersion(State state, Version version) {
        this.state = state;
        decoder.updateVersion(version);
        decoder.updateState(state);
        encoder.updateVersion(version);
        encoder.updateState(state);
    }

    private void setAddress(String host) {
        this.address = new InetSocketAddress(host, ((InetSocketAddress)this.address).getPort());
    }

    private boolean checkVelocityKeyIntegrity(ByteMessage buf) {
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
        } catch (InvalidKeyException |java.security.NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        int version = buf.readVarInt();
        if (version != 1)
            throw new IllegalStateException("Unsupported forwarding version " + version + ", wanted " + '\001');
        return true;
    }

    public static void initPackets(LimboServer server) {
        final String username = server.getConfig().getPingData().getVersion();
        final UUID uuid = UuidUtil.getOfflineModeUuid(username);

        PacketLoginSuccess loginSuccess = new PacketLoginSuccess();
        loginSuccess.setUsername(username);
        loginSuccess.setUuid(uuid);

        PacketJoinGame joinGame = new PacketJoinGame();
        joinGame.setEntityId(0);
        joinGame.setEnableRespawnScreen(true);
        joinGame.setFlat(false);
        joinGame.setGameMode(server.getConfig().getGameMode());
        joinGame.setHardcore(false);
        joinGame.setMaxPlayers(server.getConfig().getMaxPlayers());
        joinGame.setPreviousGameMode(-1);
        joinGame.setReducedDebugInfo(true);
        joinGame.setDebug(false);
        joinGame.setViewDistance(2);
        joinGame.setWorldName("minecraft:world");
        joinGame.setWorldNames("minecraft:world");
        joinGame.setHashedSeed(0);
        joinGame.setDimensionRegistry(server.getDimensionRegistry());

        PacketPlayerAbilities playerAbilities = new PacketPlayerAbilities();
        playerAbilities.setFlyingSpeed(0.0F);
        playerAbilities.setFlags(0x02);
        playerAbilities.setFieldOfView(0.1F);

        PacketPlayerPositionAndLook positionAndLook = new PacketPlayerPositionAndLook();
        positionAndLook.setX(server.getConfig().getSpawnPosition().getX());
        positionAndLook.setY(server.getConfig().getSpawnPosition().getY());
        positionAndLook.setZ(server.getConfig().getSpawnPosition().getZ());
        positionAndLook.setYaw(server.getConfig().getSpawnPosition().getYaw());
        positionAndLook.setPitch(server.getConfig().getSpawnPosition().getPitch());
        positionAndLook.setTeleportId(ThreadLocalRandom.current().nextInt());

        PacketPlayerInfo info = new PacketPlayerInfo();
        info.setUsername(username);
        info.setGameMode(server.getConfig().getGameMode());
        info.setUuid(uuid);

        PacketDeclareCommands declareCommands = new PacketDeclareCommands();
        declareCommands.setCommands(Collections.singletonList("limbo"));

        PACKET_LOGIN_SUCCESS = PreEncodedPacket.of(loginSuccess);
        PACKET_JOIN_GAME = PreEncodedPacket.of(joinGame);
        PACKET_PLAYER_ABILITIES = PreEncodedPacket.of(playerAbilities);
        PACKET_PLAYER_POS = PreEncodedPacket.of(positionAndLook);
        PACKET_PLAYER_INFO = PreEncodedPacket.of(info);
        PACKET_DECLARE_COMMANDS = PreEncodedPacket.of(declareCommands);

        if (server.getConfig().isUseJoinMessage()) {
            PacketChatMessage joinMessage = new PacketChatMessage();
            joinMessage.setJsonData(server.getConfig().getJoinMessage());
            joinMessage.setPosition(PacketChatMessage.Position.CHAT);
            joinMessage.setSender(UUID.randomUUID());
            PACKET_JOIN_MESSAGE = PreEncodedPacket.of(joinMessage);
        }

        if (server.getConfig().isUseBossBar()) {
            PacketBossBar bossBar = new PacketBossBar();
            bossBar.setBossBar(server.getConfig().getBossBar());
            bossBar.setUuid(UUID.randomUUID());
            PACKET_BOSS_BAR = PreEncodedPacket.of(bossBar);
        }
    }
}
