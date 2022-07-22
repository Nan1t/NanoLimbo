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
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.List;

public class MinecraftCipherCoder {
    private final SecretKey key;
    private final Decoder decoder;
    private final Encoder encoder;

    public MinecraftCipherCoder(SecretKey key) throws GeneralSecurityException {
        this.key = key;
        this.decoder = new Decoder();
        this.encoder = new Encoder();
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public Encoder getEncoder() {
        return encoder;
    }

    private ByteBuf process(ByteBuf msg, Cipher cipher, ByteBufAllocator allocator){
        ByteBuf source;
        if (msg.hasArray()) {
            source = msg.retain();
        } else {
            source = allocator
                    .heapBuffer(msg.readableBytes())
                    .writeBytes(msg).slice();
        }
        try {
            int inBytes = source.readableBytes();
            int baseOffset = source.arrayOffset() + source.readerIndex();

            try {
                cipher.update(source.array(), baseOffset, inBytes, source.array(), baseOffset);
            } catch (ShortBufferException ex) {
                throw new AssertionError("Cipher update did not operate in place and requested a larger "
                        + "buffer than the source buffer");
            }
            return source;
        } catch (Exception e) {
            source.release(); // compatible will never be used if we throw an exception
            throw e;
        }
    }

    private class Decoder extends MessageToMessageDecoder<ByteBuf> {
        private final Cipher cipher;

        public Decoder() throws GeneralSecurityException {
            this.cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            this.cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            out.add(process(msg, cipher, ctx.alloc()));
        }
    }

    private class Encoder extends MessageToMessageEncoder<ByteBuf> {
        private final Cipher cipher;

        public Encoder() throws GeneralSecurityException {
            this.cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            this.cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            out.add(process(msg, cipher, ctx.alloc()));
        }
    }
}
