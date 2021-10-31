package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketTitleSetSubTitle implements PacketOut {

    private String subtitle;

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeString(subtitle);
    }

}
