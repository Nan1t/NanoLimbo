package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;

public class PacketPlayerAbilities implements PacketOut {

    private int flags = 0x02;
    private float flyingSpeed = 0.0F;
    private float fieldOfView = 0.1F;

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public void setFlyingSpeed(float flyingSpeed) {
        this.flyingSpeed = flyingSpeed;
    }

    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeByte(flags);
        msg.writeFloat(flyingSpeed);
        msg.writeFloat(fieldOfView);
    }

}
