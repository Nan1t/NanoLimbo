package ru.nanit.limbo.connection;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.nanit.limbo.LimboConstants;
import ru.nanit.limbo.protocol.packets.login.*;
import ru.nanit.limbo.protocol.packets.play.*;
import ru.nanit.limbo.protocol.pipeline.PacketDecoder;
import ru.nanit.limbo.protocol.pipeline.PacketEncoder;
import ru.nanit.limbo.protocol.packets.PacketHandshake;
import ru.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ru.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ru.nanit.limbo.protocol.packets.status.PacketStatusResponse;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.Logger;
import ru.nanit.limbo.util.UuidUtil;
import ru.nanit.limbo.util.VelocityUtil;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ClientConnection extends ChannelInboundHandlerAdapter {

    private final LimboServer server;
    private final Channel channel;
    private final GameProfile profile;

    private State state;
    private Version clientVersion;
    private SocketAddress address;

    private int velocityLoginMessageId = -1;

    public ClientConnection(Channel channel, LimboServer server){
        this.server = server;
        this.channel = channel;
        this.address = channel.remoteAddress();
        this.profile = new GameProfile();
    }

    public UUID getUuid() {
        return profile.getUuid();
    }

    public String getUsername() {
        return profile.getUsername();
    }

    private void setAddress(String host){
        this.address = new InetSocketAddress(host, ((InetSocketAddress)this.address).getPort());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (state.equals(State.PLAY)){
            server.getConnections().removeConnection(this);
            Logger.info("Player %s disconnected", getUsername());
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (channel.isActive()){
            Logger.error("Unhandled exception: %s", cause.getMessage());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        handlePacket(msg);
    }

    public void handlePacket(Object packet){
        if (packet instanceof PacketHandshake){
            PacketHandshake handshake = (PacketHandshake) packet;
            clientVersion = handshake.getVersion();
            updateState(State.getById(handshake.getNextState()));
            Logger.debug("Pinged from " + address);

            if (server.getConfig().getInfoForwarding().isLegacy()){
                String[] split = handshake.getHost().split("\00");

                if (split.length == 3 || split.length == 4){
                    setAddress(split[1]);
                    profile.setUuid(UuidUtil.fromString(split[2]));
                } else {
                    disconnect("You've enabled player info forwarding. You need to connect with proxy");
                }
            }
        }

        if (packet instanceof PacketStatusRequest){
            sendPacket(new PacketStatusResponse(server));
        }

        if (packet instanceof PacketStatusPing){
            sendPacketAndClose(packet);
        }

        if (packet instanceof PacketLoginStart){
            if (server.getConnections().getCount() >= server.getConfig().getMaxPlayers()){
                disconnect("Too many players connected");
                return;
            }

            if (!clientVersion.equals(Version.getCurrentSupported())){
                disconnect("Incompatible client version");
                return;
            }

            if (server.getConfig().getInfoForwarding().isModern()){
                velocityLoginMessageId = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
                PacketLoginPluginRequest request = new PacketLoginPluginRequest();
                request.setMessageId(velocityLoginMessageId);
                request.setChannel(LimboConstants.VELOCITY_INFO_CHANNEL);
                request.setData(Unpooled.EMPTY_BUFFER);
                sendPacket(request);
                return;
            }

            if (!server.getConfig().getInfoForwarding().isModern()){
                profile.setUsername(((PacketLoginStart) packet).getUsername());

                if (profile.getUuid() == null)
                    profile.setUuid(UuidUtil.getOfflineModeUuid(getUsername()));
            }

            fireLoginSuccess();
        }

        if (packet instanceof PacketLoginPluginResponse){
            PacketLoginPluginResponse response = (PacketLoginPluginResponse) packet;

            if (server.getConfig().getInfoForwarding().isModern()
                    && response.getMessageId() == velocityLoginMessageId){

                if (!response.isSuccessful() || response.getData() == null){
                    disconnect("You need to connect with Velocity");
                    return;
                }

                if (!VelocityUtil.checkIntegrity(response.getData())) {
                    disconnect("Can't verify forwarded player info");
                    return;
                }

                setAddress(response.getData().readString());
                profile.setUuid(response.getData().readUuid());
                profile.setUsername(response.getData().readString());

                fireLoginSuccess();
            }
        }
    }

    private void fireLoginSuccess(){
        if (server.getConfig().getInfoForwarding().isModern() && velocityLoginMessageId == -1){
            disconnect("You need to connect with Velocity");
            return;
        }

        PacketLoginSuccess loginSuccess = new PacketLoginSuccess();
        loginSuccess.setUuid(UuidUtil.getOfflineModeUuid(getUsername()));
        loginSuccess.setUsername(getUsername());
        sendPacket(loginSuccess);

        updateState(State.PLAY);
        server.getConnections().addConnection(this);

        Logger.info("Player %s connected (%s)", getUsername(), address);

        sendJoinPackets();
    }

    private void sendJoinPackets(){
        PacketJoinGame joinGame = new PacketJoinGame();

        joinGame.setEntityId(0);
        joinGame.setEnableRespawnScreen(true);
        joinGame.setFlat(false);
        joinGame.setGameMode(server.getConfig().getGameMode());
        joinGame.setHardcore(false);
        joinGame.setMaxPlayers(server.getConfig().getMaxPlayers());
        joinGame.setPreviousGameMode(-1);
        joinGame.setReducedDebugInfo(false);
        joinGame.setDebug(false);
        joinGame.setViewDistance(2);
        joinGame.setWorldName("minecraft:world");
        joinGame.setWorldNames("minecraft:world");
        joinGame.setHashedSeed(0);
        joinGame.setDimensionCodec(server.getDimensionRegistry().getCodec());
        joinGame.setDimension(server.getDimensionRegistry().getDefaultDimension());

        PacketPlayerAbilities abilities = new PacketPlayerAbilities();

        abilities.setFlyingSpeed(0.0F);
        abilities.setFlags(0x02);
        abilities.setFieldOfView(0.1F);

        PacketPlayerPositionAndLook positionAndLook = new PacketPlayerPositionAndLook();

        positionAndLook.setX(server.getConfig().getSpawnPosition().getX());
        positionAndLook.setY(server.getConfig().getSpawnPosition().getY());
        positionAndLook.setZ(server.getConfig().getSpawnPosition().getZ());
        positionAndLook.setYaw(server.getConfig().getSpawnPosition().getYaw());
        positionAndLook.setPitch(server.getConfig().getSpawnPosition().getPitch());
        positionAndLook.setTeleportId(ThreadLocalRandom.current().nextInt());

        PacketPlayerInfo info = new PacketPlayerInfo();

        info.setConnection(this);
        info.setGameMode(server.getConfig().getGameMode());

        sendPacket(joinGame);
        sendPacket(abilities);
        sendPacket(positionAndLook);
        sendPacket(info);

        sendKeepAlive();

        if (server.getJoinMessage() != null){
            sendPacket(server.getJoinMessage());
        }

        if (server.getJoinBossBar() != null){
            sendPacket(server.getJoinBossBar());
        }
    }

    public void disconnect(String reason){
        if (isConnected() && state == State.LOGIN){
            PacketDisconnect disconnect = new PacketDisconnect();
            disconnect.setReason(reason);
            sendPacketAndClose(disconnect);
        }
    }

    public void sendKeepAlive(){
        if (state.equals(State.PLAY)){
            PacketKeepAlive keepAlive = new PacketKeepAlive();
            keepAlive.setId(ThreadLocalRandom.current().nextLong());
            sendPacket(keepAlive);
        }
    }

    public void sendPacket(Object packet){
        if (isConnected())
            channel.writeAndFlush(packet, channel.voidPromise());
    }

    public void sendPacketAndClose(Object packet){
        if (isConnected())
            channel.writeAndFlush(packet).addListener(ChannelFutureListener.CLOSE);
    }

    public boolean isConnected(){
        return channel.isActive();
    }

    public void updateState(State state){
        this.state = state;

        channel.pipeline().get(PacketDecoder.class).updateState(state);
        channel.pipeline().get(PacketEncoder.class).updateState(state);
    }
}
