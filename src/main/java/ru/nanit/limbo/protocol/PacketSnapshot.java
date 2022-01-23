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

package ru.nanit.limbo.protocol;

import ru.nanit.limbo.protocol.registry.Version;

import java.util.HashMap;
import java.util.Map;

public class PacketSnapshot implements PacketOut {

    private final PacketOut packet;
    private final Map<Version, byte[]> versionMessages;

    public PacketSnapshot(PacketOut packet) {
        this.packet = packet;
        this.versionMessages = new HashMap<>();
    }

    public PacketOut getWrappedPacket() {
        return packet;
    }

    public PacketSnapshot encodePacket() {
        for (Version version : Version.values()) {
            ByteMessage encodedMessage = ByteMessage.create();
            packet.encode(encodedMessage, version);
            byte[] message = encodedMessage.toByteArray();
            versionMessages.put(version, message);
            encodedMessage.release();
        }

        return this;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        byte[] message = versionMessages.get(version);

        if (message != null) {
            msg.writeBytes(message);
        }
    }

    @Override
    public String toString() {
        return packet.getClass().getSimpleName();
    }

    public static PacketSnapshot of(PacketOut packet) {
        return new PacketSnapshot(packet).encodePacket();
    }
}
