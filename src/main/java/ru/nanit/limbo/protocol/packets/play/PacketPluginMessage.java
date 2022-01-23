package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketPluginMessage implements PacketOut {

    private String channel;
    private String message;

    public void setChannel(String channel){
        this.channel = channel;
    }
    public void setMessage(String message){
        this.message = message;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeString(channel);
        msg.writeString(message);
    }
}
