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

import java.util.UUID;

/**
 * This packet was very simplified and using only for ADD_PLAYER action
 */
public class PacketPlayerInfo implements PacketOut {

    private int gameMode = 3;
    private String username = "";
    private UUID uuid;

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        if (version.less(Version.V1_8)) {
            msg.writeString(username);
            msg.writeBoolean(true); // Is online
            msg.writeShort(0);
        } else {
            msg.writeVarInt(0); // Add player action
            msg.writeVarInt(1);
            msg.writeUuid(uuid);
            msg.writeString(username);
            msg.writeVarInt(0);
            msg.writeVarInt(gameMode);
            msg.writeVarInt(60);
            msg.writeBoolean(false);
            if (version.moreOrEqual(Version.V1_19)) {
                msg.writeBoolean(false);
            }
        }
    }

}
