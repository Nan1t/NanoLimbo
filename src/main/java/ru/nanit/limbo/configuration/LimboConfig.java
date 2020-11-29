package ru.nanit.limbo.configuration;

import napi.configurate.Configuration;
import napi.configurate.source.ConfigSources;
import napi.configurate.yaml.YamlConfiguration;
import ru.nanit.limbo.server.data.*;
import ru.nanit.limbo.util.Colors;

import java.net.SocketAddress;
import java.nio.file.Path;

public final class LimboConfig {

    private final Path root;

    private SocketAddress address;
    private int maxPlayers;
    private PingData pingData;

    private String dimensionType;
    private Position spawnPosition;
    private int gameMode;

    private boolean useJoinMessage;
    private boolean useBossBar;
    private String joinMessage;
    private BossBar bossBar;

    private InfoForwarding infoForwarding;
    private long readTimeout;
    private int debugLevel = 3;

    private boolean useEpoll;
    private int bossGroupSize;
    private int workerGroupSize;

    public LimboConfig(Path root){
        this.root = root;
    }

    public void load() throws Exception {
        Configuration conf = YamlConfiguration.builder()
                .source(ConfigSources.resource("/settings.yml", this).copyTo(root))
                .build();

        conf.reload();

        address = conf.getNode("bind").getValue(SocketAddress.class);
        maxPlayers = conf.getNode("maxPlayers").getInt();
        pingData = conf.getNode("ping").getValue(PingData.class);
        dimensionType = conf.getNode("dimension").getString();
        spawnPosition = conf.getNode("spawnPosition").getValue(Position.class);
        gameMode = conf.getNode("gameMode").getInt();
        useJoinMessage = conf.getNode("joinMessage", "enable").getBoolean();
        useBossBar = conf.getNode("bossBar", "enable").getBoolean();

        if (useJoinMessage)
            joinMessage = Colors.of(conf.getNode("joinMessage", "text").getString());

        if (useBossBar)
            bossBar = conf.getNode("bossBar").getValue(BossBar.class);

        infoForwarding = conf.getNode("infoForwarding").getValue(InfoForwarding.class);
        readTimeout = conf.getNode("readTimeout").getLong();
        debugLevel = conf.getNode("debugLevel").getInt();

        useEpoll = conf.getNode("netty", "useEpoll").getBoolean(true);
        bossGroupSize = conf.getNode("netty", "threads", "bossGroup").getInt(1);
        workerGroupSize = conf.getNode("netty", "threads", "workerGroup").getInt(4);
    }

    public SocketAddress getAddress() {
        return address;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public PingData getPingData() {
        return pingData;
    }

    public String getDimensionType() {
        return dimensionType;
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public int getGameMode() {
        return gameMode;
    }

    public InfoForwarding getInfoForwarding() {
        return infoForwarding;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public boolean isUseJoinMessage() {
        return useJoinMessage;
    }

    public boolean isUseBossBar() {
        return useBossBar;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public int getBossGroupSize() {
        return bossGroupSize;
    }

    public int getWorkerGroupSize() {
        return workerGroupSize;
    }
}
