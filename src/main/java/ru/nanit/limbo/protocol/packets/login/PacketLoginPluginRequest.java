package ru.nanit.limbo.protocol.packets.login;

import io.netty.buffer.ByteBuf;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketLoginPluginRequest implements PacketOut {

    private int messageId;
    private String channel;
    private ByteBuf data;

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(messageId);
        msg.writeString(channel);
        msg.writeBytes(data);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
