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

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNextState() {
        return nextState;
    }

    public void setNextState(int nextState) {
        this.nextState = nextState;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeVarInt(this.version.getProtocolNumber());
        msg.writeString(host);
        msg.writeShort(port);
        msg.writeVarInt(nextState);
    }

    @Override
    public void decode(ByteMessage msg) {
        this.version = Version.of(msg.readVarInt());
        this.host = msg.readString();
        this.port = msg.readUnsignedShort();
        this.nextState = msg.readVarInt();
    }
}
