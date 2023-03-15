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

public class PacketPlayerPositionAndLook implements PacketOut {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private int teleportId;

    public PacketPlayerPositionAndLook() {}

    public PacketPlayerPositionAndLook(double x, double y, double z, float yaw, float pitch, int teleportId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
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
            msg.writeByte(0x08);
        } else {
            msg.writeBoolean(true);
        }

        if (version.moreOrEqual(Version.V1_9)) {
            msg.writeVarInt(teleportId);
        }

        if (version.fromTo(Version.V1_17, Version.V1_19_3)) {
            msg.writeBoolean(false); // Dismount vehicle
        }
    }

}
