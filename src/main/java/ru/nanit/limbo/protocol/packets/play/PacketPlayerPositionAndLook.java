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

public class PacketPlayerPositionAndLook implements PacketOut {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private byte flags = 0x08;
    private int teleportId;

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public void setTeleportId(int teleportId) {
        this.teleportId = teleportId;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeDouble(x);
        msg.writeDouble(y + (version.less(Version.V1_8) ? 1.62F : 0));
        msg.writeDouble(z);
        msg.writeFloat(yaw);
        msg.writeFloat(pitch);

        if (version.moreOrEqual(Version.V1_8)) {
            msg.writeByte(flags);
        } else {
            msg.writeBoolean(true);
        }

        if (version.moreOrEqual(Version.V1_9)) {
            msg.writeVarInt(teleportId);
        }

        if (version.moreOrEqual(Version.V1_17)) {
            msg.writeBoolean(false); // Dismount vehicle
        }
    }

}
