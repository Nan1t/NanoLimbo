package ru.nanit.limbo.protocol;

public interface Packet {

    void encode(ByteMessage msg);

    void decode(ByteMessage msg);

}
