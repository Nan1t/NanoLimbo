package ru.nanit.limbo.protocol.packets.login;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketIn;

public class PacketLoginPluginResponse implements PacketIn {

    private int messageId;
    private boolean successful;
    private ByteMessage data;

    public int getMessageId() {
        return messageId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public ByteMessage getData() {
        return data;
    }

    @Override
    public void decode(ByteMessage msg) {
        messageId = msg.readVarInt();
        successful = msg.readBoolean();

        if (msg.readableBytes() > 0) {
            int i = msg.readableBytes();
            data = new ByteMessage(msg.readBytes(i));
        }
    }

}
