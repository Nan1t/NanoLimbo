package ru.nanit.limbo.protocol.packets.status;

import ru.nanit.limbo.LimboConfig;
import ru.nanit.limbo.protocol.*;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketStatusResponse implements PacketOut {

    private static final String TEMPLATE = "{ \"version\": { \"name\": \"%s\", \"protocol\": %d }, \"players\": { \"max\": %d, \"online\": %d, \"sample\": [] }, \"description\": %s }";

    private int online;

    public PacketStatusResponse(){ }

    public PacketStatusResponse(int online){
        this.online = online;
    }

    @Override
    public void encode(ByteMessage msg) {
        String ver = LimboConfig.getPingData().getVersion();
        String desc = LimboConfig.getPingData().getDescription();
        String json = getResponseJson(ver, Version.getCurrentSupported().getProtocolNumber(),
                LimboConfig.getMaxPlayers(), online, desc);
        msg.writeString(json);
    }

    private String getResponseJson(String version, int protocol, int maxPlayers, int online, String description){
        return String.format(TEMPLATE, version, protocol, maxPlayers, online, description);
    }
}
