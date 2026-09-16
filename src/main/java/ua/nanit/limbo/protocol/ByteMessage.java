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

package ua.nanit.limbo.protocol;

import com.google.gson.JsonElement;
import io.netty.buffer.*;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import ua.nanit.limbo.connection.PlayerPublicKey;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.server.data.NamespacedKey;
import ua.nanit.limbo.util.ComponentUtils;
import ua.nanit.limbo.util.NbtUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@AllArgsConstructor
public class ByteMessage extends ByteBuf {

    private final ByteBuf buf;

    public byte[] toByteArray() {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

    /* Minecraft's protocol methods */

    public int readVarInt() {
        final int readable = buf.readableBytes();
        if (readable == 0) {
            throw new DecoderException("Empty buffer");
        }

        // We can read at least one byte, and this should be a common case
        int k = buf.readByte();
        if ((k & 0x80) != 128) {
            return k;
        }

        // In case decoding one byte was not enough, use a loop to decode up to the next 4 bytes
        final int maxRead = Math.min(5, readable);
        int i = k & 0x7F;
        for (int j = 1; j < maxRead; j++) {
            k = buf.readByte();
            i |= (k & 0x7F) << j * 7;
            if ((k & 0x80) != 128) {
                return i;
            }
        }
        throw new DecoderException("Bad VarInt");
    }

    public void writeVarInt(int value) {
        // Peel the one and two byte count cases explicitly as they are the most common VarInt sizes
        // that the proxy will write, to improve inlining.
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else {
            writeVarIntFull(value);
        }
    }

    private void writeVarIntFull(final int value) {
        // See https://steinborn.me/posts/performance/how-fast-can-you-write-a-varint/
        if ((value & (0xFFFFFFFF << 7)) == 0) {
            buf.writeByte(value);
        } else if ((value & (0xFFFFFFFF << 14)) == 0) {
            int w = (value & 0x7F | 0x80) << 8 | (value >>> 7);
            buf.writeShort(w);
        } else if ((value & (0xFFFFFFFF << 21)) == 0) {
            int w = (value & 0x7F | 0x80) << 16 | ((value >>> 7) & 0x7F | 0x80) << 8 | (value >>> 14);
            buf.writeMedium(w);
        } else if ((value & (0xFFFFFFFF << 28)) == 0) {
            int w = (value & 0x7F | 0x80) << 24 | (((value >>> 7) & 0x7F | 0x80) << 16)
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | (value >>> 21);
            buf.writeInt(w);
        } else {
            int w = (value & 0x7F | 0x80) << 24 | ((value >>> 7) & 0x7F | 0x80) << 16
                    | ((value >>> 14) & 0x7F | 0x80) << 8 | ((value >>> 21) & 0x7F | 0x80);
            buf.writeInt(w);
            buf.writeByte(value >>> 28);
        }
    }

    public String readString() {
        return readString(Short.MAX_VALUE);
    }

    public String readString(final int maxLen) {
        final int len = readVarInt();
        if (len > maxLen * 3) {
            throw new DecoderException("Cannot receive string longer than " + maxLen * 3 + " (got " + len + " bytes)");
        }

        final String s = buf.readString(len, StandardCharsets.UTF_8);

        if (s.length() > maxLen) {
            throw new DecoderException("Cannot receive string longer than " + maxLen + " (got " + s.length() + " characters)");
        }

        return s;
    }

    public void writeString(CharSequence str) {
        int size = ByteBufUtil.utf8Bytes(str);
        writeVarInt(size);
        buf.writeCharSequence(str, StandardCharsets.UTF_8);
    }

    public byte[] readBytesArray() {
        int length = readVarInt();
        byte[] array = new byte[length];
        buf.readBytes(array);
        return array;
    }

    public void writeBytesArray(byte[] array) {
        writeVarInt(array.length);
        buf.writeBytes(array);
    }

    public int[] readIntArray() {
        int len = readVarInt();
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            array[i] = readVarInt();
        }
        return array;
    }

    public UUID readUuid() {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        return new UUID(msb, lsb);
    }

