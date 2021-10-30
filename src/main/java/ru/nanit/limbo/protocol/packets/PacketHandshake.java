package ru.nanit.limbo.protocol.packets;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketIn;
import ru.nanit.limbo.protocol.registry.State;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketHandshake implements PacketIn {

    private Version version;
    private String host;
    private int port;
    private State nextState;

    public Version getVersion() {
        return version;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public State getNextState() {
        return nextState;
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        try {
            this.version = Version.of(msg.readVarInt());
        } catch (IllegalArgumentException e) {
            this.version = Version.UNDEFINED;
        }

        this.host = msg.readString();
        this.port = msg.readUnsignedShort();
        this.nextState = State.getById(msg.readVarInt());
    }
}
