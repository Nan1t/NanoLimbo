package ru.nanit.limbo.protocol;

import ru.nanit.limbo.protocol.registry.Version;

public class PreEncodedPacket implements PacketOut {

    private final PacketOut packet;
    private byte[] message;

    public PreEncodedPacket(PacketOut packet) {
        this.packet = packet;
    }

    public PacketOut getWrappedPacket() {
        return packet;
    }

    public PreEncodedPacket encodePacket() {
        ByteMessage encodedMessage = ByteMessage.create();
        packet.encode(encodedMessage, );
        this.message = encodedMessage.toByteArray();
        encodedMessage.release();
        return this;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeBytes(message);
    }

    public static PreEncodedPacket of(PacketOut packet) {
        return new PreEncodedPacket(packet).encodePacket();
    }
}
