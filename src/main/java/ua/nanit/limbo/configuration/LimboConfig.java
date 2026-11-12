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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import ua.nanit.limbo.configuration.serializers.*;
import ua.nanit.limbo.server.TransportType;
import ua.nanit.limbo.server.data.*;
import ua.nanit.limbo.world.DimensionType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Getter
public final class LimboConfig {

    private final Path root;

    private SocketAddress address;
    private int maxPlayers;
    private PingData pingData;

    private DimensionType dimensionType;
    private int gameMode;
    private boolean secureProfile;

    private boolean useBrandName;
    private boolean useJoinMessage;
    private boolean useBossBar;
    private boolean useTitle;
    private boolean usePlayerList;
    private boolean useHeaderAndFooter;

    private Component brandName;
    private Component joinMessage;
    private BossBar bossBar;
    private Title title;

    private boolean useActionBar;
    private Component actionBar;

    private String playerListUsername;
    private Component playerListHeader;
    private Component playerListFooter;

    private InfoForwarding infoForwarding;
    private long readTimeout;
    private int debugLevel;
    private boolean logPlayersIp;

    private TransportType transportType;
    private int bossGroupSize;
    private int workerGroupSize;

    private boolean useTrafficLimits;
    private int maxPacketSize;
    private double interval;
    private double maxPacketRate;
    private double maxPacketBytesRate;

    public void load() throws Exception {
        ConfigurationOptions options = ConfigurationOptions.defaults().serializers(getSerializers());
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .source(this::getReader)
                .defaultOptions(options)
                .build();

        ConfigurationNode conf = loader.load();

        address = conf.node("bind").get(SocketAddress.class);
        maxPlayers = conf.node("maxPlayers").getInt(100);
        pingData = conf.node("ping").get(PingData.class);
        dimensionType = conf.node("dimension").get(DimensionType.class, DimensionType.THE_END);
        gameMode = conf.node("gameMode").getInt(3);
        secureProfile = conf.node("secureProfile").getBoolean(false);
        useBrandName = conf.node("brandName", "enable").getBoolean();
        useJoinMessage = conf.node("joinMessage", "enable").getBoolean();
        useBossBar = conf.node("bossBar", "enable").getBoolean();
        useTitle = conf.node("title", "enable").getBoolean();
        usePlayerList = conf.node("playerList", "enable").getBoolean();
        playerListUsername = conf.node("playerList", "username").getString("NanoLimbo");
        useHeaderAndFooter = conf.node("headerAndFooter", "enable").getBoolean();

        if (useBrandName) {
            brandName = conf.node("brandName", "content").get(Component.class, Component.empty());
        }

        if (useJoinMessage) {
            joinMessage = conf.node("joinMessage", "text").get(Component.class, Component.empty());
        }

        if (useBossBar) {
            bossBar = conf.node("bossBar").get(BossBar.class);
        }

        if (useTitle) {
            title = conf.node("title").get(Title.class);
        }

        useActionBar = conf.node("actionBar", "enable").getBoolean(false);
        if (useActionBar) {
            actionBar = conf.node("actionBar", "text").get(Component.class, Component.empty());
        }

        if (useHeaderAndFooter) {
            playerListHeader = conf.node("headerAndFooter", "header").get(Component.class, Component.empty());
            playerListFooter = conf.node("headerAndFooter", "footer").get(Component.class, Component.empty());
        }

        infoForwarding = conf.node("infoForwarding").get(InfoForwarding.class);
        readTimeout = conf.node("readTimeout").getLong(30000);
        debugLevel = conf.node("debugLevel").getInt(2);
        logPlayersIp = conf.node("logPlayersIp").getBoolean(true);

        transportType = conf.node("netty", "transportType").get(TransportType.class, TransportType.EPOLL);
        bossGroupSize = conf.node("netty", "threads", "bossGroup").getInt(1);
        workerGroupSize = conf.node("netty", "threads", "workerGroup").getInt(4);

        useTrafficLimits = conf.node("traffic", "enable").getBoolean(false);
        maxPacketSize = conf.node("traffic", "maxPacketSize").getInt(-1);
        interval = conf.node("traffic", "interval").getDouble(-1.0);
        maxPacketRate = conf.node("traffic", "maxPacketRate").getDouble(-1.0);
        maxPacketBytesRate = conf.node("traffic", "maxPacketBytesRate").getDouble(-1.0);
    }

    @NonNull
    private BufferedReader getReader() throws IOException {
        String name = "settings.yml";
        Path filePath = Paths.get(root.toString(), name);

        if (!Files.exists(filePath)) {
            InputStream stream = getClass().getResourceAsStream("/" + name);

            if (stream == null) {
                throw new FileNotFoundException("Cannot find settings resource file");
            }

            Files.copy(stream, filePath);
        }

        return Files.newBufferedReader(filePath);
    }

    @NonNull
    private TypeSerializerCollection getSerializers() {
        return TypeSerializerCollection.builder()
                .register(SocketAddress.class, new SocketAddressSerializer())
                .register(Component.class, new ComponentSerializer())
                .register(TransportType.class, new TransportTypeSerializer())
                .register(DimensionType.class, new DimensionTypeSerializer())
                .register(NamespacedKey.class, new NamespacedKeySerializer())
                .register(InfoForwarding.class, new InfoForwardingSerializer(root))
                .register(PingData.class, new PingDataSerializer())
                .register(BossBar.class, new BossBarSerializer())
                .register(Title.class, new TitleSerializer())
                .build();
    }
}
