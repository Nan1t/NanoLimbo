package ru.nanit.limbo.protocol.packets;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Packet;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketHandshake implements Packet {

    private Version version;
    private String host;
    private int port;
    private int nextState;

    public Version getVersion() {
        return version;
    }

    public String getHost() {
        return host;
    }

    public int getNextState() {
        return nextState;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(this.version.getProtocolNumber());
        msg.writeString(host);
        msg.writeShort(port);
        msg.writeVarInt(nextState);
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        this.version = Version.of(msg.readVarInt());
        this.host = msg.readString();
        this.port = msg.readUnsignedShort();
        this.nextState = msg.readVarInt();
    }
}
