package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;

import java.util.List;

public class PacketDeclareCommands implements PacketOut {

    private List<String> commands;

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public void encode(ByteMessage msg) {
        msg.writeVarInt(commands.size() * 2 + 1); // +1 because declaring root node

        // Declare root node

        msg.writeByte(0);
        msg.writeVarInt(commands.size());

        for (int i = 1; i <= commands.size() * 2; i++){
            msg.writeVarInt(i++);
        }

        // Declare other commands

        int i = 1;
        for (String cmd : commands){
            msg.writeByte(1 | 0x04);
            msg.writeVarInt(1);
            msg.writeVarInt(i + 1);
            msg.writeString(cmd);
            i++;

            msg.writeByte(2 | 0x04 | 0x10);
            msg.writeVarInt(1);
            msg.writeVarInt(i);
            msg.writeString("arg");
            msg.writeString("brigadier:string");
            msg.writeVarInt(0);
            msg.writeString("minecraft:ask_server");
            i++;
        }

        msg.writeVarInt(0);
    }

}
