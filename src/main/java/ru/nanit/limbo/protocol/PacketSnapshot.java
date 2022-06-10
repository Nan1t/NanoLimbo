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

/**
 * PacketSnapshot encodes packet to byt array for each MC version.
 * Some versions have same snapshot, so there are mappings
 * to avoid storing same bytes array for different versions
 */
public class PacketSnapshot implements PacketOut {

    private final PacketOut packet;
    private final Map<Version, byte[]> versionMessages = new HashMap<>();
    private final Map<Version, Version> mappings = new HashMap<>();

    public PacketSnapshot(PacketOut packet) {
        this.packet = packet;
    }

    public PacketOut getWrappedPacket() {
        return packet;
    }

    public void encode() {
        Map<Integer, Version> hashes = new HashMap<>();

        for (Version version : Version.values()) {
            if (version.equals(Version.UNDEFINED)) continue;

            ByteMessage encodedMessage = ByteMessage.create();
            packet.encode(encodedMessage, version);

            int hash = encodedMessage.hashCode();
            Version hashed = hashes.get(hash);

            if (hashed != null) {
                mappings.put(version, hashed);
            } else {
                hashes.put(hash, version);
                mappings.put(version, version);
                versionMessages.put(version, encodedMessage.toByteArray());
            }

            encodedMessage.release();
        }
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        Version mapped = mappings.get(version);
        byte[] message = versionMessages.get(mapped);

        if (message != null)
            msg.writeBytes(message);
        else
            throw new IllegalArgumentException("No mappings for version " + version);
    }

    @Override
    public String toString() {
        return packet.getClass().getSimpleName();
    }

    public static PacketSnapshot of(PacketOut packet) {
        PacketSnapshot snapshot = new PacketSnapshot(packet);
        snapshot.encode();
        return snapshot;
    }
}
