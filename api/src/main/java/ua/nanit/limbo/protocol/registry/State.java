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
import ru.nanit.limbo.protocol.packets.login.*;
import ru.nanit.limbo.protocol.packets.play.*;
import ua.nanit.limbo.protocol.packets.play.PacketBossBar;
import ua.nanit.limbo.protocol.packets.play.PacketChatMessage;
import ua.nanit.limbo.protocol.packets.play.PacketJoinGame;
import ua.nanit.limbo.protocol.packets.play.PacketKeepAlive;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerPositionAndLook;
import ua.nanit.limbo.protocol.packets.play.PacketPluginMessage;
import ua.nanit.limbo.protocol.packets.play.PacketTitleSetSubTitle;
import ua.nanit.limbo.protocol.packets.play.PacketTitleSetTitle;
import ua.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ua.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ua.nanit.limbo.protocol.packets.status.PacketStatusResponse;
import ua.nanit.limbo.protocol.packets.login.PacketDisconnect;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginRequest;
import ua.nanit.limbo.protocol.packets.login.PacketLoginPluginResponse;
import ua.nanit.limbo.protocol.packets.login.PacketLoginStart;
import ua.nanit.limbo.protocol.packets.login.PacketLoginSuccess;
import ua.nanit.limbo.protocol.packets.play.PacketDeclareCommands;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerAbilities;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerInfo;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerListHeader;
import ua.nanit.limbo.protocol.packets.play.PacketTitleLegacy;
import ua.nanit.limbo.protocol.packets.play.PacketTitleTimes;

