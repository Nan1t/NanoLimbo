package ru.nanit.limbo.protocol;

public interface PacketIn extends Packet {

    @Override
    default void encode(ByteMessage msg) {
        // Can be ignored for incoming packets
    }

}
