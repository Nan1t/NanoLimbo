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
import ru.nanit.limbo.world.DefaultWorld;

import java.util.concurrent.ThreadLocalRandom;

public class ClientConnection extends ChannelInboundHandlerAdapter {

    private final LimboServer server;
    private final Channel channel;

    private State state;
    private String username;

    public ClientConnection(Channel channel, LimboServer server){
        this.server = server;
        this.channel = channel;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (state.equals(State.PLAY)){
            server.decrementPlayers();
            Logger.info("Player %s disconnected", this.username);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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
            if (server.getPlayersCount() >= LimboConfig.getMaxPlayers()){
                disconnect("Too many players connected");
                return;
            }

            this.username = ((PacketLoginStart) packet).getUsername();

            PacketLoginSuccess loginSuccess = new PacketLoginSuccess();

            loginSuccess.setUuid(UuidUtil.getOfflineModeUuid(this.username));
            loginSuccess.setUsername(this.username);

            sendPacket(loginSuccess);
            updateState(State.PLAY);

            server.incrementPlayers();
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
        positionAndLook.setY(2.0);
        positionAndLook.setZ(0.0);
        positionAndLook.setYaw(90.0F);
        positionAndLook.setPitch(0.0F);
        positionAndLook.setTeleportId(ThreadLocalRandom.current().nextInt());

        PacketUpdateViewPos updateViewPos = new PacketUpdateViewPos();

        updateViewPos.setChunkX(0);
        updateViewPos.setChunkY(0);

        PacketChunkData chunkData = new PacketChunkData();

        chunkData.setChunkX(0);
        chunkData.setChunkZ(0);
        chunkData.setFullChunk(false);
        chunkData.setPrimaryBitMask(1);
        chunkData.setHeightMaps(DefaultWorld.getHeightMaps());
        chunkData.setData(new byte[0]);
        chunkData.setBlockEntities(new CompoundBinaryTag[]{CompoundBinaryTag.empty()});

        sendPacket(joinGame);
        sendPacket(positionAndLook);
        sendPacket(updateViewPos);
        sendPacket(chunkData);
        sendPacket(updateViewPos);
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
