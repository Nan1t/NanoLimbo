package ru.nanit.limbo.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Packet;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.util.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private State.PacketVersionRegistry.PacketIdRegistry<?> mappings;
    private Version version;

    public PacketEncoder(){
        updateVersion(Version.getMinimal());
        updateState(State.HANDSHAKING);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        if (mappings == null) return;

        ByteMessage msg = new ByteMessage(out);
        int packetId = mappings.getPacketId(packet.getClass());

        if (packetId == -1){
            Logger.warning("Undefined packet class: %s", packet.getClass().getName());
            return;
        }

        msg.writeVarInt(packetId);

        try {
            packet.encode(msg, version);
        } catch (Exception e){
            Logger.warning("Cannot encode packet 0x%s: %s", Integer.toHexString(packetId), e.getMessage());
        }
    }

    public void updateVersion(Version version){
        this.version = version;
    }

    public void updateState(State state){
        this.mappings = state.clientBound.getRegistry(version);
    }

}
