package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

import java.util.UUID;

public class PacketChatMessage implements PacketOut {

    private String jsonData;
    private Position position;
    private UUID sender;

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeString(jsonData);
        msg.writeByte(position.index);
        msg.writeUuid(sender);
    }

    public enum Position {

        CHAT(0),
        SYSTEM_MESSAGE(1),
        ACTION_BAR(2);

        private final int index;

        Position(int index) {
            this.index = index;
        }

    }

}
