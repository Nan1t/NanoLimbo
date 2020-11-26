package ru.nanit.limbo.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import ru.nanit.limbo.protocol.*;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.util.Logger;

import java.util.List;

public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private State.PacketRegistry mappings;

    public PacketDecoder(){
        updateState(State.HANDSHAKING);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        if (!ctx.channel().isActive() || mappings == null) return;

        ByteMessage msg = new ByteMessage(buf);
        int packetId = msg.readVarInt();
        Packet packet = mappings.getPacket(packetId);

        if (packet != null){
            try {
                packet.decode(msg);
            } catch (Exception e){
                Logger.warning("Cannot decode packet 0x%s: %s", Integer.toHexString(packetId), e.getMessage());
            }

            ctx.fireChannelRead(packet);
        } else {
            Logger.warning("Undefined incoming packet: 0x" + Integer.toHexString(packetId));
        }
    }

    public void updateState(State state){
        this.mappings = state.serverBound;
    }

}
