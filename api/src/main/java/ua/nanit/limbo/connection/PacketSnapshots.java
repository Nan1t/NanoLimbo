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

import ua.nanit.limbo.LimboConstants;
import ua.nanit.limbo.protocol.PacketSnapshot;
import ua.nanit.limbo.protocol.packets.login.PacketLoginSuccess;
import ua.nanit.limbo.protocol.packets.play.*;
import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.data.Title;
import ua.nanit.limbo.util.UuidUtil;
import ua.nanit.limbo.protocol.packets.play.PacketBossBar;
import ua.nanit.limbo.protocol.packets.play.PacketChatMessage;
import ua.nanit.limbo.protocol.packets.play.PacketDeclareCommands;
import ua.nanit.limbo.protocol.packets.play.PacketJoinGame;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerAbilities;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerInfo;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerListHeader;
import ua.nanit.limbo.protocol.packets.play.PacketPlayerPositionAndLook;
import ua.nanit.limbo.protocol.packets.play.PacketPluginMessage;
import ua.nanit.limbo.protocol.packets.play.PacketTitleLegacy;
import ua.nanit.limbo.protocol.packets.play.PacketTitleSetSubTitle;
import ua.nanit.limbo.protocol.packets.play.PacketTitleSetTitle;
import ua.nanit.limbo.protocol.packets.play.PacketTitleTimes;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class PacketSnapshots {

    private PacketSnapshot packetLoginSuccess;
    private PacketSnapshot packetJoinGame;
    private PacketSnapshot packetSpawnPosition;
    private PacketSnapshot packetPluginMessage;
    private PacketSnapshot packetPlayerAbilities;
    private PacketSnapshot packetPlayerInfo;
    private PacketSnapshot packetDeclareCommands;
    private PacketSnapshot packetJoinMessage;
    private PacketSnapshot packetBossBar;
    private PacketSnapshot packetHeaderAndFooter;

    private PacketSnapshot packetPlayerPosAndLookLegacy;
    // For 1.19 we need to spawn player outside world to avoid stuck in terrain loading
    private PacketSnapshot packetPlayerPosAndLook;

    private PacketSnapshot packetTitleTitle;
    private PacketSnapshot packetTitleSubtitle;
    private PacketSnapshot packetTitleTimes;

    private PacketSnapshot packetTitleLegacyTitle;
    private PacketSnapshot packetTitleLegacySubtitle;
    private PacketSnapshot packetTitleLegacyTimes;


    public PacketSnapshots(LimboServer server) {
        final String username = server.getConfig().getPingData().getVersion();
        final UUID uuid = UuidUtil.getOfflineModeUuid(username);

        PacketLoginSuccess loginSuccess = new PacketLoginSuccess();
        loginSuccess.setUsername(username);
        loginSuccess.setUuid(uuid);

        PacketJoinGame joinGame = new PacketJoinGame();
        String worldName = "minecraft:" + server.getConfig().getDimensionType().toLowerCase();
        joinGame.setEntityId(0);
        joinGame.setEnableRespawnScreen(true);
        joinGame.setFlat(false);
        joinGame.setGameMode(server.getConfig().getGameMode());
        joinGame.setHardcore(false);
        joinGame.setMaxPlayers(server.getConfig().getMaxPlayers());
        joinGame.setPreviousGameMode(-1);
        joinGame.setReducedDebugInfo(true);
        joinGame.setDebug(false);
        joinGame.setViewDistance(0);
        joinGame.setWorldName(worldName);
        joinGame.setWorldNames(worldName);
        joinGame.setHashedSeed(0);
        joinGame.setDimensionRegistry(server.getDimensionRegistry());

        PacketPlayerAbilities playerAbilities = new PacketPlayerAbilities();
        playerAbilities.setFlyingSpeed(0.0F);
        playerAbilities.setFlags(0x02);
        playerAbilities.setFieldOfView(0.1F);

        int teleportId = ThreadLocalRandom.current().nextInt();

        PacketPlayerPositionAndLook positionAndLookLegacy
                = new PacketPlayerPositionAndLook(0, 64, 0, 0, 0, teleportId);

        PacketPlayerPositionAndLook positionAndLook
                = new PacketPlayerPositionAndLook(0, 400, 0, 0, 0, teleportId);

        PacketSpawnPosition spawnPosition = new PacketSpawnPosition(0, 400, 0);

        PacketDeclareCommands declareCommands = new PacketDeclareCommands();
        declareCommands.setCommands(Collections.emptyList());

        PacketPlayerInfo info = new PacketPlayerInfo();
        info.setUsername(server.getConfig().getPlayerListUsername());
        info.setGameMode(server.getConfig().getGameMode());
        info.setUuid(uuid);

        packetLoginSuccess = PacketSnapshot.of(loginSuccess);
        packetJoinGame = PacketSnapshot.of(joinGame);
        packetPlayerPosAndLookLegacy = PacketSnapshot.of(positionAndLookLegacy);
        packetPlayerPosAndLook = PacketSnapshot.of(positionAndLook);
        packetSpawnPosition = PacketSnapshot.of(spawnPosition);
        packetPlayerAbilities = PacketSnapshot.of(playerAbilities);
        packetPlayerInfo = PacketSnapshot.of(info);

        packetDeclareCommands = PacketSnapshot.of(declareCommands);

        if (server.getConfig().isUseHeaderAndFooter()) {
            PacketPlayerListHeader header = new PacketPlayerListHeader();
            header.setHeader(server.getConfig().getPlayerListHeader());
            header.setFooter(server.getConfig().getPlayerListFooter());
            packetHeaderAndFooter = PacketSnapshot.of(header);
        }

        if (server.getConfig().isUseBrandName()) {
            PacketPluginMessage pluginMessage = new PacketPluginMessage();
            pluginMessage.setChannel(LimboConstants.BRAND_CHANNEL);
            pluginMessage.setMessage(server.getConfig().getBrandName());
            packetPluginMessage = PacketSnapshot.of(pluginMessage);
        }

        if (server.getConfig().isUseJoinMessage()) {
            PacketChatMessage joinMessage = new PacketChatMessage();
            joinMessage.setJsonData(server.getConfig().getJoinMessage());
            joinMessage.setPosition(PacketChatMessage.PositionLegacy.SYSTEM_MESSAGE);
            joinMessage.setSender(UUID.randomUUID());
            packetJoinMessage = PacketSnapshot.of(joinMessage);
        }

        if (server.getConfig().isUseBossBar()) {
            PacketBossBar bossBar = new PacketBossBar();
            bossBar.setBossBar(server.getConfig().getBossBar());
            bossBar.setUuid(UUID.randomUUID());
            packetBossBar = PacketSnapshot.of(bossBar);
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

            packetTitleTitle = PacketSnapshot.of(packetTitle);
            packetTitleSubtitle = PacketSnapshot.of(packetSubtitle);
            packetTitleTimes = PacketSnapshot.of(packetTimes);

            packetTitleLegacyTitle = PacketSnapshot.of(legacyTitle);
            packetTitleLegacySubtitle = PacketSnapshot.of(legacySubtitle);
            packetTitleLegacyTimes = PacketSnapshot.of(legacyTimes);
        }
    }

    public PacketSnapshot getPacketLoginSuccess() {
        return packetLoginSuccess;
    }

    public PacketSnapshot getPacketJoinGame() {
        return packetJoinGame;
    }

    public PacketSnapshot getPacketSpawnPosition() {
        return packetSpawnPosition;
    }

    public PacketSnapshot getPacketPluginMessage() {
        return packetPluginMessage;
    }

    public PacketSnapshot getPacketPlayerAbilities() {
        return packetPlayerAbilities;
    }

    public PacketSnapshot getPacketPlayerInfo() {
        return packetPlayerInfo;
    }

    public PacketSnapshot getPacketDeclareCommands() {
        return packetDeclareCommands;
    }

    public PacketSnapshot getPacketJoinMessage() {
        return packetJoinMessage;
    }

    public PacketSnapshot getPacketBossBar() {
        return packetBossBar;
    }

    public PacketSnapshot getPacketHeaderAndFooter() {
        return packetHeaderAndFooter;
    }

    public PacketSnapshot getPacketPlayerPosAndLookLegacy() {
        return packetPlayerPosAndLookLegacy;
    }

    public PacketSnapshot getPacketPlayerPosAndLook() {
        return packetPlayerPosAndLook;
    }

    public PacketSnapshot getPacketTitleTitle() {
        return packetTitleTitle;
    }

    public PacketSnapshot getPacketTitleSubtitle() {
        return packetTitleSubtitle;
    }

    public PacketSnapshot getPacketTitleTimes() {
        return packetTitleTimes;
    }

    public PacketSnapshot getPacketTitleLegacyTitle() {
        return packetTitleLegacyTitle;
    }

    public PacketSnapshot getPacketTitleLegacySubtitle() {
        return packetTitleLegacySubtitle;
    }

    public PacketSnapshot getPacketTitleLegacyTimes() {
        return packetTitleLegacyTimes;
    }
}
