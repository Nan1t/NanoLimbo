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

package ru.nanit.limbo.connection.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ru.nanit.limbo.server.Logger;

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