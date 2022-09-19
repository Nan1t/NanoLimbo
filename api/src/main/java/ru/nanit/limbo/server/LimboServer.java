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

package ru.nanit.limbo.server;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import ru.nanit.limbo.configuration.LimboConfig;
import ru.nanit.limbo.connection.ClientChannelInitializer;
import ru.nanit.limbo.connection.ClientConnection;
import ru.nanit.limbo.connection.PacketHandler;
import ru.nanit.limbo.connection.PacketSnapshots;
import ru.nanit.limbo.world.dimension.DimensionRegistry;

public final class LimboServer {
    private boolean running = false;

    private PacketHandler packetHandler;
    private PacketSnapshots packetSnapshots;
    private Connections connections;
    private DimensionRegistry dimensionRegistry;
    private ScheduledFuture<?> keepAliveTask;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private final LimboConfig config;
    private final CommandHandler<Command> commandHandler;
    private final ClassLoader classLoader;

    public LimboServer(LimboConfig config, CommandHandler<Command> commandHandler, ClassLoader classLoader) {
        this.config = config;
        this.commandHandler = commandHandler;
        this.classLoader = classLoader;
    }

    public LimboConfig getConfig() {
        return config;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public PacketSnapshots getPacketSnapshots() {
        return packetSnapshots;
    }

    public Connections getConnections() {
        return connections;
    }

    public DimensionRegistry getDimensionRegistry() {
        return dimensionRegistry;
    }

    public CommandHandler<Command> getCommandManager() {
        return commandHandler;
    }

    public void start() throws Exception {
        Logger.info("Starting server...");

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        packetHandler = new PacketHandler(this);
        dimensionRegistry = new DimensionRegistry(classLoader);
        dimensionRegistry.load(config.getDimensionType());
        connections = new Connections();

        packetSnapshots = new PacketSnapshots(this);

        startBootstrap();

        keepAliveTask = workerGroup.scheduleAtFixedRate(this::broadcastKeepAlive, 0L, 5L, TimeUnit.SECONDS);

        Logger.info("Server started on %s", config.getAddress());

        Logger.setLevel(config.getDebugLevel());

        System.gc();
        running = true;
    }

    private void startBootstrap() {
        Class<? extends ServerChannel> channelClass;

        if (config.isUseEpoll() && Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(config.getBossGroupSize());
            workerGroup = new EpollEventLoopGroup(config.getWorkerGroupSize());
            channelClass = EpollServerSocketChannel.class;
            Logger.debug("Using Epoll transport type");
        } else {
            bossGroup = new NioEventLoopGroup(config.getBossGroupSize());
            workerGroup = new NioEventLoopGroup(config.getWorkerGroupSize());
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

    private void broadcastKeepAlive() {
        connections.getAllConnections().forEach(ClientConnection::sendKeepAlive);
    }

    public void stop() {
        Logger.info("Stopping server...");

        if (keepAliveTask != null) {
            keepAliveTask.cancel(true);
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }

        running = false;
        Logger.info("Server stopped, Goodbye!");
    }

    public boolean isRunning() {
        return running;
    }
}
