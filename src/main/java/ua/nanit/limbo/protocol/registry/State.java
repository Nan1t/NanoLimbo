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

package ua.nanit.limbo.protocol.registry;

import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.packets.PacketHandshake;
import ua.nanit.limbo.protocol.packets.login.*;
import ua.nanit.limbo.protocol.packets.play.*;
import ua.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ua.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ua.nanit.limbo.protocol.packets.status.PacketStatusResponse;

import java.util.*;
import java.util.function.Supplier;

import static ua.nanit.limbo.protocol.registry.Version.*;

public enum State {

    HANDSHAKING(0) {
        {
            serverBound.register(PacketHandshake::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
        }
    },
    STATUS(1) {
        {
            serverBound.register(PacketStatusRequest::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
            serverBound.register(PacketStatusPing::new,
                    map(0x01, Version.getMin(), Version.getMax())
            );
            clientBound.register(PacketStatusResponse::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
            clientBound.register(PacketStatusPing::new,
                    map(0x01, Version.getMin(), Version.getMax())
            );
        }
    },
    LOGIN(2) {
        {
            serverBound.register(PacketLoginStart::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
            serverBound.register(PacketLoginPluginResponse::new,
                    map(0x02, Version.getMin(), Version.getMax())
            );
            clientBound.register(PacketDisconnect::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
            clientBound.register(PacketLoginSuccess::new,
                    map(0x02, Version.getMin(), Version.getMax())
            );
            clientBound.register(PacketLoginPluginRequest::new,
                    map(0x04, Version.getMin(), Version.getMax())
            );
        }
    },
    PLAY(3) {
        {
            serverBound.register(PacketKeepAlive::new,
                    map(0x00, V1_7_2, V1_8),
                    map(0x0B, V1_9, V1_11_1),
                    map(0x0C, V1_12, V1_12),
                    map(0x0B, V1_12_1, V1_12_2),
                    map(0x0E, V1_13, V1_13_2),
                    map(0x0F, V1_14, V1_15_2),
                    map(0x10, V1_16, V1_16_4),
                    map(0x0F, V1_17, V1_18_2),
                    map(0x11, V1_19, V1_19),
                    map(0x12, V1_19_1, V1_19_1),
                    map(0x11, V1_19_3, V1_19_3),
                    map(0x12, V1_19_4, V1_19_4)
            );

            clientBound.register(PacketDeclareCommands::new,
                    map(0x11, V1_13, V1_14_4),
                    map(0x12, V1_15, V1_15_2),
                    map(0x11, V1_16, V1_16_1),
                    map(0x10, V1_16_2, V1_16_4),
                    map(0x12, V1_17, V1_18_2),
                    map(0x0F, V1_19, V1_19_1),
                    map(0x0E, V1_19_3, V1_19_3),
                    map(0x10, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketJoinGame::new,
                    map(0x01, V1_7_2, V1_8),
                    map(0x23, V1_9, V1_12_2),
                    map(0x25, V1_13, V1_14_4),
                    map(0x26, V1_15, V1_15_2),
                    map(0x25, V1_16, V1_16_1),
                    map(0x24, V1_16_2, V1_16_4),
                    map(0x26, V1_17, V1_18_2),
                    map(0x23, V1_19, V1_19),
                    map(0x25, V1_19_1, V1_19_1),
                    map(0x24, V1_19_3, V1_19_3),
                    map(0x28, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketPluginMessage::new,
                    map(0x19, V1_13, V1_13_2),
                    map(0x18, V1_14, V1_14_4),
                    map(0x19, V1_15, V1_15_2),
                    map(0x18, V1_16, V1_16_1),
                    map(0x17, V1_16_2, V1_16_4),
                    map(0x18, V1_17, V1_18_2),
                    map(0x15, V1_19, V1_19),
                    map(0x16, V1_19_1, V1_19_1),
                    map(0x15, V1_19_3, V1_19_3),
                    map(0x17, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketPlayerAbilities::new,
                    map(0x39, V1_7_2, V1_8),
                    map(0x2B, V1_9, V1_12),
                    map(0x2C, V1_12_1, V1_12_2),
                    map(0x2E, V1_13, V1_13_2),
                    map(0x31, V1_14, V1_14_4),
                    map(0x32, V1_15, V1_15_2),
                    map(0x31, V1_16, V1_16_1),
                    map(0x30, V1_16_2, V1_16_4),
                    map(0x32, V1_17, V1_18_2),
                    map(0x2F, V1_19, V1_19),
                    map(0x31, V1_19_1, V1_19_1),
                    map(0x30, V1_19_3, V1_19_3),
                    map(0x34, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketPlayerPositionAndLook::new,
                    map(0x08, V1_7_2, V1_8),
                    map(0x2E, V1_9, V1_12),
                    map(0x2F, V1_12_1, V1_12_2),
                    map(0x32, V1_13, V1_13_2),
                    map(0x35, V1_14, V1_14_4),
                    map(0x36, V1_15, V1_15_2),
                    map(0x35, V1_16, V1_16_1),
                    map(0x34, V1_16_2, V1_16_4),
                    map(0x38, V1_17, V1_18_2),
                    map(0x36, V1_19, V1_19),
                    map(0x39, V1_19_1, V1_19_1),
                    map(0x38, V1_19_3, V1_19_3),
                    map(0x3C, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketKeepAlive::new,
                    map(0x00, V1_7_2, V1_8),
                    map(0x1F, V1_9, V1_12_2),
                    map(0x21, V1_13, V1_13_2),
                    map(0x20, V1_14, V1_14_4),
                    map(0x21, V1_15, V1_15_2),
                    map(0x20, V1_16, V1_16_1),
                    map(0x1F, V1_16_2, V1_16_4),
                    map(0x21, V1_17, V1_18_2),
                    map(0x1E, V1_19, V1_19),
                    map(0x20, V1_19_1, V1_19_1),
                    map(0x1F, V1_19_3, V1_19_3),
                    map(0x23, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketChatMessage::new,
                    map(0x02, V1_7_2, V1_8),
                    map(0x0F, V1_9, V1_12_2),
                    map(0x0E, V1_13, V1_14_4),
                    map(0x0F, V1_15, V1_15_2),
                    map(0x0E, V1_16, V1_16_4),
                    map(0x0F, V1_17, V1_18_2),
                    map(0x5F, V1_19, V1_19),
                    map(0x62, V1_19_1, V1_19_1),
                    map(0x60, V1_19_3, V1_19_3),
                    map(0x64, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketBossBar::new,
                    map(0x0C, V1_9, V1_14_4),
                    map(0x0D, V1_15, V1_15_2),
                    map(0x0C, V1_16, V1_16_4),
                    map(0x0D, V1_17, V1_18_2),
                    map(0x0A, V1_19, V1_19_3),
                    map(0x0B, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketPlayerInfo::new,
                    map(0x38, V1_7_2, V1_8),
                    map(0x2D, V1_9, V1_12),
                    map(0x2E, V1_12_1, V1_12_2),
                    map(0x30, V1_13, V1_13_2),
                    map(0x33, V1_14, V1_14_4),
                    map(0x34, V1_15, V1_15_2),
                    map(0x33, V1_16, V1_16_1),
                    map(0x32, V1_16_2, V1_16_4),
                    map(0x36, V1_17, V1_18_2),
                    map(0x34, V1_19, V1_19),
                    map(0x37, V1_19_1, V1_19_1),
                    map(0x36, V1_19_3, V1_19_3),
                    map(0x3A, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketTitleLegacy::new,
                    map(0x45, V1_8, V1_11_1),
                    map(0x47, V1_12, V1_12),
                    map(0x48, V1_12_1, V1_12_2),
                    map(0x4B, V1_13, V1_13_2),
                    map(0x4F, V1_14, V1_14_4),
                    map(0x50, V1_15, V1_15_2),
                    map(0x4F, V1_16, V1_16_4)
            );
            clientBound.register(PacketTitleSetTitle::new,
                    map(0x59, V1_17, V1_17_1),
                    map(0x5A, V1_18, V1_19),
                    map(0x5D, V1_19_1, V1_19_1),
                    map(0x5B, V1_19_3, V1_19_3),
                    map(0x5F, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketTitleSetSubTitle::new,
                    map(0x57, V1_17, V1_17_1),
                    map(0x58, V1_18, V1_19),
                    map(0x5B, V1_19_1, V1_19_1),
                    map(0x59, V1_19_3, V1_19_3),
                    map(0x5D, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketTitleTimes::new,
                    map(0x5A, V1_17, V1_17_1),
                    map(0x5B, V1_18, V1_19),
                    map(0x5E, V1_19_1, V1_19_1),
                    map(0x5C, V1_19_3, V1_19_3),
                    map(0x60, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketPlayerListHeader::new,
                    map(0x47, V1_8, V1_8),
                    map(0x48, V1_9, V1_9_2),
                    map(0x47, V1_9_4, V1_11_1),
                    map(0x49, V1_12, V1_12),
                    map(0x4A, V1_12_1, V1_12_2),
                    map(0x4E, V1_13, V1_13_2),
                    map(0x53, V1_14, V1_14_4),
                    map(0x54, V1_15, V1_15_2),
                    map(0x53, V1_16, V1_16_4),
                    map(0x5E, V1_17, V1_17_1),
                    map(0x5F, V1_18, V1_18_2),
                    map(0x60, V1_19, V1_19),
                    map(0x63, V1_19_1, V1_19_1),
                    map(0x61, V1_19_3, V1_19_3),
                    map(0x65, V1_19_4, V1_19_4)
            );
            clientBound.register(PacketSpawnPosition::new,
                    map(0x4C, V1_19_3, V1_19_3),
                    map(0x50, V1_19_4, V1_19_4)
            );
        }
    };

    private static final Map<Integer, State> STATE_BY_ID = new HashMap<>();

    static {
        for (State registry : values()) {
            STATE_BY_ID.put(registry.stateId, registry);
        }
    }

    private final int stateId;
    public final ProtocolMappings serverBound = new ProtocolMappings();
    public final ProtocolMappings clientBound = new ProtocolMappings();

    State(int stateId) {
        this.stateId = stateId;
    }

    public static State getById(int stateId) {
        return STATE_BY_ID.get(stateId);
    }

    public static class ProtocolMappings {

        private final Map<Version, PacketRegistry> registry = new HashMap<>();

        public PacketRegistry getRegistry(Version version) {
            return registry.getOrDefault(version, registry.get(getMin()));
        }

        public void register(Supplier<?> packet, Mapping... mappings) {
            for (Mapping mapping : mappings) {
                for (Version ver : getRange(mapping)) {
                    PacketRegistry reg = registry.computeIfAbsent(ver, PacketRegistry::new);
                    reg.register(mapping.packetId, packet);
                }
            }
        }

        private Collection<Version> getRange(Mapping mapping) {
            Version from = mapping.from;
            Version curr = mapping.to;

            if (curr == from)
                return Collections.singletonList(from);

            List<Version> versions = new LinkedList<>();

            while (curr != from) {
                versions.add(curr);
                curr = curr.getPrev();
            }

            versions.add(from);

            return versions;
        }

    }

    public static class PacketRegistry {

        private final Version version;
        private final Map<Integer, Supplier<?>> packetsById = new HashMap<>();
        private final Map<Class<?>, Integer> packetIdByClass = new HashMap<>();

        public PacketRegistry(Version version) {
            this.version = version;
        }

        public Version getVersion() {
            return version;
        }

        public Packet getPacket(int packetId) {
            Supplier<?> supplier = packetsById.get(packetId);
            return supplier == null ? null : (Packet) supplier.get();
        }

        public int getPacketId(Class<?> packetClass) {
            return packetIdByClass.getOrDefault(packetClass, -1);
        }

        public void register(int packetId, Supplier<?> supplier) {
            packetsById.put(packetId, supplier);
            packetIdByClass.put(supplier.get().getClass(), packetId);
        }

    }

    private static class Mapping {

        private final int packetId;
        private final Version from;
        private final Version to;

        public Mapping(int packetId, Version from, Version to) {
            this.from = from;
            this.to = to;
            this.packetId = packetId;
        }
    }

    /**
     * Map packet id to version range
     * @param packetId Packet id
     * @param from Minimal version (include)
     * @param to Last version (include)
     * @return Created mapping
     */
    private static Mapping map(int packetId, Version from, Version to) {
        return new Mapping(packetId, from, to);
    }

}
