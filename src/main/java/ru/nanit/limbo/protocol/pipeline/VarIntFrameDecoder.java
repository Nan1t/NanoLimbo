package ru.nanit.limbo.protocol.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ru.nanit.limbo.util.Logger;

import java.util.List;

public class VarIntFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (!ctx.channel().isActive()) {
            in.clear();
            return;
        }

        final VarIntByteDecoder reader = new VarIntByteDecoder();

        int varintEnd = in.forEachByte(reader);

        if (varintEnd == -1) {
            return;
        }

        if (reader.getResult() == VarIntByteDecoder.DecodeResult.SUCCESS) {
            int readVarint = reader.getReadVarint();
            int bytesRead = reader.getBytesRead();
            if (readVarint < 0) {
                Logger.error("BAD_LENGTH_CACHED");
            } else if (readVarint == 0) {
                // skip over the empty packet and ignore it
                in.readerIndex(varintEnd + 1);
            } else {
                int minimumRead = bytesRead + readVarint;
                if (in.isReadable(minimumRead)) {
                    out.add(in.retainedSlice(varintEnd + 1, readVarint));
                    in.skipBytes(minimumRead);
                }
            }
        } else if (reader.getResult() == VarIntByteDecoder.DecodeResult.TOO_BIG) {
            Logger.error("Too big");
        }
    }
}