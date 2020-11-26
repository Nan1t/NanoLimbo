package ru.nanit.limbo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.nanit.limbo.LimboConfig;
import ru.nanit.limbo.connection.ClientChannelInitializer;
import ru.nanit.limbo.connection.ClientConnection;
import ru.nanit.limbo.util.Logger;
import ru.nanit.limbo.world.DefaultDimension;

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

    public int getConnectionsCount(){
        return connections.size();
    }

    public void addConnection(ClientConnection connection){
        connections.put(connection.getUuid(), connection);
    }

    public void removeConnection(ClientConnection connection){
        connections.remove(connection.getUuid());
    }

    public void start() throws Exception {
        Logger.info("Starting server...");

        LimboConfig.load(Paths.get("./settings.properties"));
        DefaultDimension.init();

        executor.scheduleAtFixedRate(this::broadcastKeepAlive, 0L, 5L, TimeUnit.SECONDS);

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ClientChannelInitializer(this));

        bootstrap.bind(LimboConfig.getHost(), LimboConfig.getPort());

        Logger.info("Server started on %s:%d", LimboConfig.getHost(), LimboConfig.getPort());
    }

    private void broadcastKeepAlive(){
        connections.values().forEach(ClientConnection::sendKeepAlive);
    }

}
