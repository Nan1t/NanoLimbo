package ru.nanit.limbo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.nanit.limbo.LimboConfig;
import ru.nanit.limbo.connection.ClientChannelInitializer;
import ru.nanit.limbo.util.Logger;

public final class LimboServer {

    public void start() throws Exception {
        Logger.info("Starting server...");

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ClientChannelInitializer(this));

        bootstrap.bind(LimboConfig.getHost(), LimboConfig.getPort());
        Logger.info("Server started on %s:%d", LimboConfig.getHost(), LimboConfig.getPort());
    }

}
