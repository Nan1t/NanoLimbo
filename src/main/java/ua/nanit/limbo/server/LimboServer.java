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

package ua.nanit.limbo.server;

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
import ua.nanit.limbo.configuration.LimboConfig;
import ua.nanit.limbo.connection.ClientChannelInitializer;
import ua.nanit.limbo.connection.ClientConnection;
import ua.nanit.limbo.connection.PacketHandler;
import ua.nanit.limbo.connection.PacketSnapshots;
import ua.nanit.limbo.world.DimensionRegistry;

import java.nio.file.Paths;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class LimboServer {

    private LimboConfig config;
    private PacketHandler packetHandler;
    private Connections connections;
    private DimensionRegistry dimensionRegistry;
    private ScheduledFuture<?> keepAliveTask;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private CommandManager commandManager;

    public LimboConfig getConfig() {
        return config;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public Connections getConnections() {
        return connections;
    }

    public DimensionRegistry getDimensionRegistry() {
        return dimensionRegistry;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void start() throws Exception {

        long start = System.currentTimeMillis();

        Logger.info("Starting server...");

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);

        config = new LimboConfig(Paths.get("./"));
        config.load();

        packetHandler = new PacketHandler(this);
        dimensionRegistry = new DimensionRegistry(this);
        dimensionRegistry.load(config.getDimensionType());
        connections = new Connections();

        PacketSnapshots.initPackets(this);

        startBootstrap();

        keepAliveTask = workerGroup.scheduleAtFixedRate(this::broadcastKeepAlive, 0L, 5L, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "NanoLimbo shutdown thread"));

        Logger.info("Server started on %s", config.getAddress());

        Logger.setLevel(config.getDebugLevel());

        commandManager = new CommandManager();
        commandManager.registerAll(this);
        commandManager.start();

        Logger.info("Done ("+ (System.currentTimeMillis() - start) / 1000 + "s)! For help, type \"help\"");

        System.gc();
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

    private void stop() {
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

        Logger.info("Server stopped, Goodbye!");
    }

}
