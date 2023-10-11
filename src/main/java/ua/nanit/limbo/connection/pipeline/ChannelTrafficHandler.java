package ua.nanit.limbo.connection.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import ua.nanit.limbo.server.Logger;

import java.util.Arrays;

public class ChannelTrafficHandler extends ChannelInboundHandlerAdapter {

    private final int maxPacketSize;
    private final double maxPacketRate;
    private final PacketBucket packetBucket;

    public ChannelTrafficHandler(int maxPacketSize, double interval, double maxPacketRate) {
        this.maxPacketSize = maxPacketSize;
        this.maxPacketRate = maxPacketRate;
        this.packetBucket = (interval > 0.0 && maxPacketRate > 0.0) ? new PacketBucket(interval * 1000.0, 150) : null;
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf in = (ByteBuf) msg;
            int bytes = in.readableBytes();

            if (maxPacketSize > 0 && bytes > maxPacketSize) {
                closeConnection(ctx, "Closed %s due to large packet size (%d bytes)", ctx.channel().remoteAddress(), bytes);
                return;
            }

            if (packetBucket != null) {
                packetBucket.incrementPackets(1);
                if (packetBucket.getCurrentPacketRate() > maxPacketRate) {
                    closeConnection(ctx, "Closed %s due to many packets sent (%d in the last %.1f seconds)", ctx.channel().remoteAddress(), packetBucket.sum, (packetBucket.intervalTime / 1000.0));
                    return;
                }
            }
        }

        super.channelRead(ctx, msg);
    }

    private void closeConnection(ChannelHandlerContext ctx, String reason, Object... args) {
        ctx.close();
        Logger.info(reason, args);
    }

    private static class PacketBucket {
        private static final double NANOSECONDS_TO_MILLISECONDS = 1.0e-6;
        private static final int MILLISECONDS_TO_SECONDS = 1000;

        private final double intervalTime;
        private final double intervalResolution;
        private final int[] data;
        private int newestData;
        private double lastBucketTime;
        private int sum;

        public PacketBucket(final double intervalTime, final int totalBuckets) {
            this.intervalTime = intervalTime;
            this.intervalResolution = intervalTime / totalBuckets;
            this.data = new int[totalBuckets];
        }

        public void incrementPackets(final int packets) {
            double timeMs = System.nanoTime() * NANOSECONDS_TO_MILLISECONDS;
            double timeDelta = timeMs - this.lastBucketTime;

            if (timeDelta < 0.0) {
                timeDelta = 0.0;
            }

            if (timeDelta < this.intervalResolution) {
                this.data[this.newestData] += packets;
                this.sum += packets;
                return;
            }

            int bucketsToMove = (int)(timeDelta / this.intervalResolution);
            double nextBucketTime = this.lastBucketTime + bucketsToMove * this.intervalResolution;

            if (bucketsToMove >= this.data.length) {
                Arrays.fill(this.data, 0);
                this.data[0] = packets;
                this.sum = packets;
                this.newestData = 0;
                this.lastBucketTime = timeMs;
                return;
            }

            for (int i = 1; i < bucketsToMove; ++i) {
                int index = (this.newestData + i) % this.data.length;
                this.sum -= this.data[index];
                this.data[index] = 0;
            }

            int newestDataIndex = (this.newestData + bucketsToMove) % this.data.length;
            this.sum += packets - this.data[newestDataIndex];
            this.data[newestDataIndex] = packets;
            this.newestData = newestDataIndex;
            this.lastBucketTime = nextBucketTime;
        }

        public double getCurrentPacketRate() {
            return this.sum / (this.intervalTime / MILLISECONDS_TO_SECONDS);
        }
    }
}
