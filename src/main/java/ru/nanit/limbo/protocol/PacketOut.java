package ru.nanit.limbo.protocol;

public interface PacketOut extends Packet {

    @Override
    default void decode(ByteMessage msg) {
        // Can be ignored for outgoing packets
    }

}
