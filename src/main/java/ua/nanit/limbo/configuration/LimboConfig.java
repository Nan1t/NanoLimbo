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

package ua.nanit.limbo.configuration;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import ua.nanit.limbo.util.Colors;
import ua.nanit.limbo.server.data.BossBar;
import ua.nanit.limbo.server.data.InfoForwarding;
import ua.nanit.limbo.server.data.PingData;
import ua.nanit.limbo.server.data.Title;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class LimboConfig {

    private final Path root;

    private SocketAddress address;
    private int maxPlayers;
    private PingData pingData;

    private String dimensionType;
    private int gameMode;

    private boolean useBrandName;
    private boolean useJoinMessage;
    private boolean useBossBar;
    private boolean useTitle;
    private boolean usePlayerList;
    private boolean useHeaderAndFooter;

    private String brandName;
    private String joinMessage;
    private BossBar bossBar;
    private Title title;

    private String playerListUsername;
    private String playerListHeader;
    private String playerListFooter;

    private InfoForwarding infoForwarding;
    private long readTimeout;
    private int debugLevel;

    private boolean useEpoll;
    private int bossGroupSize;
    private int workerGroupSize;

    private boolean useTrafficLimits;
    private int maxPacketSize;
    private double interval;
    private double maxPacketRate;

    public LimboConfig(Path root) {
        this.root = root;
    }

    public void load() throws Exception {
        ConfigurationOptions options = ConfigurationOptions.defaults().serializers(getSerializers());
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .source(this::getReader)
                .defaultOptions(options)
                .build();

        ConfigurationNode conf = loader.load();

        address = conf.node("bind").get(SocketAddress.class);
        maxPlayers = conf.node("maxPlayers").getInt();
        pingData = conf.node("ping").get(PingData.class);
        dimensionType = conf.node("dimension").getString("the_end");
        if (dimensionType.equalsIgnoreCase("nether")) {
            dimensionType = "the_nether";
        }
        if (dimensionType.equalsIgnoreCase("end")) {
            dimensionType = "the_end";
        }
        gameMode = conf.node("gameMode").getInt();
        useBrandName = conf.node("brandName", "enable").getBoolean();
        useJoinMessage = conf.node("joinMessage", "enable").getBoolean();
        useBossBar = conf.node("bossBar", "enable").getBoolean();
        useTitle = conf.node("title", "enable").getBoolean();
        usePlayerList = conf.node("playerList", "enable").getBoolean();
        playerListUsername = conf.node("playerList", "username").getString();
        useHeaderAndFooter = conf.node("headerAndFooter", "enable").getBoolean();

        if (useBrandName)
            brandName = conf.node("brandName", "content").getString();

        if (useJoinMessage)
            joinMessage = Colors.of(conf.node("joinMessage", "text").getString(""));

        if (useBossBar)
            bossBar = conf.node("bossBar").get(BossBar.class);

        if (useTitle)
            title = conf.node("title").get(Title.class);

        if (useHeaderAndFooter) {
            playerListHeader = Colors.of(conf.node("headerAndFooter", "header").getString());
            playerListFooter = Colors.of(conf.node("headerAndFooter", "footer").getString());
        }

        infoForwarding = conf.node("infoForwarding").get(InfoForwarding.class);
        readTimeout = conf.node("readTimeout").getLong();
        debugLevel = conf.node("debugLevel").getInt();

        useEpoll = conf.node("netty", "useEpoll").getBoolean(true);
        bossGroupSize = conf.node("netty", "threads", "bossGroup").getInt(1);
        workerGroupSize = conf.node("netty", "threads", "workerGroup").getInt(4);

        useTrafficLimits = conf.node("traffic", "enable").getBoolean(false);
        maxPacketSize = conf.node("traffic", "maxPacketSize").getInt(-1);
        interval = conf.node("traffic", "interval").getDouble(-1.0);
        maxPacketRate = conf.node("traffic", "maxPacketRate").getDouble(-1.0);
    }

    private BufferedReader getReader() throws IOException {
        String name = "settings.yml";
        Path filePath = Paths.get(root.toString(), name);

        if (!Files.exists(filePath)) {
            InputStream stream = getClass().getResourceAsStream( "/" + name);

            if (stream == null)
                throw new FileNotFoundException("Cannot find settings resource file");

            Files.copy(stream, filePath);
        }

        return Files.newBufferedReader(filePath);
    }

    private TypeSerializerCollection getSerializers() {
        return TypeSerializerCollection.builder()
                .register(SocketAddress.class, new SocketAddressSerializer())
                .register(InfoForwarding.class, new InfoForwarding.Serializer())
                .register(PingData.class, new PingData.Serializer())
                .register(BossBar.class, new BossBar.Serializer())
                .register(Title.class, new Title.Serializer())
                .build();
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

    public boolean isUseBrandName() {
        return useBrandName;
    }

    public boolean isUseJoinMessage() {
        return useJoinMessage;
    }

    public boolean isUseBossBar() {
        return useBossBar;
    }

    public boolean isUseTitle() {
        return useTitle;
    }

    public boolean isUsePlayerList() {
        return usePlayerList;
    }

    public boolean isUseHeaderAndFooter() {
        return useHeaderAndFooter;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public Title getTitle() {
        return title;
    }

    public String getPlayerListUsername() {
        return playerListUsername;
    }

    public String getPlayerListHeader() {
        return playerListHeader;
    }

    public String getPlayerListFooter() {
        return playerListFooter;
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

    public boolean isUseTrafficLimits() {
        return useTrafficLimits;
    }

    public int getMaxPacketSize() {
        return maxPacketSize;
    }

    public double getInterval() {
        return interval;
    }

    public double getMaxPacketRate() {
        return maxPacketRate;
    }
}
