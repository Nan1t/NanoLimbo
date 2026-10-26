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

package ua.nanit.limbo.protocol.packets.play;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

import java.util.EnumSet;
import java.util.UUID;

/**
 * This packet was very simplified and using only for ADD_PLAYER action
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketPlayerInfo implements PacketOut {

    private int gameMode = 3;
    private String username = "";
    private UUID uuid;

    @Override
    public void encode(@NonNull ByteMessage msg, @NonNull Version version) {
        if (version.less(Version.V1_8)) {
            msg.writeString(this.username);
            msg.writeBoolean(true); // Is online
            msg.writeShort(0);
        } else {
            if (version.moreOrEqual(Version.V1_19_3)) {
                EnumSet<Action> actions = EnumSet.noneOf(Action.class);
                actions.add(Action.ADD_PLAYER);
                actions.add(Action.UPDATE_LISTED);
                actions.add(Action.UPDATE_GAMEMODE);
                msg.writeEnumSet(actions, Action.class);

                msg.writeVarInt(1); // Array length (1 element)
                msg.writeUuid(this.uuid); // UUID
                msg.writeString(this.username); //Username
                msg.writeVarInt(0); //Properties (0 is empty)

                msg.writeVarInt(this.gameMode); //Gamemode
                msg.writeBoolean(true); //Update listed
                return;
            }

            msg.writeVarInt(0); // Add player action
            msg.writeVarInt(1);
            msg.writeUuid(this.uuid);
            msg.writeString(this.username);
            msg.writeVarInt(0);
            msg.writeVarInt(this.gameMode);
            msg.writeVarInt(60);
            msg.writeBoolean(false);

            if (version.moreOrEqual(Version.V1_19)) {
                msg.writeBoolean(false);
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public enum Action {
        ADD_PLAYER,
        INITIALIZE_CHAT,
        UPDATE_GAMEMODE,
        UPDATE_LISTED,
        UPDATE_LATENCY,
        UPDATE_DISPLAY_NAME
    }
}
