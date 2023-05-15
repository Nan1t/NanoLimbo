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
import ua.nanit.limbo.world.DimensionRegistry;

public class PacketJoinGame implements PacketOut {

    private int entityId;
    private boolean isHardcore = false;
    private int gameMode = 2;
    private int previousGameMode = -1;
    private String[] worldNames;
    private DimensionRegistry dimensionRegistry;
    private String worldName;
    private long hashedSeed;
    private int maxPlayers;
    private int viewDistance = 2;
    private boolean reducedDebugInfo;
    private boolean enableRespawnScreen;
    private boolean isDebug;
    private boolean isFlat;

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setHardcore(boolean hardcore) {
        isHardcore = hardcore;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public void setPreviousGameMode(int previousGameMode) {
        this.previousGameMode = previousGameMode;
    }

    public void setWorldNames(String... worldNames) {
        this.worldNames = worldNames;
    }

    public void setDimensionRegistry(DimensionRegistry dimensionRegistry) {
        this.dimensionRegistry = dimensionRegistry;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public void setHashedSeed(long hashedSeed) {
        this.hashedSeed = hashedSeed;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }

    public void setReducedDebugInfo(boolean reducedDebugInfo) {
        this.reducedDebugInfo = reducedDebugInfo;
    }

    public void setEnableRespawnScreen(boolean enableRespawnScreen) {
        this.enableRespawnScreen = enableRespawnScreen;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    public void setFlat(boolean flat) {
        isFlat = flat;
    }

    @Override
    public void encode(ByteMessage msg, Version version) {
        msg.writeInt(entityId);

        if (version.fromTo(Version.V1_7_2, Version.V1_7_6)) {
            msg.writeByte(gameMode == 3 ? 1 : gameMode);
            msg.writeByte(dimensionRegistry.getDefaultDimension_1_16().getId());
            msg.writeByte(0); // Difficulty
            msg.writeByte(maxPlayers);
            msg.writeString("flat"); // Level type
        }

        if (version.fromTo(Version.V1_8, Version.V1_9)) {
            msg.writeByte(gameMode);
            msg.writeByte(dimensionRegistry.getDefaultDimension_1_16().getId());
            msg.writeByte(0); // Difficulty
            msg.writeByte(maxPlayers);
            msg.writeString("flat"); // Level type
            msg.writeBoolean(reducedDebugInfo);
        }

        if (version.fromTo(Version.V1_9_1, Version.V1_13_2)) {
            msg.writeByte(gameMode);
            msg.writeInt(dimensionRegistry.getDefaultDimension_1_16().getId());
            msg.writeByte(0); // Difficulty
            msg.writeByte(maxPlayers);
            msg.writeString("flat"); // Level type
            msg.writeBoolean(reducedDebugInfo);
        }

        if (version.fromTo(Version.V1_14, Version.V1_14_4)) {
            msg.writeByte(gameMode);
            msg.writeInt(dimensionRegistry.getDefaultDimension_1_16().getId());
            msg.writeByte(maxPlayers);
            msg.writeString("flat"); // Level type
            msg.writeVarInt(viewDistance);
            msg.writeBoolean(reducedDebugInfo);
        }

        if (version.fromTo(Version.V1_15, Version.V1_15_2)) {
            msg.writeByte(gameMode);
            msg.writeInt(dimensionRegistry.getDefaultDimension_1_16().getId());
            msg.writeLong(hashedSeed);
            msg.writeByte(maxPlayers);
            msg.writeString("flat"); // Level type
            msg.writeVarInt(viewDistance);
            msg.writeBoolean(reducedDebugInfo);
            msg.writeBoolean(enableRespawnScreen);
        }

        if (version.fromTo(Version.V1_16, Version.V1_16_1)) {
            msg.writeByte(gameMode);
            msg.writeByte(previousGameMode);
            msg.writeStringsArray(worldNames);
            msg.writeCompoundTag(dimensionRegistry.getOldCodec());
            msg.writeString(dimensionRegistry.getDefaultDimension_1_16().getName());
            msg.writeString(worldName);
            msg.writeLong(hashedSeed);
            msg.writeByte(maxPlayers);
            msg.writeVarInt(viewDistance);
            msg.writeBoolean(reducedDebugInfo);
            msg.writeBoolean(enableRespawnScreen);
            msg.writeBoolean(isDebug);
            msg.writeBoolean(isFlat);
        }

        if (version.fromTo(Version.V1_16_2, Version.V1_17_1)) {
            msg.writeBoolean(isHardcore);
            msg.writeByte(gameMode);
            msg.writeByte(previousGameMode);
            msg.writeStringsArray(worldNames);
            msg.writeCompoundTag(dimensionRegistry.getCodec_1_16());
            msg.writeCompoundTag(dimensionRegistry.getDefaultDimension_1_16().getData());
            msg.writeString(worldName);
            msg.writeLong(hashedSeed);
            msg.writeVarInt(maxPlayers);
            msg.writeVarInt(viewDistance);
            msg.writeBoolean(reducedDebugInfo);
            msg.writeBoolean(enableRespawnScreen);
            msg.writeBoolean(isDebug);
            msg.writeBoolean(isFlat);
        }

        if (version.fromTo(Version.V1_18, Version.V1_18_2)) {
            msg.writeBoolean(isHardcore);
            msg.writeByte(gameMode);
            msg.writeByte(previousGameMode);
            msg.writeStringsArray(worldNames);
            if (version.moreOrEqual(Version.V1_18_2)) {
                msg.writeCompoundTag(dimensionRegistry.getCodec_1_18_2());
                msg.writeCompoundTag(dimensionRegistry.getDefaultDimension_1_18_2().getData());
            } else {
                msg.writeCompoundTag(dimensionRegistry.getCodec_1_16());
                msg.writeCompoundTag(dimensionRegistry.getDefaultDimension_1_16().getData());
            }
            msg.writeString(worldName);
            msg.writeLong(hashedSeed);
            msg.writeVarInt(maxPlayers);
            msg.writeVarInt(viewDistance);
            msg.writeVarInt(viewDistance); // Simulation Distance
            msg.writeBoolean(reducedDebugInfo);
            msg.writeBoolean(enableRespawnScreen);
            msg.writeBoolean(isDebug);
            msg.writeBoolean(isFlat);
        }

        if (version.fromTo(Version.V1_19, Version.V1_19_4)) {
            msg.writeBoolean(isHardcore);
            msg.writeByte(gameMode);
            msg.writeByte(previousGameMode);
            msg.writeStringsArray(worldNames);
            if (version.moreOrEqual(Version.V1_19_1)) {
                if (version.moreOrEqual(Version.V1_19_4)) {
                    msg.writeCompoundTag(dimensionRegistry.getCodec_1_19_4());
                }
                else {
                    msg.writeCompoundTag(dimensionRegistry.getCodec_1_19_1());
                }
            }
            else {
                msg.writeCompoundTag(dimensionRegistry.getCodec_1_19());
            }
            msg.writeString(worldName); // World type
            msg.writeString(worldName);
            msg.writeLong(hashedSeed);
            msg.writeVarInt(maxPlayers);
            msg.writeVarInt(viewDistance);
            msg.writeVarInt(viewDistance); // Simulation Distance
            msg.writeBoolean(reducedDebugInfo);
            msg.writeBoolean(enableRespawnScreen);
            msg.writeBoolean(isDebug);
            msg.writeBoolean(isFlat);
            msg.writeBoolean(false);
        }

        if (version.moreOrEqual(Version.V1_20)) {
            msg.writeBoolean(isHardcore);
            msg.writeByte(gameMode);
            msg.writeByte(previousGameMode);
            msg.writeStringsArray(worldNames);
            msg.writeCompoundTag(dimensionRegistry.getCodec_1_20());
            msg.writeString(worldName); // World type
            msg.writeString(worldName);
            msg.writeLong(hashedSeed);
            msg.writeVarInt(maxPlayers);
            msg.writeVarInt(viewDistance);
            msg.writeVarInt(viewDistance); // Simulation Distance
            msg.writeBoolean(reducedDebugInfo);
            msg.writeBoolean(enableRespawnScreen);
            msg.writeBoolean(isDebug);
            msg.writeBoolean(isFlat);
            msg.writeBoolean(false);
            msg.writeVarInt(0);
        }
    }

}
