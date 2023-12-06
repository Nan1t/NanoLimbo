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

import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.data.BossBar;

import java.util.UUID;

/**
 * Packet for 1.9+
 */
public class PacketBossBar implements PacketOut {

    private UUID uuid;
    private BossBar bossBar;
    private int flags;

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setBossBar(BossBar bossBar) {
        this.bossBar = bossBar;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeUuid(uuid);
        msg.writeVarInt(0); // Create bossbar
        msg.writeNbtMessage(bossBar.getText(), version);
        msg.writeFloat(bossBar.getHealth());
        msg.writeVarInt(bossBar.getColor().getIndex());
        msg.writeVarInt(bossBar.getDivision().getIndex());
        msg.writeByte(flags);
    }

}
