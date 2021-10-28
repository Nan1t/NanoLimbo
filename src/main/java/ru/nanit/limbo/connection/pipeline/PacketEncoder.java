package ru.nanit.limbo.connection.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Packet;
import ru.nanit.limbo.protocol.PreEncodedPacket;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.util.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private State.PacketRegistry registry;

    public PacketEncoder(){
        updateState(State.HANDSHAKING);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        if (registry == null) return;

        ByteMessage msg = new ByteMessage(out);
        int packetId;

        if (packet instanceof PreEncodedPacket){
            packetId = registry.getPacketId(((PreEncodedPacket)packet).getWrappedPacket().getClass());
        } else {
            packetId = registry.getPacketId(packet.getClass());
        }

        if (packetId == -1){
            Logger.warning("Undefined packet class: %s", packet.getClass().getName());
            return;
        }

        msg.writeVarInt(packetId);

        try {
            packet.encode(msg);
        } catch (Exception e){
            Logger.warning("Cannot encode packet 0x%s: %s", Integer.toHexString(packetId), e.getMessage());
        }
    }

    public void updateState(State state){
        this.registry = state.clientBound;
    }

}
