package ru.nanit.limbo.protocol.packets.play;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

public class PacketJoinGame implements PacketOut {

    private int entityId;
    private boolean isHardcore = false;
    private int gameMode = 2;
    private int previousGameMode = -1;
    private String[] worldNames;
    private CompoundBinaryTag dimensionCodec;
    private CompoundBinaryTag dimension;
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

    public void setDimension(CompoundBinaryTag dimension) {
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
        msg.writeBoolean(isHardcore);
        msg.writeByte(gameMode);
        msg.writeByte(previousGameMode);
        msg.writeStringsArray(worldNames);
        msg.writeCompoundTag(dimensionCodec);
        msg.writeCompoundTag(dimension);
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
