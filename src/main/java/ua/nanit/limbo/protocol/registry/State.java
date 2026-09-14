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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import ua.nanit.limbo.protocol.Packet;
import ua.nanit.limbo.protocol.packets.PacketHandshake;
import ua.nanit.limbo.protocol.packets.configuration.PacketFinishConfiguration;
import ua.nanit.limbo.protocol.packets.configuration.PacketKnownPacks;
import ua.nanit.limbo.protocol.packets.configuration.PacketRegistryData;
import ua.nanit.limbo.protocol.packets.configuration.PacketUpdateTags;
import ua.nanit.limbo.protocol.packets.login.*;
import ua.nanit.limbo.protocol.packets.play.*;
import ua.nanit.limbo.protocol.packets.status.PacketStatusPing;
import ua.nanit.limbo.protocol.packets.status.PacketStatusRequest;
import ua.nanit.limbo.protocol.packets.status.PacketStatusResponse;

import java.util.*;
import java.util.function.Supplier;

import static ua.nanit.limbo.protocol.registry.Version.*;

@AllArgsConstructor
public enum State {

    HANDSHAKING {
        {
            serverBound.register(
                    PacketHandshake::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
        }
    },
    STATUS {
        {
            serverBound.register(
                    PacketStatusRequest::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
            serverBound.register(
                    PacketStatusPing::new,
                    map(0x01, Version.getMin(), Version.getMax())
            );
            clientBound.register(
                    PacketStatusResponse::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
            clientBound.register(
                    PacketStatusPing::new,
                    map(0x01, Version.getMin(), Version.getMax())
            );
        }
    },
    LOGIN {
        {
            serverBound.register(
                    PacketLoginStart::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
            serverBound.register(
                    PacketLoginPluginResponse::new,
                    map(0x02, Version.getMin(), Version.getMax())
            );
            serverBound.register(
                    PacketLoginAcknowledged::new,
                    map(0x03, V1_20_2, Version.getMax())
            );
            clientBound.register(
                    PacketLoginDisconnect::new,
                    map(0x00, Version.getMin(), Version.getMax())
            );
            clientBound.register(
                    PacketLoginSuccess::new,
                    map(0x02, Version.getMin(), Version.getMax())
            );
            clientBound.register(
                    PacketLoginPluginRequest::new,
                    map(0x04, Version.getMin(), Version.getMax())
            );
        }
    },
    CONFIGURATION {
        {
            clientBound.register(
                    PacketPluginMessage::new,
                    map(0x00, V1_20_2, V1_20_3),
                    map(0x01, V1_20_5, Version.getMax())
            );
            clientBound.register(
                    PacketDisconnect::new,
                    map(0x01, V1_20_2, V1_20_3),
                    map(0x02, V1_20_5, Version.getMax())
            );
            clientBound.register(
                    PacketFinishConfiguration::new,
                    map(0x02, V1_20_2, V1_20_3),
                    map(0x03, V1_20_5, Version.getMax())
            );
            clientBound.register(
                    PacketKeepAlive::new,
                    map(0x03, V1_20_2, V1_20_3),
                    map(0x04, V1_20_5, Version.getMax())
            );
            clientBound.register(
                    PacketKnownPacks::new,
                    map(0x0E, V1_20_5, V26_2),
                    map(0x0F, V26_3, Version.getMax())
            );
            clientBound.register(
                    PacketUpdateTags::new,
                    map(0x0D, V1_20_5, V26_2),
                    map(0x0E, V26_3, Version.getMax())
            );
            clientBound.register(
                    PacketRegistryData::new,
                    map(0x05, V1_20_2, V1_20_3),
                    map(0x07, V1_20_5, Version.getMax())
            );

            serverBound.register(
                    PacketPluginMessage::new,
                    map(0x01, V1_20_2, V1_20_3),
                    map(0x02, V1_20_2, Version.getMax())
            );
            serverBound.register(
                    PacketFinishConfiguration::new,
                    map(0x02, V1_20_2, V1_20_3),
                    map(0x03, V1_20_5, Version.getMax())
            );
            serverBound.register(
                    PacketKeepAlive::new,
                    map(0x03, V1_20_2, V1_20_3),
                    map(0x04, V1_20_5, Version.getMax())
            );
            serverBound.register(
                    PacketKnownPacks::new,
                    map(0x07, V1_20_5, Version.getMax())
            );
        }
    },
    PLAY {
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
                    map(0x12, V1_19_4, V1_20),
                    map(0x14, V1_20_2, V1_20_2),
                    map(0x15, V1_20_3, V1_20_3),
                    map(0x18, V1_20_5, V1_21),
                    map(0x1A, V1_21_2, V1_21_5),
                    map(0x1B, V1_21_6, V1_21_11),
                    map(0x1C, V26_1, Version.getMax())
            );
            clientBound.register(PacketDisconnect::new,
                    map(0x40, V1_7_2, V1_8),
                    map(0x1A, V1_9, V1_12_2),
                    map(0x1B, V1_13, V1_13_2),
                    map(0x1A, V1_14, V1_14_4),
                    map(0x1B, V1_15, V1_15_2),
                    map(0x1A, V1_16, V1_16_1),
                    map(0x19, V1_16_2, V1_16_4),
                    map(0x1A, V1_17, V1_18_2),
                    map(0x17, V1_19, V1_19),
                    map(0x19, V1_19_1, V1_19_1),
                    map(0x17, V1_19_3, V1_19_3),
                    map(0x1A, V1_19_4, V1_20),
                    map(0x1B, V1_20_2, V1_20_2),
                    map(0x15, V1_20_3, V1_20_3),
                    map(0x1D, V1_20_5, V1_21_4),
                    map(0x1C, V1_21_5, V1_21_7),
                    map(0x20, V1_21_9, Version.getMax())
            );
            clientBound.register(PacketDeclareCommands::new,
                    map(0x11, V1_13, V1_14_4),
                    map(0x12, V1_15, V1_15_2),
                    map(0x11, V1_16, V1_16_1),
                    map(0x10, V1_16_2, V1_16_4),
                    map(0x12, V1_17, V1_18_2),
                    map(0x0F, V1_19, V1_19_1),
                    map(0x0E, V1_19_3, V1_19_3),
                    map(0x10, V1_19_4, V1_20),
                    map(0x11, V1_20_2, V1_21_4),
                    map(0x10, V1_21_5, Version.getMax())
            );
            clientBound.register(PacketLogin::new,
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
                    map(0x28, V1_19_4, V1_20),
                    map(0x29, V1_20_2, V1_20_3),
                    map(0x2B, V1_20_5, V1_21),
                    map(0x2C, V1_21_2, V1_21_4),
                    map(0x2B, V1_21_5, V1_21_7),
                    map(0x30, V1_21_9, V1_21_11),
                    map(0x31, V26_1, V26_2),
                    map(0x32, V26_3, Version.getMax())
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
                    map(0x17, V1_19_4, V1_20),
                    map(0x18, V1_20_2, V1_20_3),
                    map(0x19, V1_20_5, V1_21_4),
                    map(0x18, V1_21_5, Version.getMax())
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
                    map(0x34, V1_19_4, V1_20),
                    map(0x36, V1_20_2, V1_20_3),
                    map(0x38, V1_20_5, V1_21),
                    map(0x3A, V1_21_2, V1_21_4),
                    map(0x39, V1_21_5, V1_21_7),
                    map(0x3E, V1_21_9, V1_21_11),
                    map(0x40, V26_1, V26_2),
                    map(0x41, V26_3, Version.getMax())
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
                    map(0x3C, V1_19_4, V1_20),
                    map(0x3E, V1_20_2, V1_20_3),
                    map(0x40, V1_20_5, V1_21),
                    map(0x42, V1_21_2, V1_21_4),
                    map(0x41, V1_21_5, V1_21_7),
                    map(0x46, V1_21_9, V1_21_11),
                    map(0x48, V26_1, V26_2),
                    map(0x49, V26_3, Version.getMax())
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
                    map(0x23, V1_19_4, V1_20),
                    map(0x24, V1_20_2, V1_20_3),
                    map(0x26, V1_20_5, V1_21),
                    map(0x27, V1_21_2, V1_21_4),
                    map(0x26, V1_21_5, V1_21_7),
                    map(0x2B, V1_21_9, V1_21_11),
                    map(0x2C, V26_1, V26_2),
                    map(0x2D, V26_3, Version.getMax())
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
                    map(0x64, V1_19_4, V1_20),
                    map(0x67, V1_20_2, V1_20_2),
                    map(0x69, V1_20_3, V1_20_3),
                    map(0x6C, V1_20_5, V1_21),
                    map(0x73, V1_21_2, V1_21_4),
                    map(0x72, V1_21_5, V1_21_7),
                    map(0x77, V1_21_9, V1_21_11),
                    map(0x79, V26_1, V26_2),
                    map(0x7C, V26_3, Version.getMax())
            );
            clientBound.register(PacketBossBar::new,
                    map(0x0C, V1_9, V1_14_4),
                    map(0x0D, V1_15, V1_15_2),
                    map(0x0C, V1_16, V1_16_4),
                    map(0x0D, V1_17, V1_18_2),
                    map(0x0A, V1_19, V1_19_3),
                    map(0x0B, V1_19_4, V1_20),
                    map(0x0A, V1_20_2, V1_21_4),
                    map(0x09, V1_21_5, Version.getMax())
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
                    map(0x3A, V1_19_4, V1_20),
                    map(0x3C, V1_20_2, V1_20_3),
                    map(0x3E, V1_20_5, V1_21),
                    map(0x40, V1_21_2, V1_21_4),
                    map(0x3F, V1_21_5, V1_21_7),
                    map(0x44, V1_21_9, V1_21_11),
                    map(0x46, V26_1, V26_2),
                    map(0x47, V26_3, Version.getMax())
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
                    map(0x5F, V1_19_4, V1_20),
                    map(0x61, V1_20_2, V1_20_2),
                    map(0x63, V1_20_3, V1_20_3),
                    map(0x65, V1_20_5, V1_21),
                    map(0x6C, V1_21_2, V1_21_4),
                    map(0x6B, V1_21_5, V1_21_7),
                    map(0x70, V1_21_9, V1_21_11),
                    map(0x72, V26_1, V26_2),
                    map(0x74, V26_3, Version.getMax())
            );
            clientBound.register(PacketTitleSetSubTitle::new,
                    map(0x57, V1_17, V1_17_1),
                    map(0x58, V1_18, V1_19),
                    map(0x5B, V1_19_1, V1_19_1),
                    map(0x59, V1_19_3, V1_19_3),
                    map(0x5D, V1_19_4, V1_20),
                    map(0x5F, V1_20_2, V1_20_2),
                    map(0x61, V1_20_3, V1_20_3),
                    map(0x63, V1_20_5, V1_21),
                    map(0x6A, V1_21_2, V1_21_4),
                    map(0x69, V1_21_5, V1_21_7),
                    map(0x6E, V1_21_9, V1_21_11),
                    map(0x70, V26_1, V26_2),
                    map(0x72, V26_3, Version.getMax())
            );
            clientBound.register(PacketTitleTimes::new,
                    map(0x5A, V1_17, V1_17_1),
                    map(0x5B, V1_18, V1_19),
                    map(0x5E, V1_19_1, V1_19_1),
                    map(0x5C, V1_19_3, V1_19_3),
                    map(0x60, V1_19_4, V1_20),
                    map(0x62, V1_20_2, V1_20_2),
                    map(0x64, V1_20_3, V1_20_3),
                    map(0x66, V1_20_5, V1_21),
                    map(0x6D, V1_21_2, V1_21_4),
                    map(0x6C, V1_21_5, V1_21_7),
                    map(0x71, V1_21_9, V1_21_11),
                    map(0x73, V26_1, V26_2),
                    map(0x75, V26_3, Version.getMax())
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
                    map(0x65, V1_19_4, V1_20),
                    map(0x68, V1_20_2, V1_20_2),
                    map(0x6A, V1_20_3, V1_20_3),
                    map(0x6D, V1_20_5, V1_21),
                    map(0x74, V1_21_2, V1_21_4),
                    map(0x73, V1_21_5, V1_21_7),
                    map(0x78, V1_21_9, V1_21_11),
                    map(0x7A, V26_1, V26_2),
                    map(0x7D, V26_3, Version.getMax())
            );
            clientBound.register(PacketSpawnPosition::new,
                    map(0x4C, V1_19_3, V1_19_3),
                    map(0x50, V1_19_4, V1_20),
                    map(0x52, V1_20_2, V1_20_2),
                    map(0x54, V1_20_3, V1_20_3),
                    map(0x56, V1_20_5, V1_21),
                    map(0x5B, V1_21_2, V1_21_4),
                    map(0x5A, V1_21_5, V1_21_7),
                    map(0x5F, V1_21_9, V1_21_11),
                    map(0x61, V26_1, V26_2),
                    map(0x63, V26_3, Version.getMax())
            );
            clientBound.register(PacketGameEvent::new,
                    map(0x20, V1_20_3, V1_20_3),
                    map(0x22, V1_20_5, V1_21),
                    map(0x23, V1_21_2, V1_21_4),
                    map(0x22, V1_21_5, V1_21_7),
                    map(0x26, V1_21_9, V26_2),
                    map(0x27, V26_3, Version.getMax())
            );
            clientBound.register(PacketChunkWithLight::new,
                    map(0x25, V1_20_3, V1_20_3),
                    map(0x27, V1_20_5, V1_21),
                    map(0x28, V1_21_2, V1_21_4),
                    map(0x27, V1_21_5, V1_21_7),
                    map(0x2C, V1_21_9, V1_21_11),
                    map(0x2D, V26_1, V26_2),
                    map(0x2E, V26_3, Version.getMax())
            );
        }
    };

    public final ProtocolMappings serverBound = new ProtocolMappings();
    public final ProtocolMappings clientBound = new ProtocolMappings();

    public static class ProtocolMappings {
        private final Map<Version, PacketRegistry> registry = new EnumMap<>(Version.class);

        @NonNull
        public PacketRegistry getRegistry(@NonNull Version version) {
            return this.registry.getOrDefault(version, this.registry.get(getMin()));
        }

        public void register(@NonNull Supplier<?> packet, @NonNull Mapping... mappings) {
            for (Mapping mapping : mappings) {
                for (Version ver : getRange(mapping)) {
                    PacketRegistry reg = this.registry.computeIfAbsent(ver, PacketRegistry::new);
                    reg.register(mapping.packetId, packet);
                }
            }
        }

        @NonNull
        private Collection<Version> getRange(@NonNull Mapping mapping) {
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

    @AllArgsConstructor
    @Getter
    public static class PacketRegistry {
        private final Version version;
        private final Map<Integer, Supplier<?>> packetsById = new HashMap<>();
        private final Map<Class<?>, Integer> packetIdByClass = new HashMap<>();

        public Packet getPacket(int packetId) {
            Supplier<?> supplier = packetsById.get(packetId);
            return supplier == null ? null : (Packet) supplier.get();
        }

        public int getPacketId(@NonNull Class<?> packetClass) {
            return packetIdByClass.getOrDefault(packetClass, -1);
        }

        public void register(int packetId, @NonNull Supplier<?> supplier) {
            this.packetsById.put(packetId, supplier);
            this.packetIdByClass.put(supplier.get().getClass(), packetId);
        }
    }

    public record Mapping(int packetId, @NonNull Version from, @NonNull Version to) {
    }

    /**
     * Map packet id to version range
     *
     * @param packetId Packet id
     * @param from     Minimal version (include)
     * @param to       Last version (include)
     * @return Created mapping
     */
    private static Mapping map(int packetId, @NonNull Version from, @NonNull Version to) {
        return new Mapping(packetId, from, to);
    }
}
