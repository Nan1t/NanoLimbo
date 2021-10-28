package ru.nanit.limbo.protocol;

import ru.nanit.limbo.protocol.registry.Version;

import java.util.HashMap;
import java.util.Map;

public class PreEncodedPacket implements PacketOut {

    private final PacketOut packet;
    private final Map<Version, byte[]> versionMessages;

    public PreEncodedPacket(PacketOut packet) {
        this.packet = packet;
        this.versionMessages = new HashMap<>();
    }

    public PacketOut getWrappedPacket() {
        return packet;
    }

    public PreEncodedPacket encodePacket() {
        for (Version version : Version.values()) {
            ByteMessage encodedMessage = ByteMessage.create();
            packet.encode(encodedMessage, version);
            byte[] message = encodedMessage.toByteArray();
            versionMessages.put(version, message);
            encodedMessage.release();
        }

        return this;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        byte[] message = versionMessages.get(version);

        if (message != null) {
            msg.writeBytes(message);
        }
    }

    public static PreEncodedPacket of(PacketOut packet) {
        return new PreEncodedPacket(packet).encodePacket();
    }
}
