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

package ua.nanit.limbo.protocol.packets.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketLoginSuccess implements PacketOut {

    private UUID uuid;
    private String username;
    private UUID sessionId;

    @Override
    public void encode(@NonNull ByteMessage msg, @NonNull Version version) {
        if (version.moreOrEqual(Version.V1_16)) {
            msg.writeUuid(this.uuid);
        } else if (version.moreOrEqual(Version.V1_7_6)) {
            msg.writeString(this.uuid.toString());
        } else {
            msg.writeString(this.uuid.toString().replace("-", ""));
        }
        msg.writeString(this.username);
        if (version.moreOrEqual(Version.V1_19)) {
            msg.writeVarInt(0);
        }
        if (version.fromTo(Version.V1_20_5, Version.V1_21)) {
            msg.writeBoolean(true);
        }
        if (version.moreOrEqual(Version.V26_2)) {
            msg.writeUuid(this.sessionId);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