import java.util.*;
import java.util.function.Supplier;

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
                    map(0x00, Version.V1_8, Version.V1_8),
                    map(0x0B, Version.V1_9, Version.V1_11_1),
                    map(0x0C, Version.V1_12, Version.V1_12),
                    map(0x0B, Version.V1_12_1, Version.V1_12_2),
                    map(0x0E, Version.V1_13, Version.V1_13_2),
                    map(0x0F, Version.V1_14, Version.V1_15_2),
                    map(0x10, Version.V1_16, Version.V1_16_4),
                    map(0x0F, Version.V1_17, Version.V1_18_2),
                    map(0x11, Version.V1_19, Version.V1_19),
                    map(0x12, Version.V1_19_1, Version.V1_19_1)
            );

            clientBound.register(PacketDeclareCommands::new,
                    map(0x11, Version.V1_13, Version.V1_14_4),
                    map(0x12, Version.V1_15, Version.V1_15_2),
                    map(0x11, Version.V1_16, Version.V1_16_1),
                    map(0x10, Version.V1_16_2, Version.V1_16_4),
                    map(0x12, Version.V1_17, Version.V1_18_2),
                    map(0x0F, Version.V1_19, Version.V1_19_1)
            );
            clientBound.register(PacketJoinGame::new,
                    map(0x01, Version.V1_8, Version.V1_8),
                    map(0x23, Version.V1_9, Version.V1_12_2),
                    map(0x25, Version.V1_13, Version.V1_14_4),
                    map(0x26, Version.V1_15, Version.V1_15_2),
                    map(0x25, Version.V1_16, Version.V1_16_1),
                    map(0x24, Version.V1_16_2, Version.V1_16_4),
                    map(0x26, Version.V1_17, Version.V1_18_2),
                    map(0x23, Version.V1_19, Version.V1_19),
                    map(0x25, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketPluginMessage::new,
                    map(0x19, Version.V1_13, Version.V1_13_2),
                    map(0x18, Version.V1_14, Version.V1_14_4),
                    map(0x19, Version.V1_15, Version.V1_15_2),
                    map(0x18, Version.V1_16, Version.V1_16_1),
                    map(0x17, Version.V1_16_2, Version.V1_16_4),
                    map(0x18, Version.V1_17, Version.V1_18_2),
                    map(0x15, Version.V1_19, Version.V1_19),
                    map(0x16, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketPlayerAbilities::new,
                    map(0x39, Version.V1_8, Version.V1_8),
                    map(0x2B, Version.V1_9, Version.V1_12),
                    map(0x2C, Version.V1_12_1, Version.V1_12_2),
                    map(0x2E, Version.V1_13, Version.V1_13_2),
                    map(0x31, Version.V1_14, Version.V1_14_4),
                    map(0x32, Version.V1_15, Version.V1_15_2),
                    map(0x31, Version.V1_16, Version.V1_16_1),
                    map(0x30, Version.V1_16_2, Version.V1_16_4),
                    map(0x32, Version.V1_17, Version.V1_18_2),
                    map(0x2F, Version.V1_19, Version.V1_19),
                    map(0x31, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketPlayerPositionAndLook::new,
                    map(0x08, Version.V1_8, Version.V1_8),
                    map(0x2E, Version.V1_9, Version.V1_12),
                    map(0x2F, Version.V1_12_1, Version.V1_12_2),
                    map(0x32, Version.V1_13, Version.V1_13_2),
                    map(0x35, Version.V1_14, Version.V1_14_4),
                    map(0x36, Version.V1_15, Version.V1_15_2),
                    map(0x35, Version.V1_16, Version.V1_16_1),
                    map(0x34, Version.V1_16_2, Version.V1_16_4),
                    map(0x38, Version.V1_17, Version.V1_18_2),
                    map(0x36, Version.V1_19, Version.V1_19),
                    map(0x39, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketKeepAlive::new,
                    map(0x00, Version.V1_8, Version.V1_8),
                    map(0x1F, Version.V1_9, Version.V1_12_2),
                    map(0x21, Version.V1_13, Version.V1_13_2),
                    map(0x20, Version.V1_14, Version.V1_14_4),
                    map(0x21, Version.V1_15, Version.V1_15_2),
                    map(0x20, Version.V1_16, Version.V1_16_1),
                    map(0x1F, Version.V1_16_2, Version.V1_16_4),
                    map(0x21, Version.V1_17, Version.V1_18_2),
                    map(0x1E, Version.V1_19, Version.V1_19),
                    map(0x20, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketChatMessage::new,
                    map(0x02, Version.V1_8, Version.V1_8),
                    map(0x0F, Version.V1_9, Version.V1_12_2),
                    map(0x0E, Version.V1_13, Version.V1_14_4),
                    map(0x0F, Version.V1_15, Version.V1_15_2),
                    map(0x0E, Version.V1_16, Version.V1_16_4),
                    map(0x0F, Version.V1_17, Version.V1_18_2),
                    map(0x5F, Version.V1_19, Version.V1_19),
                    map(0x62, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketBossBar::new,
                    map(0x0C, Version.V1_9, Version.V1_14_4),
                    map(0x0D, Version.V1_15, Version.V1_15_2),
                    map(0x0C, Version.V1_16, Version.V1_16_4),
                    map(0x0D, Version.V1_17, Version.V1_18_2),
                    map(0x0A, Version.V1_19, Version.V1_19_1)
            );
            clientBound.register(PacketPlayerInfo::new,
                    map(0x38, Version.V1_8, Version.V1_8),
                    map(0x2D, Version.V1_9, Version.V1_12),
                    map(0x2E, Version.V1_12_1, Version.V1_12_2),
                    map(0x30, Version.V1_13, Version.V1_13_2),
                    map(0x33, Version.V1_14, Version.V1_14_4),
                    map(0x34, Version.V1_15, Version.V1_15_2),
                    map(0x33, Version.V1_16, Version.V1_16_1),
                    map(0x32, Version.V1_16_2, Version.V1_16_4),
                    map(0x36, Version.V1_17, Version.V1_18_2),
                    map(0x34, Version.V1_19, Version.V1_19),
                    map(0x37, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketTitleLegacy::new,
                    map(0x45, Version.V1_8, Version.V1_11_1),
                    map(0x47, Version.V1_12, Version.V1_12),
                    map(0x48, Version.V1_12_1, Version.V1_12_2),
                    map(0x4B, Version.V1_13, Version.V1_13_2),
                    map(0x4F, Version.V1_14, Version.V1_14_4),
                    map(0x50, Version.V1_15, Version.V1_15_2),
                    map(0x4F, Version.V1_16, Version.V1_16_4)
            );
            clientBound.register(PacketTitleSetTitle::new,
                    map(0x59, Version.V1_17, Version.V1_17_1),
                    map(0x5A, Version.V1_18, Version.V1_19),
                    map(0x5D, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketTitleSetSubTitle::new,
                    map(0x57, Version.V1_17, Version.V1_17_1),
                    map(0x58, Version.V1_18, Version.V1_19),
                    map(0x5B, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketTitleTimes::new,
                    map(0x5A, Version.V1_17, Version.V1_17_1),
                    map(0x5B, Version.V1_18, Version.V1_19),
                    map(0x5E, Version.V1_19_1, Version.V1_19_1)
            );
            clientBound.register(PacketPlayerListHeader::new,
                    map(0x47, Version.V1_8, Version.V1_8),
                    map(0x48, Version.V1_9, Version.V1_9_2),
                    map(0x47, Version.V1_9_4, Version.V1_11_1),
                    map(0x49, Version.V1_12, Version.V1_12),
                    map(0x4A, Version.V1_12_1, Version.V1_12_2),
                    map(0x4E, Version.V1_13, Version.V1_13_2),
                    map(0x53, Version.V1_14, Version.V1_14_4),
                    map(0x54, Version.V1_15, Version.V1_15_2),
                    map(0x53, Version.V1_16, Version.V1_16_4),
                    map(0x5E, Version.V1_17, Version.V1_17_1),
                    map(0x5F, Version.V1_18, Version.V1_18_2),
                    map(0x60, Version.V1_19, Version.V1_19),
                    map(0x63, Version.V1_19_1, Version.V1_19_1)
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
            return registry.getOrDefault(version, registry.get(Version.getMin()));
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
