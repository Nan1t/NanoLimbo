/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.nanit.limbo.protocol.packets.play;

import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

import java.util.List;

/**
 * Packet for 1.13+
 */
public class PacketDeclareCommands implements PacketOut {

    private List<String> commands;

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeVarInt(commands.size() * 2 + 1); // +1 because declaring root node

        // Declare root node

        msg.writeByte(0);
        msg.writeVarInt(commands.size());

        for (int i = 1; i <= commands.size() * 2; i++) {
            msg.writeVarInt(i++);
        }

        // Declare other commands

        int i = 1;
        for (String cmd : commands) {
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
