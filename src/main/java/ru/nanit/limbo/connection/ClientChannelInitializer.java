package ru.nanit.limbo.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import ru.nanit.limbo.connection.pipeline.PacketDecoder;
import ru.nanit.limbo.connection.pipeline.PacketEncoder;
import ru.nanit.limbo.connection.pipeline.VarIntFrameDecoder;
import ru.nanit.limbo.connection.pipeline.VarIntLengthEncoder;
import ru.nanit.limbo.server.LimboServer;

import java.util.concurrent.TimeUnit;

public class ClientChannelInitializer extends ChannelInitializer<Channel> {

    private final LimboServer server;

    public ClientChannelInitializer(LimboServer server) {
        this.server = server;
    }

    @Override
    protected void initChannel(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();

        PacketDecoder decoder = new PacketDecoder();
        PacketEncoder encoder = new PacketEncoder();

        pipeline.addLast("timeout", new ReadTimeoutHandler(server.getConfig().getReadTimeout(),
                TimeUnit.MILLISECONDS));
        pipeline.addLast("frame_decoder", new VarIntFrameDecoder());
        pipeline.addLast("frame_encoder", new VarIntLengthEncoder());
        pipeline.addLast("decoder", decoder);
        pipeline.addLast("encoder", encoder);
        pipeline.addLast("handler", new ClientConnection(channel, server, decoder, encoder));
    }

}
