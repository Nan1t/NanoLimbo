package ru.nanit.limbo.connection.pipeline;

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

        VarIntByteDecoder reader = new VarIntByteDecoder();
        int varIntEnd = in.forEachByte(reader);

        if (varIntEnd == -1) return;

        if (reader.getResult() == VarIntByteDecoder.DecodeResult.SUCCESS) {
            int readVarInt = reader.getReadVarInt();
            int bytesRead = reader.getBytesRead();
            if (readVarInt < 0) {
                Logger.error("[VarIntFrameDecoder] Bad data length");
            } else if (readVarInt == 0) {
                in.readerIndex(varIntEnd + 1);
            } else {
                int minimumRead = bytesRead + readVarInt;
                if (in.isReadable(minimumRead)) {
                    out.add(in.retainedSlice(varIntEnd + 1, readVarInt));
                    in.skipBytes(minimumRead);
                }
            }
        } else if (reader.getResult() == VarIntByteDecoder.DecodeResult.TOO_BIG) {
            Logger.error("[VarIntFrameDecoder] Too big data");
        }
    }
}