package ua.nanit.limbo.connection.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import ua.nanit.limbo.server.Logger;

public class ChannelTrafficHandler extends ChannelInboundHandlerAdapter {

    private final int packetSize;
    private final int packetsPerSec;
    private final int bytesPerSec;

    private int packetsCounter;
    private int bytesCounter;

    private long lastPacket;

    public ChannelTrafficHandler(int packetSize, int packetsPerSec, int bytesPerSec) {
        this.packetSize = packetSize;
        this.packetsPerSec = packetsPerSec;
        this.bytesPerSec = bytesPerSec;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf in = (ByteBuf) msg;
            int bytes = in.readableBytes();

            System.out.println(bytes + " bytes");

            if (packetSize > 0 && bytes > packetSize) {
                closeConnection(ctx, "Closed %s due too large packet size (%d bytes)", ctx.channel().remoteAddress(), bytes);
                return;
            }

            if (!measureTraffic(ctx, bytes)) return;
        }

        super.channelRead(ctx, msg);
    }

    private boolean measureTraffic(ChannelHandlerContext ctx, int bytes) {
        if (packetsPerSec < 0 && bytesPerSec < 0) return true;

        long time = System.currentTimeMillis();

        if (time - lastPacket >= 1000) {
            bytesCounter = 0;
            packetsCounter = 0;
        }

        packetsCounter++;
        bytesCounter += bytes;

        if (packetsPerSec > 0 && packetsCounter > packetsPerSec) {
            closeConnection(ctx, "Closed %s due too frequent packet sending (%d per sec)", ctx.channel().remoteAddress(), packetsCounter);
            return false;
        }

        if (bytesPerSec > 0 && bytesCounter > bytesPerSec) {
            closeConnection(ctx, "Closed %s due too many bytes sent per second (%d per sec)", ctx.channel().remoteAddress(), bytesCounter);
            return false;
        }

        lastPacket = time;

        return true;
    }

    private void closeConnection(ChannelHandlerContext ctx, String reason, Object... args) {
        ctx.close();
        Logger.info(reason, args);
    }
}
