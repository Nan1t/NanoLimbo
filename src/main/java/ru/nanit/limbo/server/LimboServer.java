package ru.nanit.limbo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import napi.configurate.serializing.NodeSerializers;
import ru.nanit.limbo.configuration.LimboConfig;
import ru.nanit.limbo.configuration.SocketAddressSerializer;
import ru.nanit.limbo.connection.ClientChannelInitializer;
import ru.nanit.limbo.connection.ClientConnection;
import ru.nanit.limbo.server.data.*;
import ru.nanit.limbo.util.Logger;
import ru.nanit.limbo.world.DimensionRegistry;

import java.net.SocketAddress;
import java.nio.file.Paths;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class LimboServer {

    private LimboConfig config;
    private Connections connections;
    private DimensionRegistry dimensionRegistry;
    private ScheduledFuture<?> keepAliveTask;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public LimboConfig getConfig(){
        return config;
    }

    public Connections getConnections(){
        return connections;
    }

    public DimensionRegistry getDimensionRegistry() {
        return dimensionRegistry;
    }

    public void start() throws Exception {
        Logger.info("Starting server...");

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        NodeSerializers.register(SocketAddress.class, new SocketAddressSerializer());
        NodeSerializers.register(InfoForwarding.class, new InfoForwarding.Serializer());
        NodeSerializers.register(PingData.class, new PingData.Serializer());
        NodeSerializers.register(BossBar.class, new BossBar.Serializer());
        NodeSerializers.register(Position.class, new Position.Serializer());

        config = new LimboConfig(Paths.get("./"));
        config.load();

        Logger.setLevel(config.getDebugLevel());

        dimensionRegistry = new DimensionRegistry();
        dimensionRegistry.load(config.getDimensionType());
        connections = new Connections();

        ClientConnection.preInitPackets(this);

        startBootstrap();

        keepAliveTask = workerGroup.scheduleAtFixedRate(this::broadcastKeepAlive, 0L, 5L, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "NanoLimbo shutdown thread"));

        Logger.info("Server started on %s", config.getAddress());
    }

    private void startBootstrap(){
        Class<? extends ServerChannel> channelClass;

        if (Epoll.isAvailable()){
            bossGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(4);
            channelClass = EpollServerSocketChannel.class;
            Logger.debug("Using Epoll transport type");
        } else {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(4);
            channelClass = NioServerSocketChannel.class;
            Logger.debug("Using Java NIO transport type");
        }

        new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(channelClass)
                .childHandler(new ClientChannelInitializer(this))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .localAddress(config.getAddress())
                .bind();
    }

    private void broadcastKeepAlive(){
        connections.getAllConnections().forEach(ClientConnection::sendKeepAlive);
    }

    private void stop(){
        if (keepAliveTask != null){
            keepAliveTask.cancel(true);
        }

        if (bossGroup != null){
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null){
            workerGroup.shutdownGracefully();
        }
    }

}
