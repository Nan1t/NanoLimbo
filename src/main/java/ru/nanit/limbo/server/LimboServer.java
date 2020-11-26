package ru.nanit.limbo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.nanit.limbo.LimboConfig;
import ru.nanit.limbo.connection.ClientChannelInitializer;
import ru.nanit.limbo.connection.ClientConnection;
import ru.nanit.limbo.protocol.packets.play.PacketBossBar;
import ru.nanit.limbo.protocol.packets.play.PacketChatMessage;
import ru.nanit.limbo.util.Logger;
import ru.nanit.limbo.world.DimensionRegistry;

import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class LimboServer {

    private final Map<UUID, ClientConnection> connections = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private PacketChatMessage joinMessage;
    private PacketBossBar joinBossBar;

    public int getConnectionsCount(){
        return connections.size();
    }

    public void addConnection(ClientConnection connection){
        connections.put(connection.getUuid(), connection);
    }

    public void removeConnection(ClientConnection connection){
        connections.remove(connection.getUuid());
    }

    public PacketChatMessage getJoinMessage() {
        return joinMessage;
    }

    public PacketBossBar getJoinBossBar() {
        return joinBossBar;
    }

    public void start() throws Exception {
        Logger.info("Starting server...");

        LimboConfig.load(Paths.get("./settings.properties"));
        DimensionRegistry.init(LimboConfig.getDimensionType());

        initializeInGameData();

        executor.scheduleAtFixedRate(this::broadcastKeepAlive, 0L, 5L, TimeUnit.SECONDS);

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ClientChannelInitializer(this));

        if (LimboConfig.getHost().isEmpty()){
            bootstrap.bind(LimboConfig.getPort());
        } else {
            bootstrap.bind(LimboConfig.getHost(), LimboConfig.getPort());
        }

        Logger.info("Server started on %s:%d", LimboConfig.getHost(), LimboConfig.getPort());
    }

    private void initializeInGameData(){
        if (LimboConfig.getJoinMessages().getChatMessage() != null){
            joinMessage = new PacketChatMessage();
            joinMessage.setJsonData(LimboConfig.getJoinMessages().getChatMessage());
            joinMessage.setPosition(PacketChatMessage.Position.CHAT);
            joinMessage.setSender(UUID.randomUUID());
        }

        if (LimboConfig.getJoinMessages().getBossBarText() != null){
            joinBossBar = new PacketBossBar();
            joinBossBar.setTitle(LimboConfig.getJoinMessages().getBossBarText());
            joinBossBar.setHealth(LimboConfig.getJoinMessages().getBossBarHealth());
            joinBossBar.setColor(LimboConfig.getJoinMessages().getBossBarColor());
            joinBossBar.setDivision(LimboConfig.getJoinMessages().getBossBarDivision());
            joinBossBar.setUuid(UUID.randomUUID());
        }
    }

    private void broadcastKeepAlive(){
        connections.values().forEach(ClientConnection::sendKeepAlive);
    }

}
