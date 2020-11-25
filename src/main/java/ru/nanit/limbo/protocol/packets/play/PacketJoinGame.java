package ru.nanit.limbo.protocol.packets.play;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import ru.nanit.limbo.protocol.ByteMessage;
import ru.nanit.limbo.protocol.Direction;
import ru.nanit.limbo.protocol.PacketOut;
import ru.nanit.limbo.protocol.registry.Version;

import java.util.List;

public class PacketJoinGame implements PacketOut {

    private int entityId;
    private boolean isHardcore = false;
    private int gameMode = 2;
    private int previousGameMode = -1;
    private int worldCount = 1;
    private List<String> worldNames;
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

    public void setWorldCount(int worldCount) {
        this.worldCount = worldCount;
    }

    public void setWorldNames(List<String> worldNames) {
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
    public void encode(ByteMessage msg, Direction direction, Version version) {

    }

}