    public void writeUuid(UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public String[] readStringsArray() {
        int length = readVarInt();
        String[] ret = new String[length];
        for (int i = 0; i < length; i++) {
            ret[i] = readString();
        }
        return ret;
    }

    public void writeStringsArray(String[] stringArray) {
        writeVarInt(stringArray.length);
        for (String str : stringArray) {
            writeString(str);
        }
    }

    public void writeVarIntArray(int[] array) {
        writeVarInt(array.length);
        for (int i : array) {
            writeVarInt(i);
        }
    }

    public void writeLongArray(long[] array) {
        if (array == null) {
            writeVarInt(0);
            return;
        }

        writeVarInt(array.length);
        for (long i : array) {
            writeLong(i);
        }
    }

    public void writeBitSet(BitSet bitSet) {
        writeLongArray((bitSet != null ? bitSet.toLongArray() : null));
    }

    public void writeBitSet(BitSet bitSet, @NonNull Version version) {
        if (version.moreOrEqual(Version.V26_3)) {
            if (bitSet == null || bitSet.isEmpty()) {
                writeVarInt(0);
                return;
            }
            writeBytesArray(bitSet.toByteArray());
        } else {
            writeBitSet(bitSet);
        }
    }

    public void writeCompoundTagArray(CompoundBinaryTag[] compoundTags) {
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buf)) {
            writeVarInt(compoundTags.length);

            for (CompoundBinaryTag tag : compoundTags) {
                BinaryTagIO.writer().write(tag, (OutputStream) stream);
            }
        } catch (IOException e) {
            throw new EncoderException("Cannot write NBT CompoundTag");
        }
    }

    public CompoundBinaryTag readCompoundTag() {
        try (ByteBufInputStream stream = new ByteBufInputStream(buf)) {
            return BinaryTagIO.reader().read((InputStream) stream);
        } catch (IOException thrown) {
            throw new DecoderException("Cannot read NBT CompoundTag");
        }
    }

    public void writeNamespacedKey(@NonNull NamespacedKey namespacedKey) {
        writeString(namespacedKey.toString());
    }

    public void writeNamespacedKeyArray(@NonNull NamespacedKey[] keys) {
        writeVarInt(keys.length);
        for (NamespacedKey namespacedKey : keys) {
            writeNamespacedKey(namespacedKey);
        }
    }

    public void writeCompoundTag(@NonNull CompoundBinaryTag compoundTag, @NonNull Version version) {
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buf)) {
            if (version.moreOrEqual(Version.V1_20_2)) {
                BinaryTagIO.writer().writeNameless(compoundTag, stream, BinaryTagIO.Compression.NONE);
            } else {
                BinaryTagIO.writer().writeNamed(Map.entry("", compoundTag), stream, BinaryTagIO.Compression.NONE);
            }
        } catch (IOException e) {
            throw new EncoderException("Cannot write NBT CompoundTag");
        }
    }

    public void writeTag(@NonNull BinaryTag tag, @NonNull Version version) {
        try (ByteBufOutputStream stream = new ByteBufOutputStream(buf);
             DataOutputStream dos = new DataOutputStream(stream)) {
            BinaryTagType type = tag.type();
            if (version.moreOrEqual(Version.V1_20_2)) {
                dos.writeByte(type.id());
                type.write(tag, dos);
            } else {
                dos.writeByte(type.id());
                dos.writeUTF("");
                type.write(tag, dos);
            }
        } catch (IOException e) {
            throw new EncoderException("Cannot write NBT BinaryTag: " + tag.type(), e);
        }
    }

    public void writeComponent(@NonNull Component component, @NonNull Version version) {
        GsonComponentSerializer gsonComponentSerializer = ComponentUtils.getJsonChatSerializer(version);

        if (version.moreOrEqual(Version.V1_20_3)) {
            JsonElement jsonElement = gsonComponentSerializer.serializeToTree(component);
            CompoundBinaryTag binaryTag = (CompoundBinaryTag) NbtUtils.fromJson(jsonElement);
            writeCompoundTag(binaryTag, version);
        } else {
            writeString(gsonComponentSerializer.serialize(component));
        }
    }

    public <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumset, Class<E> oclass) {
        E[] enums = oclass.getEnumConstants();
        BitSet bits = new BitSet(enums.length);

        for (int i = 0; i < enums.length; ++i) {
            bits.set(i, enumset.contains(enums[i]));
        }

        writeFixedBitSet(bits, enums.length, buf);
    }

    private static void writeFixedBitSet(BitSet bits, int size, ByteBuf buf) {
        if (bits.length() > size) {
            throw new StackOverflowError("BitSet too large (expected " + size + " got " + bits.size() + ")");
        }
        buf.writeBytes(Arrays.copyOf(bits.toByteArray(), (size + 8) >> 3));
    }

    public PlayerPublicKey readPublicKey() {
        if (buf.readBoolean()) {
            return new PlayerPublicKey(readLong(), readArray(512), readArray(4096));
        }

        return null;
    }

    public byte[] readArray() {
        return readArray(this.buf.readableBytes());
    }

    public byte[] readArray(int limit) {
        int len = readVarInt();
        if (len > limit) {
            throw new DecoderException("Cannot receive byte array longer than " + limit + " (got " + len + " bytes)");
        }
        byte[] ret = new byte[len];
        this.buf.readBytes(ret);
        return ret;
    }

    /* Delegated methods */

    @Override
    public int capacity() {
        return buf.capacity();
    }

    @Override
    public ByteBuf capacity(int newCapacity) {
        return buf.capacity(newCapacity);
    }

    @Override
    public int maxCapacity() {
        return buf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return buf.alloc();
    }

    @Override
    @Deprecated
    public ByteOrder order() {
        return buf.order();
    }

    @Override
    @Deprecated
    public ByteBuf order(ByteOrder endianness) {
        return buf.order(endianness);
    }

    @Override
    public ByteBuf unwrap() {
        return buf.unwrap();
    }

    @Override
    public boolean isDirect() {
        return buf.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return buf.isReadOnly();
    }

    @Override
    public ByteBuf asReadOnly() {
        return buf.asReadOnly();
    }

    @Override
    public int readerIndex() {
        return buf.readerIndex();
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        return buf.readerIndex(readerIndex);
    }

    @Override
    public int writerIndex() {
        return buf.writerIndex();
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        return buf.writerIndex(writerIndex);
    }

    @Override
    public ByteBuf setIndex(int readerIndex, int writerIndex) {
        return buf.setIndex(readerIndex, writerIndex);
    }

    @Override
    public int readableBytes() {
        return buf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return buf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return buf.maxWritableBytes();
    }

    @Override
    public int maxFastWritableBytes() {
        return buf.maxFastWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return buf.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return buf.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return buf.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return buf.isWritable(size);
    }

    @Override
    public ByteBuf clear() {
        return buf.clear();
    }

    @Override
    public ByteBuf markReaderIndex() {
        return buf.markReaderIndex();
    }

    @Override
    public ByteBuf resetReaderIndex() {
        return buf.resetReaderIndex();
    }

    @Override
    public ByteBuf markWriterIndex() {
        return buf.markWriterIndex();
    }

    @Override
    public ByteBuf resetWriterIndex() {
        return buf.resetWriterIndex();
    }

    @Override
    public ByteBuf discardReadBytes() {
        return buf.discardReadBytes();
    }

    @Override
    public ByteBuf discardSomeReadBytes() {
        return buf.discardSomeReadBytes();
    }

    @Override
    public ByteBuf ensureWritable(int minWritableBytes) {
        return buf.ensureWritable(minWritableBytes);
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return buf.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return buf.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return buf.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return buf.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return buf.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return buf.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return buf.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return buf.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return buf.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return buf.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return buf.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return buf.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return buf.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return buf.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return buf.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return buf.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return buf.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return buf.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return buf.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return buf.getFloat(index);
    }

    @Override
    public float getFloatLE(int index) {
        return buf.getFloatLE(index);
    }

    @Override
    public double getDouble(int index) {
        return buf.getDouble(index);
    }

    @Override
    public double getDoubleLE(int index) {
        return buf.getDoubleLE(index);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int length) {
        return buf.getBytes(index, dst, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        return buf.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        return buf.getBytes(index, dst, dstIndex, length);
    }

    @Override
    public ByteBuf getBytes(int index, ByteBuffer dst) {
        return buf.getBytes(index, dst);
    }

    @Override
    public ByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return buf.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return buf.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return buf.getCharSequence(index, length, charset);
    }

    @Override
    public ByteBuf setBoolean(int index, boolean value) {
        return buf.setBoolean(index, value);
    }

    @Override
    public ByteBuf setByte(int index, int value) {
        return buf.setByte(index, value);
    }

    @Override
    public ByteBuf setShort(int index, int value) {
        return buf.setShort(index, value);
    }

    @Override
    public ByteBuf setShortLE(int index, int value) {
        return buf.setShortLE(index, value);
    }

    @Override
    public ByteBuf setMedium(int index, int value) {
        return buf.setMedium(index, value);
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        return buf.setMediumLE(index, value);
    }

    @Override
    public ByteBuf setInt(int index, int value) {
        return buf.setInt(index, value);
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        return buf.setIntLE(index, value);
    }

    @Override
    public ByteBuf setLong(int index, long value) {
        return buf.setLong(index, value);
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        return buf.setLongLE(index, value);
    }

    @Override
    public ByteBuf setChar(int index, int value) {
        return buf.setChar(index, value);
    }

    @Override
    public ByteBuf setFloat(int index, float value) {
        return buf.setFloat(index, value);
    }

    @Override
    public ByteBuf setFloatLE(int index, float value) {
        return buf.setFloatLE(index, value);
    }

    @Override
    public ByteBuf setDouble(int index, double value) {
        return buf.setDouble(index, value);
    }

    @Override
    public ByteBuf setDoubleLE(int index, double value) {
        return buf.setDoubleLE(index, value);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src) {
        return buf.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int length) {
        return buf.setBytes(index, src, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return buf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src) {
        return buf.setBytes(index, src);
    }

    @Override
    public ByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return buf.setBytes(index, src, srcIndex, length);
    }

    @Override
    public ByteBuf setBytes(int index, ByteBuffer src) {
        return buf.setBytes(index, src);
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return buf.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return buf.setBytes(index, in, position, length);
    }

    @Override
    public ByteBuf setZero(int index, int length) {
        return buf.setZero(index, length);
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return buf.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public short readShortLE() {
        return buf.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return buf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return buf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return buf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return buf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return buf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public int readIntLE() {
        return buf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return buf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public long readLongLE() {
        return buf.readLongLE();
    }

    @Override
    public char readChar() {
        return buf.readChar();
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public float readFloatLE() {
        return buf.readFloatLE();
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public double readDoubleLE() {
        return buf.readDoubleLE();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return buf.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return buf.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return buf.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        return buf.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        return buf.readBytes(dst, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        return buf.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        return buf.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return buf.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return buf.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        return buf.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return buf.readBytes(out, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return buf.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return buf.readBytes(out, position, length);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        return buf.skipBytes(length);
    }

    @Override
    public ByteBuf writeBoolean(boolean value) {
        return buf.writeBoolean(value);
    }

    @Override
    public ByteBuf writeByte(int value) {
        return buf.writeByte(value);
    }

    @Override
    public ByteBuf writeShort(int value) {
        return buf.writeShort(value);
    }

    @Override
    public ByteBuf writeShortLE(int value) {
        return buf.writeShortLE(value);
    }

    @Override
    public ByteBuf writeMedium(int value) {
        return buf.writeMedium(value);
    }

    @Override
    public ByteBuf writeMediumLE(int value) {
        return buf.writeMediumLE(value);
    }

    @Override
    public ByteBuf writeInt(int value) {
        return buf.writeInt(value);
    }

    @Override
    public ByteBuf writeIntLE(int value) {
        return buf.writeIntLE(value);
    }

    @Override
    public ByteBuf writeLong(long value) {
        return buf.writeLong(value);
    }

    @Override
    public ByteBuf writeLongLE(long value) {
        return buf.writeLongLE(value);
    }

    @Override
    public ByteBuf writeChar(int value) {
        return buf.writeChar(value);
    }

    @Override
    public ByteBuf writeFloat(float value) {
        return buf.writeFloat(value);
    }

    @Override
    public ByteBuf writeFloatLE(float value) {
        return buf.writeFloatLE(value);
    }

    @Override
    public ByteBuf writeDouble(double value) {
        return buf.writeDouble(value);
    }

    @Override
    public ByteBuf writeDoubleLE(double value) {
        return buf.writeDoubleLE(value);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src) {
        return buf.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int length) {
        return buf.writeBytes(src, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        return buf.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        return buf.writeBytes(src);
    }

    @Override
    public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        return buf.writeBytes(src, srcIndex, length);
    }

    @Override
    public ByteBuf writeBytes(ByteBuffer src) {
        return buf.writeBytes(src);
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return buf.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return buf.writeBytes(in, position, length);
    }

    @Override
    public ByteBuf writeZero(int length) {
        return buf.writeZero(length);
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return buf.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return buf.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return buf.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return buf.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return buf.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return buf.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return buf.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return buf.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return buf.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return buf.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return buf.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return buf.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return buf.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return buf.retainedSlice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return buf.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return buf.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return buf.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return buf.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return buf.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return buf.hasArray();
    }

    @Override
    public byte[] array() {
        return buf.array();
    }

    @Override
    public int arrayOffset() {
        return buf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return buf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return buf.memoryAddress();
    }

    @Override
    public boolean isContiguous() {
        return buf.isContiguous();
    }

    @Override
    public String toString(Charset charset) {
        return buf.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return buf.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return buf.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return buf.compareTo(buffer);
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    @Override
    public ByteBuf retain(int increment) {
        return buf.retain(increment);
    }

    @Override
    public ByteBuf retain() {
        return buf.retain();
    }

    @Override
    public ByteBuf touch() {
        return buf.touch();
    }

    @Override
    public ByteBuf touch(Object hint) {
        return buf.touch(hint);
    }

    @Override
    public int refCnt() {
        return buf.refCnt();
    }

    @Override
    public boolean release() {
        return buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return buf.release(decrement);
    }

    public static ByteMessage create() {
        return new ByteMessage(Unpooled.buffer());
    }
}
