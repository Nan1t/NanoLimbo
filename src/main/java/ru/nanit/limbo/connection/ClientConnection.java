package ru.nanit.limbo.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ClientConnection extends ChannelInboundHandlerAdapter {

    private final LimboServer server;
    private final Channel channel;

    private State state;
    private Version clientVersion;

    private UUID uuid;
    private String username;

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public ClientConnection(Channel channel, LimboServer server){
        this.server = server;
        this.channel = channel;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (state.equals(State.PLAY)){
            server.getConnections().removeConnection(this);
            Logger.info("Player %s disconnected", this.username);
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
            updateState(State.getById(handshake.getNextState()));
            clientVersion = handshake.getVersion();
            Logger.debug("Pinged from " + handshake.getHost() + ":" + handshake.getPort());
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

            this.username = ((PacketLoginStart) packet).getUsername();
            this.uuid = UuidUtil.getOfflineModeUuid(this.username);

            PacketLoginSuccess loginSuccess = new PacketLoginSuccess();

            loginSuccess.setUuid(UuidUtil.getOfflineModeUuid(this.username));
            loginSuccess.setUsername(this.username);

            sendPacket(loginSuccess);
            updateState(State.PLAY);

            server.getConnections().addConnection(this);
            Logger.info("Player %s connected (%s)", this.username, channel.remoteAddress());

            sendJoinPackets();
        }
    }

    private void sendJoinPackets(){
        PacketJoinGame joinGame = new PacketJoinGame();

        joinGame.setEntityId(0);
        joinGame.setEnableRespawnScreen(true);
        joinGame.setFlat(false);
        joinGame.setGameMode(2);
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

        PacketPlayerPositionAndLook positionAndLook = new PacketPlayerPositionAndLook();

        positionAndLook.setX(server.getConfig().getSpawnPosition().getX());
        positionAndLook.setY(server.getConfig().getSpawnPosition().getY());
        positionAndLook.setZ(server.getConfig().getSpawnPosition().getZ());
        positionAndLook.setYaw(server.getConfig().getSpawnPosition().getYaw());
        positionAndLook.setPitch(server.getConfig().getSpawnPosition().getPitch());
        positionAndLook.setTeleportId(ThreadLocalRandom.current().nextInt());

        PacketPlayerInfo info = new PacketPlayerInfo();

        info.setConnection(this);
        info.setGameMode(2);

        sendPacket(joinGame);
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
