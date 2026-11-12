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

package ua.nanit.limbo.connection;

import io.netty.buffer.ByteBufAllocator;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.Nullable;
import ua.nanit.limbo.LimboConstants;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.MetadataWriter;
import ua.nanit.limbo.protocol.PacketSnapshot;
import ua.nanit.limbo.protocol.packets.configuration.PacketFinishConfiguration;
import ua.nanit.limbo.protocol.packets.configuration.PacketKnownPacks;
import ua.nanit.limbo.protocol.packets.configuration.PacketRegistryData;
import ua.nanit.limbo.protocol.packets.configuration.PacketUpdateTags;
import ua.nanit.limbo.protocol.packets.login.PacketLoginSuccess;
import ua.nanit.limbo.protocol.packets.play.*;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.data.Title;
import ua.nanit.limbo.util.ComponentUtils;
import ua.nanit.limbo.util.UUIDUtils;
import ua.nanit.limbo.world.DimensionRegistry;
import ua.nanit.limbo.world.DimensionType;
import ua.nanit.limbo.world.VersionedDimension;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class PacketSnapshots {

    public static PacketSnapshot PACKET_LOGIN_SUCCESS;
    public static PacketSnapshot PACKET_JOIN_GAME;
    public static PacketSnapshot PACKET_SPAWN_POSITION;
    public static PacketSnapshot PACKET_PLUGIN_MESSAGE;
    public static PacketSnapshot PACKET_PLAYER_ABILITIES;
    public static PacketSnapshot PACKET_PLAYER_INFO;
    public static PacketSnapshot PACKET_DECLARE_COMMANDS;
    public static PacketSnapshot PACKET_JOIN_MESSAGE;
    public static PacketSnapshot PACKET_BOSS_BAR;
    public static PacketSnapshot PACKET_HEADER_AND_FOOTER;

    public static PacketSnapshot PACKET_PLAYER_POS_AND_LOOK_LEGACY;
    // For 1.19 we need to spawn player outside the world to avoid stuck in terrain loading
    public static PacketSnapshot PACKET_PLAYER_POS_AND_LOOK;

    public static PacketSnapshot PACKET_TITLE_TITLE;
    public static PacketSnapshot PACKET_TITLE_SUBTITLE;
    public static PacketSnapshot PACKET_TITLE_TIMES;

    public static PacketSnapshot PACKET_TITLE_LEGACY_TITLE;
    public static PacketSnapshot PACKET_TITLE_LEGACY_SUBTITLE;
    public static PacketSnapshot PACKET_TITLE_LEGACY_TIMES;

    public static PacketSnapshot PACKET_REGISTRY_DATA;
    private static Map<Version, List<PacketSnapshot>> PACKETS_REGISTRY_DATA;

    public static PacketSnapshot PACKET_KNOWN_PACKS;

    public static PacketSnapshot PACKET_UPDATE_TAGS;

    public static PacketSnapshot PACKET_FINISH_CONFIGURATION;

    public static List<PacketSnapshot> PACKETS_CHUNKS;
    public static PacketSnapshot PACKET_START_WAITING_CHUNKS;

    public static void initPackets(@NonNull LimboServer server) {
        String playerListName = server.getConfig().getPlayerListUsername();
        if (playerListName.length() > 16) {
            playerListName = playerListName.substring(0, 16);
        }

        final UUID uuid = UUIDUtils.getOfflineModeUuid(playerListName);

        PacketLoginSuccess loginSuccess = new PacketLoginSuccess();
        loginSuccess.setUsername(playerListName);
        loginSuccess.setUuid(uuid);
        loginSuccess.setSessionId(UUID.randomUUID());

        PacketLogin packetLogin = new PacketLogin();
        DimensionType dimensionType = server.getConfig().getDimensionType();
        DimensionRegistry dimensionRegistry = server.getDimensionRegistry();
        VersionedDimension versionedDimension = dimensionType.createVersionedDimension(dimensionRegistry);
        packetLogin.setEntityId(new Random().nextInt(1, 999999));
        packetLogin.setEnableRespawnScreen(true);
        packetLogin.setFlat(false);
        packetLogin.setGameMode(server.getConfig().getGameMode());
        packetLogin.setSecureProfile(server.getConfig().isSecureProfile());
        packetLogin.setHardcore(false);
        packetLogin.setMaxPlayers(server.getConfig().getMaxPlayers());
        packetLogin.setPreviousGameMode(-1);
        packetLogin.setReducedDebugInfo(true);
        packetLogin.setDebug(false);
        packetLogin.setViewDistance(0);
        packetLogin.setSeed(0);
        packetLogin.setDimension(versionedDimension);

        PacketPlayerAbilities playerAbilities = new PacketPlayerAbilities();
        playerAbilities.setFlyingSpeed(0.0F);
        playerAbilities.setFlying(true);
        playerAbilities.setFieldOfView(0.1F);

        int teleportId = ThreadLocalRandom.current().nextInt();

        PacketPlayerPositionAndLook positionAndLookLegacy
                = new PacketPlayerPositionAndLook(0, 64, 0, 0, 0, teleportId);

        PacketPlayerPositionAndLook positionAndLook
                = new PacketPlayerPositionAndLook(0, 400, 0, 0, 0, teleportId);

        PacketSpawnPosition packetSpawnPosition = new PacketSpawnPosition(
                versionedDimension.getKey(),
                0,
                400,
                0,
                0,
                0
        );

        PacketDeclareCommands declareCommands = new PacketDeclareCommands();
        declareCommands.setCommands(Collections.emptyList());

        PacketPlayerInfo info = new PacketPlayerInfo();
        info.setUsername(playerListName);
        info.setGameMode(server.getConfig().getGameMode());
        info.setUuid(uuid);

        PACKET_LOGIN_SUCCESS = PacketSnapshot.of(loginSuccess);
        PACKET_JOIN_GAME = PacketSnapshot.of(packetLogin);
        PACKET_PLAYER_POS_AND_LOOK_LEGACY = PacketSnapshot.of(positionAndLookLegacy);
        PACKET_PLAYER_POS_AND_LOOK = PacketSnapshot.of(positionAndLook);
        PACKET_SPAWN_POSITION = PacketSnapshot.of(packetSpawnPosition);
        PACKET_PLAYER_ABILITIES = PacketSnapshot.of(playerAbilities);
        PACKET_PLAYER_INFO = PacketSnapshot.of(info);

        PACKET_DECLARE_COMMANDS = PacketSnapshot.of(declareCommands);

        if (server.getConfig().isUseHeaderAndFooter()) {
            PacketPlayerListHeader header = new PacketPlayerListHeader();
            header.setHeader(server.getConfig().getPlayerListHeader());
            header.setFooter(server.getConfig().getPlayerListFooter());
            PACKET_HEADER_AND_FOOTER = PacketSnapshot.of(header);
        }

        if (server.getConfig().isUseBrandName()) {
            PacketPluginMessage pluginMessage = new PacketPluginMessage();
            pluginMessage.setChannel(LimboConstants.BRAND_CHANNEL);
            ByteMessage byteMessage = new ByteMessage(ByteBufAllocator.DEFAULT.heapBuffer());
            try {
                byteMessage.writeString(ComponentUtils.toLegacyString(server.getConfig().getBrandName()));
                pluginMessage.setData(byteMessage.toByteArray());
            } finally {
                byteMessage.release();
            }
            PACKET_PLUGIN_MESSAGE = PacketSnapshot.of(pluginMessage);
        }

        if (server.getConfig().isUseJoinMessage()) {
            PacketChatMessage joinMessage = new PacketChatMessage();
            joinMessage.setMessage(server.getConfig().getJoinMessage());
            joinMessage.setPosition(PacketChatMessage.PositionLegacy.SYSTEM_MESSAGE);
            joinMessage.setSender(UUID.randomUUID());
            PACKET_JOIN_MESSAGE = PacketSnapshot.of(joinMessage);
        }

        if (server.getConfig().isUseBossBar()) {
            PacketBossBar bossBar = new PacketBossBar();
            bossBar.setBossBar(server.getConfig().getBossBar());
            bossBar.setUuid(UUID.randomUUID());
            PACKET_BOSS_BAR = PacketSnapshot.of(bossBar);
        }

        if (server.getConfig().isUseTitle()) {
            Title title = server.getConfig().getTitle();

            PacketTitleSetTitle packetTitle = new PacketTitleSetTitle();
            PacketTitleSetSubTitle packetSubtitle = new PacketTitleSetSubTitle();
            PacketTitleTimes packetTimes = new PacketTitleTimes();

            PacketTitleLegacy legacyTitle = new PacketTitleLegacy();
            PacketTitleLegacy legacySubtitle = new PacketTitleLegacy();
            PacketTitleLegacy legacyTimes = new PacketTitleLegacy();

            packetTitle.setTitle(title.getTitle());
            packetSubtitle.setSubtitle(title.getSubtitle());
            packetTimes.setFadeIn(title.getFadeIn());
            packetTimes.setStay(title.getStay());
            packetTimes.setFadeOut(title.getFadeOut());

            legacyTitle.setTitle(title);
            legacyTitle.setAction(PacketTitleLegacy.Action.SET_TITLE);

            legacySubtitle.setTitle(title);
            legacySubtitle.setAction(PacketTitleLegacy.Action.SET_SUBTITLE);

            legacyTimes.setTitle(title);
            legacyTimes.setAction(PacketTitleLegacy.Action.SET_TIMES_AND_DISPLAY);

            PACKET_TITLE_TITLE = PacketSnapshot.of(packetTitle);
            PACKET_TITLE_SUBTITLE = PacketSnapshot.of(packetSubtitle);
            PACKET_TITLE_TIMES = PacketSnapshot.of(packetTimes);

            PACKET_TITLE_LEGACY_TITLE = PacketSnapshot.of(legacyTitle);
            PACKET_TITLE_LEGACY_SUBTITLE = PacketSnapshot.of(legacySubtitle);
            PACKET_TITLE_LEGACY_TIMES = PacketSnapshot.of(legacyTimes);
        }

        PACKET_KNOWN_PACKS = PacketSnapshot.of(PacketKnownPacks.class, (version) -> {
            PacketKnownPacks packetKnownPacks = new PacketKnownPacks();

            packetKnownPacks.setKnownPacks(List.of(
                    new PacketKnownPacks.KnownPack(
                            "minecraft",
                            "core",
                            version.getDisplayName()
                    )
            ));

            return packetKnownPacks;
        });

        PACKET_UPDATE_TAGS = PacketSnapshot.of(PacketUpdateTags.class, (version) -> {
            PacketUpdateTags packetUpdateTags = new PacketUpdateTags();
            Map<String, Map<String, List<Integer>>> tags = dimensionRegistry.createUpdateTags(version);
            packetUpdateTags.setTags(tags);
            return packetUpdateTags;
        });

        PacketRegistryData packetRegistryData = new PacketRegistryData();
        packetRegistryData.setMetadataWriter((msg, version) -> msg.writeCompoundTag(dimensionRegistry.getCodec_1_20(), version));

        PACKET_REGISTRY_DATA = PacketSnapshot.of(packetRegistryData);

        Map<Version, List<PacketSnapshot>> perVersionRegistries = new EnumMap<>(Version.class);
        for (Map.Entry<Version, List<MetadataWriter>> entry : dimensionRegistry.createPerVersionRegistries().entrySet()) {
            Version version = entry.getKey();
            List<MetadataWriter> registriesMetadata = entry.getValue();

            List<PacketSnapshot> packetSnapshots = new ArrayList<>();
            for (MetadataWriter writeableData : registriesMetadata) {
                PacketRegistryData registryData = new PacketRegistryData();
                registryData.setMetadataWriter(writeableData);

                packetSnapshots.add(PacketSnapshot.of(registryData, version));
            }

            perVersionRegistries.put(version, packetSnapshots);
        }
        PACKETS_REGISTRY_DATA = perVersionRegistries;

        PACKET_FINISH_CONFIGURATION = PacketSnapshot.of(new PacketFinishConfiguration());

        PacketGameEvent packetGameEvent = new PacketGameEvent();
        packetGameEvent.setType((byte) 13); // Waiting for chunks type
        packetGameEvent.setValue(0);
        PACKET_START_WAITING_CHUNKS = PacketSnapshot.of(packetGameEvent);

        int chunkXOffset = 0; // Default x position is 0
        int chunkZOffset = 0; // Default z position is 0
        int chunkEdgeSize = 1;

        List<PacketSnapshot> chunks = new ArrayList<>();
        // Make multiple chunks for edges
        for (int chunkX = chunkXOffset - chunkEdgeSize; chunkX <= chunkXOffset + chunkEdgeSize; ++chunkX) {
            for (int chunkZ = chunkZOffset - chunkEdgeSize; chunkZ <= chunkZOffset + chunkEdgeSize; ++chunkZ) {
                PacketChunkWithLight packetChunkWithLight = new PacketChunkWithLight();
                packetChunkWithLight.setX(chunkX);
                packetChunkWithLight.setZ(chunkZ);
                packetChunkWithLight.setDimension(versionedDimension);

                chunks.add(PacketSnapshot.of(packetChunkWithLight));
            }
        }
        PACKETS_CHUNKS = chunks;
    }

    @Nullable
    public static List<PacketSnapshot> getPacketsRegistryData(@NonNull Version version) {
        return PACKETS_REGISTRY_DATA.get(version);
    }
}
