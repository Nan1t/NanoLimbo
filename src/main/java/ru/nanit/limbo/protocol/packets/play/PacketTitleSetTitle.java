package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketTitleSetTitle implements PacketOut {

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeString(title);
    }

}
