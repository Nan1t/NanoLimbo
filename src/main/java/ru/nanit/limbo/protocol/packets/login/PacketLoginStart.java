package ru.nanit.limbo.protocol.packets.login;

import ru.nanit.limbo.protocol.*;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketLoginStart implements PacketIn {

    private String username;

    public String getUsername() {
        return username;
    }

    @Override
    public void decode(ByteMessage msg, Version version) {
        this.username = msg.readString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
