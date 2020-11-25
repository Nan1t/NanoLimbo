package ru.nanit.limbo.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.nanit.limbo.protocol.packets.login.*;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.protocol.pipeline.PacketDecoder;
import ru.nanit.limbo.protocol.pipeline.PacketEncoder;
import ru.nanit.limbo.protocol.packets.PacketHandshake;
import ru.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ru.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ru.nanit.limbo.protocol.packets.status.PacketStatusResponse;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.server.LimboServer;
import ru.nanit.limbo.util.UuidUtil;

public class ClientConnection extends ChannelInboundHandlerAdapter {

    private final LimboServer server;
    private final Channel channel;

    private String username;

    public ClientConnection(Channel channel, LimboServer server){
        this.channel = channel;
        this.server = server;
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
            this.username = ((PacketLoginStart) packet).getUsername();

            // Limbo always in offline mode. Online mode set on proxy side
            PacketLoginSuccess loginSuccess = new PacketLoginSuccess();

            loginSuccess.setUuid(UuidUtil.getOfflineModeUuid(this.username));
            loginSuccess.setUsername(this.username);

            sendPacket(loginSuccess);
            updateState(State.PLAY);
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

    public void disconnect(){
        if (channel.isActive()){
            channel.close();
        }
    }

    public void disconnect(String reason){
        PacketDisconnect packet = new PacketDisconnect();
        packet.setReason(reason);
        sendPacketAndClose(packet);
    }

    public boolean isConnected(){
        return channel.isActive();
    }

    public void updateState(State state){
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
    }
}
