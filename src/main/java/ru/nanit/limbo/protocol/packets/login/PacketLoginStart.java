package ru.nanit.limbo.protocol.packets.login;

import ru.nanit.limbo.protocol.*;

public class PacketLoginStart implements PacketIn {

    private String username;

    public String getUsername() {
        return username;
    }

    @Override
    public void decode(ByteMessage msg) {
        this.username = msg.readString();
    }

}
