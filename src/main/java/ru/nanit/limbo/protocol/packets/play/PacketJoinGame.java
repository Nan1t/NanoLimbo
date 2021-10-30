package ru.nanit.limbo.protocol.packets.play;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;
import ru.nanit.limbo.world.Dimension;

public class PacketJoinGame implements PacketOut {

    private int entityId;
    private boolean isHardcore = false;
    private int gameMode = 2;
    private int previousGameMode = -1;
    private String[] worldNames;
    private CompoundBinaryTag dimensionCodec;
    private Dimension dimension;
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

    public void setDimensionCodec(CompoundBinaryTag dimensionCodec) {
        this.dimensionCodec = dimensionCodec;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
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

        if (version.fromTo(Version.V1_8, Version.V1_13_2)) {
            msg.writeByte(gameMode);
            msg.writeInt(dimension.getId());
            msg.writeByte(0); // Difficulty
            msg.writeByte(maxPlayers);
            msg.writeString("flat"); // Level type
            msg.writeBoolean(reducedDebugInfo);
        }

        if (version.fromTo(Version.V1_14, Version.V1_14_4)) {
            msg.writeByte(gameMode);
            msg.writeInt(dimension.getId());
            msg.writeByte(maxPlayers);
            msg.writeString("flat"); // Level type
            msg.writeVarInt(viewDistance);
            msg.writeBoolean(reducedDebugInfo);
        }

        if (version.fromTo(Version.V1_15, Version.V1_15_2)) {
            msg.writeByte(gameMode);
            msg.writeInt(dimension.getId());
            msg.writeLong(hashedSeed);
            msg.writeByte(maxPlayers);
            msg.writeString("flat"); // Level type
            msg.writeVarInt(viewDistance);
            msg.writeBoolean(reducedDebugInfo);
            msg.writeBoolean(enableRespawnScreen);
        }

        if (version.fromTo(Version.V1_16, Version.V1_16_1)) {
            msg.writeBoolean(isHardcore);
            msg.writeByte(gameMode);
            msg.writeByte(previousGameMode);
            msg.writeStringsArray(worldNames);
            msg.writeCompoundTag(dimensionCodec);
            msg.writeInt(dimension.getId());
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
            msg.writeCompoundTag(dimensionCodec);
            msg.writeCompoundTag(dimension.getData());
            msg.writeString(worldName);
            msg.writeLong(hashedSeed);
            msg.writeVarInt(maxPlayers);
            msg.writeVarInt(viewDistance);
            msg.writeBoolean(reducedDebugInfo);
            msg.writeBoolean(enableRespawnScreen);
            msg.writeBoolean(isDebug);
            msg.writeBoolean(isFlat);
        }
    }

}
