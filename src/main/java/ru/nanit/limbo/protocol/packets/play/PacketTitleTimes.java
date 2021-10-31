package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketTitleTimes implements PacketOut {

    private int fadeIn;
    private int stay;
    private int fadeOut;

    public void setFadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
    }

    public void setStay(int stay) {
        this.stay = stay;
    }

    public void setFadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeInt(fadeIn);
        msg.writeInt(stay);
        msg.writeInt(fadeOut);
    }

}
