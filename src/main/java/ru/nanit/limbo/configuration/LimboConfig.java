package ru.nanit.limbo.configuration;

import java.net.SocketAddress;

import ru.nanit.limbo.server.data.BossBar;
import ru.nanit.limbo.server.data.InfoForwarding;
import ru.nanit.limbo.server.data.PingData;
import ru.nanit.limbo.server.data.Title;
import ru.nanit.limbo.world.Location;

public interface LimboConfig {
    SocketAddress getAddress();

    int getMaxPlayers();

    PingData getPingData();

    String getDimensionType();

    Location getSpawnPosition();

    int getGameMode();

    InfoForwarding getInfoForwarding();

    long getReadTimeout();

    int getDebugLevel();

    boolean isUseBrandName();

    boolean isUseJoinMessage();

    boolean isUseBossBar();

    boolean isUseTitle();

    boolean isUsePlayerList();

    boolean isUseHeaderAndFooter();

    String getBrandName();

    String getJoinMessage();

    BossBar getBossBar();

    Title getTitle();

    String getPlayerListUsername();

    String getPlayerListHeader();

    String getPlayerListFooter();

    boolean isUseEpoll();

    int getBossGroupSize();

    int getWorkerGroupSize();
}
