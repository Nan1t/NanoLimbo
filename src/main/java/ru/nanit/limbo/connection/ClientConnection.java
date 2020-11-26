package ru.nanit.limbo.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import ru.nanit.limbo.LimboConfig;
import ru.nanit.limbo.protocol.packets.login.*;
import ru.nanit.limbo.protocol.packets.play.*;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.protocol.pipeline.PacketDecoder;
import ru.nanit.limbo.protocol.pipeline.PacketEncoder;
import ru.nanit.limbo.protocol.packets.PacketHandshake;
import ru.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ru.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ru.nanit.limbo.protocol.packets.status.PacketStatusResponse;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.Logger;
import ru.nanit.limbo.util.UuidUtil;
import ru.nanit.limbo.world.DefaultDimension;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ClientConnection extends ChannelInboundHandlerAdapter {

    private final LimboServer server;
    private final Channel channel;

    private State state;

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
            server.removeConnection(this);
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
            State state = State.getById(handshake.getNextState());
            updateStateAndVersion(state, handshake.getVersion());
        }

        if (packet instanceof PacketStatusRequest){
            sendPacket(new PacketStatusResponse());
        }

        if (packet instanceof PacketStatusPing){
            sendPacketAndClose(packet);
        }

        if (packet instanceof PacketLoginStart){
            if (server.getConnectionsCount() >= LimboConfig.getMaxPlayers()){
                disconnect("Too many players connected");
                return;
            }

            this.username = ((PacketLoginStart) packet).getUsername();
            this.uuid = UuidUtil.getOfflineModeUuid(this.username);

            PacketLoginSuccess loginSuccess = new PacketLoginSuccess();

            loginSuccess.setUuid(UuidUtil.getOfflineModeUuid(this.username));
            loginSuccess.setUsername(this.username);

            sendPacket(loginSuccess);
            updateState(State.PLAY);

            server.addConnection(this);
            Logger.info("Player %s connected", this.username);

            startJoinProcess();
        }

        if (packet instanceof PacketKeepAlive){
            System.out.println("Get KeepAlive " + ((PacketKeepAlive)packet).getId());
        }
    }

    private void startJoinProcess(){
        PacketJoinGame joinGame = new PacketJoinGame();

        joinGame.setEntityId(0);
        joinGame.setEnableRespawnScreen(true);
        joinGame.setFlat(false);
        joinGame.setGameMode(2);
        joinGame.setHardcore(false);
        joinGame.setMaxPlayers(LimboConfig.getMaxPlayers());
        joinGame.setPreviousGameMode(-1);
        joinGame.setReducedDebugInfo(false);
        joinGame.setDebug(false);
        joinGame.setViewDistance(2);
        joinGame.setWorldName("minecraft:world");
        joinGame.setWorldNames("minecraft:world");
        joinGame.setHashedSeed(0);
        joinGame.setDimensionCodec(DefaultDimension.getCodec());
        joinGame.setDimension(DefaultDimension.getDimension());

        PacketPlayerPositionAndLook positionAndLook = new PacketPlayerPositionAndLook();

        positionAndLook.setX(0.0);
        positionAndLook.setY(0.0);
        positionAndLook.setZ(0.0);
        positionAndLook.setYaw(90.0F);
        positionAndLook.setPitch(0.0F);
        positionAndLook.setTeleportId(ThreadLocalRandom.current().nextInt());

        sendPacket(joinGame);
        sendPacket(positionAndLook);
        sendKeepAlive();
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

    public void updateStateAndVersion(State state, Version version){
        PacketDecoder decoder = channel.pipeline().get(PacketDecoder.class);
        PacketEncoder encoder = channel.pipeline().get(PacketEncoder.class);

        decoder.updateVersion(version);
        decoder.updateState(state);
        encoder.updateVersion(version);
        encoder.updateState(state);

        this.state = state;
    }
}
